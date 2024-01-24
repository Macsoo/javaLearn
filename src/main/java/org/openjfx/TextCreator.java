package org.openjfx;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.StringEscapeUtils;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TextCreator extends GenericListVisitorAdapter<Text, Void> {
    public TextCreator() {
        super();
    }

    int indent = 0;

    String newLine() {
        return "\n" + " ".repeat(indent);
    }

    enum Syntax {
        KEYWORD, TYPE, METHOD, PARAMETER, STRING, FIELD, NUMBER, COMMENT, ERROR;
        final String styleClass;

        Syntax() {
            this.styleClass = "syntax-" + toString().toLowerCase();
        }

        public String getStyleClass() {
            return styleClass;
        }
    }

    class Builder {
        List<Text> in;
        List<Text> out;
        Text current;

        Builder(List<Text> in) {
            this.in = in;
            this.out = new ArrayList<>();
        }

        Builder() {
            this.out = new ArrayList<>();
        }

        Builder append(String text, boolean andSpace) {
            if (current != null) out.add(current);
            current = new Text(text + (andSpace ? " " : ""));
            current.getStyleClass().add("code");
            return this;
        }

        Builder acceptList(
                NodeList<? extends Visitable> nodeList,
                String separator,
                String preText, Syntax preSyntax,
                String postText)
        {
            if (nodeList == null || nodeList.isEmpty()) return this;
            if (preText != null) {
                append(preText, false);
                if (preSyntax != null)
                    style(preSyntax);
            }
            int len = nodeList.size();
            if (separator != null) for (int i = 0; i < len; i++) {
                accept(nodeList.get(i));
                if (i != len - 1) append(separator, false);
            }
            else for (int i = 0; i < len; i++) {
                accept(nodeList.get(i));
            }
            if (postText != null) {
                append(postText, false);
            }
            return this;
        }

        Builder space() {
            if (current != null) {
                current.setText(current.getText() + " ");
            } else {
                current = new Text(" ");
            }
            current.getStyleClass().add("code");
            return this;
        }

        Builder appendAll(List<Text> text) {
            if (current != null) out.add(current);
            current = null;
            out.addAll(text);
            return this;
        }

        Builder accept(Visitable node) {
            return appendAll(node.accept(TextCreator.this, null));
        }

        Builder append(String text) {
            return append(text, true);
        }

        Builder blockStart() {
            if (current != null) out.add(current);
            current = null;
            indent += 4;
            return append("{", false).append(newLine(), false);
        }

        Builder blockEnd() {
            if (current != null) out.add(current);
            current = null;
            indent -= 4;
            return append(newLine(), false)
                    .append("}", false);
        }

        Builder thenIf(boolean predicate, Function<Builder, Builder> f) {
            if (!predicate) return this;
            return f.apply(this);
        }

        Builder innerStatement(Statement statement) {
            if (statement.isBlockStmt()) return accept(statement);
            indent += 4;
            append(newLine()).accept(statement);
            indent -= 4;
            return this;
        }

        Builder semicolon() {
            return append(";", false);
        }

        Builder style(Syntax syntax) {
            if (current != null) current.getStyleClass().add(syntax.getStyleClass());
            return this;
        }

        List<Text> build() {
            if (current != null) out.add(current);
            return out;
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        Builder acceptIf(Optional<? extends Visitable> comment) {
            comment.ifPresent(this::accept);
            return this;
        }

        Builder appendIf(boolean predicate, String text, Syntax syntax) {
            if (!predicate) return this;
            if(text != null) {
                append(text, false);
                if (syntax != null)
                    style(syntax);
            }
            return this;
        }
    }

    @Override
    public List<Text> visit(AnnotationDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .append("@", false)
                .append("interface")
                .style(Syntax.KEYWORD)
                .append(n.getNameAsString())
                .style(Syntax.TYPE)
                .blockStart()
                .acceptList(n.getMembers(), newLine(),
                        null, null, newLine())
                .blockEnd()
                .build();
    }

    @Override
    public List<Text> visit(AnnotationMemberDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .accept(n.getType())
                .append(" " + n.getNameAsString(), false)
                .style(Syntax.METHOD)
                .append("()", false)
                .appendIf(n.getDefaultValue().isPresent(), " default ", Syntax.KEYWORD)
                .acceptIf(n.getDefaultValue())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(ArrayAccessExpr n, Void arg) {
        return new Builder(super.visit(n, arg))
                .acceptIf(n.getComment())
                .accept(n.getName())
                .append("[", false)
                .accept(n.getIndex())
                .append("]", false)
                .build();
    }

    @Override
    public List<Text> visit(ArrayCreationExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("new")
                .style(Syntax.KEYWORD)
                .accept(n.getElementType())
                .acceptList(n.getLevels(), null,
                        null, null, null)
                .appendIf(n.getInitializer().isPresent(), " ", null)
                .acceptIf(n.getInitializer())
                .build();
    }

    @Override
    public List<Text> visit(ArrayCreationLevel n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        " ", null, null)
                .append("[", false)
                .acceptIf(n.getDimension())
                .append("]", false)
                .build();
    }

    @Override
    public List<Text> visit(ArrayInitializerExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("{", false)
                .acceptList(n.getValues(), ", ",
                        null, null, null)
                .append("}", false)
                .build();
    }

    @Override
    public List<Text> visit(ArrayType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getComponentType())
                .acceptList(n.getAnnotations(), " ",
                        " ", null, null)
                .append("[]", false)
                .build();
    }

    @Override
    public List<Text> visit(AssertStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("assert")
                .style(Syntax.KEYWORD)
                .accept(n.getCheck())
                .appendIf(n.getMessage().isPresent(), " : ", null)
                .acceptIf(n.getMessage())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(AssignExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getTarget())
                .append(" " + n.getOperator().asString())
                .accept(n.getValue())
                .build();
    }

    @Override
    public List<Text> visit(BinaryExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getLeft())
                .append(" " + n.getOperator().asString())
                .accept(n.getRight())
                .build();
    }

    @Override
    public List<Text> visit(BlockComment n, Void arg) {
        String c = n.getContent();
        if (c.startsWith("${") && c.endsWith("}$"))
            return List.of(new Text("TEXTLINE:" + n.getContent().substring(2, c.length() - 2)));
        if (c.startsWith("#{") && c.endsWith("}#"))
            return List.of(new Text("SUCCESS:" + n.getContent().substring(2, c.length() - 2)));
        if (c.startsWith("={") && c.endsWith("}="))
            return List.of(new Text("TEXTAREA:" + n.getContent().substring(2, c.length() - 2)));
        return new Builder()
                .acceptIf(n.getComment())
                .append("/*", false)
                .style(Syntax.COMMENT)
                .append(c, false)
                .style(Syntax.COMMENT)
                .append("*/")
                .style(Syntax.COMMENT)
                .build();
    }

    @Override
    public List<Text> visit(BlockStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .blockStart()
                .acceptList(n.getStatements(), newLine(),
                        null, null, null)
                .blockEnd()
                .build();
    }

    @Override
    public List<Text> visit(BooleanLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.toString(), false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(BreakStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("break", n.getLabel().isPresent())
                .style(Syntax.KEYWORD)
                .acceptIf(n.getLabel())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(CastExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("(", false)
                .accept(n.getType())
                .append(")", false)
                .accept(n.getExpression())
                .build();
    }

    @Override
    public List<Text> visit(CatchClause n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("catch")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getParameter())
                .append(")")
                .accept(n.getBody())
                .build();
    }

    @Override
    public List<Text> visit(CharLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("'" + n.asChar() + "'", false)
                .style(Syntax.STRING)
                .build();
    }

    @Override
    public List<Text> visit(ClassExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getType())
                .append(".", false)
                .append("class", false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(ClassOrInterfaceDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .append(n.isInterface() ? "interface" : "class")
                .style(Syntax.KEYWORD)
                .append(n.getNameAsString(), n.getTypeParameters().isEmpty())
                .style(Syntax.TYPE)
                .acceptList(n.getTypeParameters(), ", ",
                        "<", null, "> ")
                .acceptList(n.getExtendedTypes(), ", ",
                        "extends ", Syntax.KEYWORD, " ")
                .acceptList(n.getImplementedTypes(), ", ",
                        "implements ", Syntax.KEYWORD, " ")
                .acceptList(n.getPermittedTypes(), ", ",
                        "permits ", Syntax.KEYWORD, " ")
                .blockStart()
                .acceptList(n.getMembers(), newLine(),
                        null, null, null)
                .blockEnd()
                .build();
    }

    @Override
    public List<Text> visit(ClassOrInterfaceType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append(n.getNameWithScope(), false)
                .style(Syntax.TYPE)
                .thenIf(n.getTypeArguments().isPresent(), B -> B
                        .append("<", false)
                        .acceptList(n.getTypeArguments().get(), ", ",
                                null, null, null)
                        .append(">", false))
                .build();
    }

    @Override
    public List<Text> visit(CompilationUnit n, Void arg) {
        return new Builder()
                .acceptIf(n.getPackageDeclaration())
                .appendIf(n.getPackageDeclaration().isPresent(), newLine() + newLine(), null)
                .acceptList(n.getImports(), newLine(),
                        null, null, newLine())
                .appendIf(!n.getImports().isEmpty(), newLine(), null)
                .acceptList(n.getTypes(), newLine(),
                        null, null, null)
                .build();
    }

    @Override
    public List<Text> visit(ConditionalExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getCondition())
                .append(" ?")
                .accept(n.getThenExpr())
                .append(" :")
                .accept(n.getElseExpr())
                .build();
    }

    @Override
    public List<Text> visit(ConstructorDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .acceptList(n.getTypeParameters(), ", ",
                        "<", null, "> ")
                .append(n.getNameAsString(), false)
                .append("(", false)
                .thenIf(n.getReceiverParameter().isPresent(), B -> B
                        .accept(n.getReceiverParameter().get())
                        .appendIf(!n.getParameters().isEmpty(), ", ", null))
                .acceptList(n.getParameters(), ", ",
                null, null, null)
                .append(")")
                .acceptList(n.getThrownExceptions(), ", ",
                        "throws ", Syntax.KEYWORD, " ")
                .accept(n.getBody()).build();
    }

    @Override
    public List<Text> visit(ContinueStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("continue", n.getLabel().isPresent())
                .style(Syntax.KEYWORD)
                .acceptIf(n.getLabel())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(DoStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("do")
                .style(Syntax.KEYWORD)
                .innerStatement(n.getBody())
                .appendIf(!n.getBody().isBlockStmt(), newLine(), null)
                .thenIf(n.getBody().isBlockStmt(), Builder::space)
                .append("while")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getCondition())
                .append(")", false)
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(DoubleLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getValue(), false)
                .style(Syntax.NUMBER)
                .build();
    }

    @Override
    public List<Text> visit(EmptyStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(EnclosedExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("(", false)
                .accept(n.getInner())
                .append(")", false)
                .build();
    }

    @Override
    public List<Text> visit(EnumConstantDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append(n.getNameAsString(), false)
                .style(Syntax.PARAMETER)
                .acceptList(n.getArguments(), ", ",
                        "(", null, ")")
                .thenIf(!n.getClassBody().isEmpty(), Builder::blockStart)
                .acceptList(n.getClassBody(), newLine(),
                        null, null, null)
                .thenIf(!n.getClassBody().isEmpty(), Builder::blockEnd)
                .build();
    }

    @Override
    public List<Text> visit(EnumDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .append("enum")
                .style(Syntax.KEYWORD)
                .append(n.getNameAsString())
                .style(Syntax.TYPE)
                .acceptList(n.getImplementedTypes(), ", ",
                        "implements ", Syntax.KEYWORD, " ")
                .blockStart()
                .acceptList(n.getEntries(), "," + newLine(),
                        null, null, null)
                .acceptList(n.getMembers(), newLine(),
                        ";" + newLine(), null, null)
                .blockEnd()
                .build();
    }

    @Override
    public List<Text> visit(ExplicitConstructorInvocationStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .thenIf(n.getTypeArguments().isPresent(), B -> B
                        .acceptList(n.getTypeArguments().get(), ", ",
                                "<", null, "> "))
                .thenIf(n.getExpression().isPresent() && !n.isThis(), B -> B
                        .accept(n.getExpression().get())
                        .append(".", false))
                .append(n.isThis() ? "this" : "super", false)
                .style(Syntax.KEYWORD)
                .append("(", false)
                .acceptList(n.getArguments(), ", ",
                        null, null, null)
                .append(")", false)
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(ExpressionStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getExpression())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(FieldAccessExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getScope())
                .append(".", false)
                .append(n.getNameAsString(), false)
                .style(Syntax.FIELD)
                .build();
    }

    @Override
    public List<Text> visit(FieldDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .accept(n.getVariables().getFirst().orElseThrow().getType())
                .acceptList(n.getVariables(), ", ",
                        " ", null, null)
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(ForStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("for")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .acceptList(n.getInitialization(), ", ",
                        null, null, null)
                .append(";", n.getCompare().isPresent())
                .acceptIf(n.getCompare())
                .semicolon()
                .acceptList(n.getUpdate(), ", ",
                        " ", null, null)
                .append(")")
                .innerStatement(n.getBody())
                .build();
    }

    @Override
    public List<Text> visit(ForEachStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("for")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getVariable())
                .append(":")
                .accept(n.getIterable())
                .append(")")
                .innerStatement(n.getBody())
                .build();
    }

    @Override
    public List<Text> visit(IfStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("if")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getCondition())
                .append(")")
                .innerStatement(n.getThenStmt())
                .appendIf(!n.getThenStmt().isBlockStmt(), newLine(), null)
                .thenIf(n.getThenStmt().isBlockStmt(), Builder::space)
                .appendIf(n.getElseStmt().isPresent(), "else ", Syntax.KEYWORD)
                .thenIf(n.getElseStmt().isPresent(), B -> B.innerStatement(n.getElseStmt().get()))
                .build();
    }

    @Override
    public List<Text> visit(ImportDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("import")
                .style(Syntax.KEYWORD)
                .appendIf(n.isStatic(), "static ", Syntax.KEYWORD)
                .append(n.getNameAsString(), false)
                .style(Syntax.TYPE)
                .appendIf(n.isAsterisk(), ".*", Syntax.TYPE)
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(InitializerDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .appendIf(n.isStatic(), "static ", Syntax.KEYWORD)
                .accept(n.getBody())
                .build();
    }

    @Override
    public List<Text> visit(InstanceOfExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getExpression())
                .append(" instanceof")
                .style(Syntax.KEYWORD)
                .accept(n.getType())
                .acceptIf(n.getPattern())
                .build();
    }

    @Override
    public List<Text> visit(IntegerLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getValue(), false)
                .style(Syntax.NUMBER)
                .build();
    }

    @Override
    public List<Text> visit(IntersectionType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .acceptList(n.getElements(), " & ",
                        null, null, null)
                .build();
    }

    @Override
    public List<Text> visit(JavadocComment n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("/**", false)
                .style(Syntax.COMMENT)
                .append(n.getContent(), false)
                .style(Syntax.COMMENT)
                .append("*/")
                .style(Syntax.COMMENT)
                .build();
    }

    @Override
    public List<Text> visit(LabeledStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getLabel().asString() + ":")
                .accept(n.getStatement())
                .build();
    }

    @Override
    public List<Text> visit(LambdaExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .appendIf(n.isEnclosingParameters(), "(", null)
                .acceptList(n.getParameters(), ", ",
                        null, null, null)
                .appendIf(n.isEnclosingParameters(), ")", null)
                .append(" ->")
                .thenIf(n.getBody() instanceof ExpressionStmt, B -> B
                        .accept(((ExpressionStmt)n.getBody()).getExpression()))
                .thenIf(n.getBody() instanceof BlockStmt, B -> B
                        .accept(n.getBody()))
                .build();
    }

    @Override
    public List<Text> visit(LineComment n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("//", false)
                .style(Syntax.COMMENT)
                .append(n.getContent(), false)
                .style(Syntax.COMMENT)
                .append(newLine(), false)
                .build();
    }

    @Override
    public List<Text> visit(LocalClassDeclarationStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getClassDeclaration())
                .build();
    }

    @Override
    public List<Text> visit(LocalRecordDeclarationStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getRecordDeclaration())
                .build();
    }

    @Override
    public List<Text> visit(LongLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getValue(), false)
                .style(Syntax.NUMBER)
                .build();
    }

    @Override
    public List<Text> visit(MarkerAnnotationExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("@" + n.getNameAsString(), false)
                .style(Syntax.TYPE)
                .build();
    }

    @Override
    public List<Text> visit(MemberValuePair n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getNameAsString())
                .style(Syntax.PARAMETER)
                .append("=")
                .accept(n.getValue())
                .build();
    }

    @Override
    public List<Text> visit(MethodCallExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptIf(n.getScope())
                .appendIf(n.getScope().isPresent(), ".", null)
                .thenIf(n.getTypeArguments().isPresent(), B -> B
                        .append("<", false)
                        .acceptList(n.getTypeArguments().get(), ", ",
                                null, null, null)
                        .append(">", false))
                .append(n.getNameAsString(), false)
                .style(Syntax.METHOD)
                .append("(", false)
                .acceptList(n.getArguments(), ", ",
                        null, null, null)
                .append(")", false)
                .build();
    }

    @Override
    public List<Text> visit(MethodDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .acceptList(n.getTypeParameters(), ", ",
                        "<", null, "> ")
                .accept(n.getType())
                .space()
                .append(n.getNameAsString(), false)
                .style(Syntax.METHOD)
                .append("(", false)
                .acceptIf(n.getReceiverParameter())
                .appendIf(n.getReceiverParameter().isPresent() && !n.getParameters().isEmpty(),
                        ", ", null)
                .acceptList(n.getParameters(), ", ",
                        null, null, null)
                .append(")", n.getBody().isPresent() || !n.getThrownExceptions().isEmpty())
                .acceptList(n.getThrownExceptions(), ", ",
                        "throws ", Syntax.KEYWORD, n.getBody().isPresent() ? " " : "")
                .appendIf(n.getBody().isEmpty(), ";", null)
                .acceptIf(n.getBody())
                .build();
    }

    @Override
    public List<Text> visit(MethodReferenceExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getScope())
                .append("::", false)
                .thenIf(n.getTypeArguments().isPresent(), B -> B
                        .append("<", false)
                        .acceptList(n.getTypeArguments().get(), ", ",
                                null, null, null)
                        .append(">", false))
                .append(n.getIdentifier(), false)
                .style(n.getIdentifier().equals("new") ? Syntax.KEYWORD : Syntax.METHOD)
                .build();
    }

    @Override
    public List<Text> visit(NameExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getNameAsString(), false)
                .build();
    }

    @Override
    public List<Text> visit(Name n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptIf(n.getQualifier())
                .appendIf(n.getQualifier().isPresent(), ".", null)
                .append(n.getIdentifier(), false)
                .build();
    }

    @Override
    public List<Text> visit(NormalAnnotationExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("@" + n.getNameAsString() + "(", false)
                .style(Syntax.TYPE)
                .acceptList(n.getPairs(), ", ",
                        null, null, null)
                .append(")", false)
                .style(Syntax.TYPE)
                .build();
    }

    @Override
    public List<Text> visit(NullLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("null", false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(ObjectCreationExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptIf(n.getScope())
                .appendIf(n.getScope().isPresent(), ".", null)
                .append("new")
                .style(Syntax.KEYWORD)
                .accept(n.getType())
                .thenIf(n.getTypeArguments().isPresent(), B -> B
                        .append("<", false)
                        .acceptList(n.getTypeArguments().get(), ", ",
                                null, null, null)
                        .append(">", false))
                .append("(", false)
                .acceptList(n.getArguments(), ", ",
                        null, null, null)
                .append(")", false)
                .thenIf(n.getAnonymousClassBody().isPresent(), B -> B
                        .space()
                        .blockStart()
                        .acceptList(n.getAnonymousClassBody().get(), newLine(),
                                null, null, null)
                        .blockEnd())
                .build();
    }

    @Override
    public List<Text> visit(PackageDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append("package")
                .style(Syntax.KEYWORD)
                .append(n.getNameAsString(), false)
                .style(Syntax.TYPE)
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(Parameter n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .accept(n.getType())
                .thenIf(n.isVarArgs(), B -> B
                        .acceptList(n.getVarArgsAnnotations(), " ",
                                " ", null, null)
                        .append("...", false))
                .appendIf(!n.getType().isUnknownType(), " ", null)
                .accept(n.getName())
                .build();
    }

    @Override
    public List<Text> visit(PrimitiveType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append(n.asString(), false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(ReturnStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("return", n.getExpression().isPresent())
                .style(Syntax.KEYWORD)
                .acceptIf(n.getExpression())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(SimpleName n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getIdentifier(), false)
                .build();
    }

    @Override
    public List<Text> visit(SingleMemberAnnotationExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("@" + n.getNameAsString() + "(", false)
                .style(Syntax.TYPE)
                .accept(n.getMemberValue())
                .append(")", false)
                .style(Syntax.TYPE)
                .build();
    }

    @Override
    public List<Text> visit(StringLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("\"" + StringEscapeUtils.escapeJava(n.asString()) + "\"", false)
                .style(Syntax.STRING)
                .build();
    }

    @Override
    public List<Text> visit(SuperExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .thenIf(n.getTypeName().isPresent(), B -> B
                        .append(name(n.getTypeName().get()), false)
                        .style(Syntax.TYPE)
                        .append(".", false))
                .append("super", false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(SwitchEntry n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .appendIf(!n.getLabels().isEmpty(), "case ", Syntax.KEYWORD)
                .acceptList(n.getLabels(), ", ",
                        null, null, null)
                .appendIf(n.getLabels().isEmpty(), "default", Syntax.KEYWORD)
                .thenIf(true, B -> {
                    switch (n.getType()) {
                        case STATEMENT_GROUP -> {
                            B.append(":", false);
                            if (!n.getStatements().isEmpty()) {
                                indent += 4;
                                B.append(newLine(), false)
                                        .acceptList(n.getStatements(), newLine(),
                                        null, null, null);
                                indent -= 4;
                            }
                        }
                        case EXPRESSION, THROWS_STATEMENT -> B.append(" ->").accept(n.getStatement(0));
                        case BLOCK -> B.append(" ->").innerStatement(n.getStatement(0));
                    }
                    return B;
                })
                .build();
    }

    @Override
    public List<Text> visit(SwitchStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("switch")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getSelector())
                .append(")")
                .blockStart()
                .acceptList(n.getEntries(), newLine(),
                        null, null, null)
                .blockEnd()
                .build();
    }

    @Override
    public List<Text> visit(SynchronizedStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("synchronized")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getExpression())
                .append(")")
                .accept(n.getBody())
                .build();
    }

    @Override
    public List<Text> visit(ThisExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .thenIf(n.getTypeName().isPresent(), B -> B
                        .append(name(n.getTypeName().get()), false)
                        .style(Syntax.TYPE)
                        .append(".", false))
                .append("this", false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(ThrowStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("throw")
                .style(Syntax.KEYWORD)
                .accept(n.getExpression())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(TryStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("try")
                .style(Syntax.KEYWORD)
                .acceptList(n.getResources(), ", ",
                        "(", null, ") ")
                .accept(n.getTryBlock())
                .acceptList(n.getCatchClauses(), " ",
                        " ", null, null)
                .appendIf(n.getFinallyBlock().isPresent(), " finally ", Syntax.KEYWORD)
                .acceptIf(n.getFinallyBlock())
                .build();
    }

    @Override
    public List<Text> visit(TypeExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .accept(n.getType())
                .build();
    }

    @Override
    public List<Text> visit(TypeParameter n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append(n.getNameAsString(), false)
                .style(Syntax.TYPE)
                .acceptList(n.getTypeBound(), " & ",
                        " extends ", Syntax.KEYWORD, null)
                .build();
    }

    @Override
    public List<Text> visit(UnaryExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .appendIf(n.getOperator().isPrefix(), n.getOperator().asString(), null)
                .accept(n.getExpression())
                .appendIf(n.getOperator().isPostfix(), n.getOperator().asString(), null)
                .build();
    }

    @Override
    public List<Text> visit(UnionType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getElements(), " | ",
                        null, null, null)
                .build();
    }

    @Override
    public List<Text> visit(UnknownType n, Void arg) {
        return List.of();
    }

    @Override
    public List<Text> visit(VariableDeclarationExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .accept(n.getVariable(0).getType())
                .space()
                .acceptList(n.getVariables(), ", ",
                        null, null, null)
                .build();
    }

    @Override
    public List<Text> visit(VariableDeclarator n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getNameAsString(), false)
                .appendIf(n.getInitializer().isPresent(), " = ", null)
                .acceptIf(n.getInitializer())
                .build();
    }

    @Override
    public List<Text> visit(VoidType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append("void", false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(WhileStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("while")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getCondition())
                .append(")")
                .innerStatement(n.getBody())
                .build();
    }

    @Override
    public List<Text> visit(WildcardType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append("?", false)
                .thenIf(n.getExtendedType().isPresent(), B -> B
                        .append(" extends")
                        .style(Syntax.KEYWORD)
                        .accept(n.getExtendedType().get())
                )
                .thenIf(n.getSuperType().isPresent(), B -> B
                        .append(" super")
                        .style(Syntax.KEYWORD)
                        .accept(n.getSuperType().get())
                )
                .build();
    }

    @Override
    public List<Text> visit(NodeList n, Void arg) {
        throw new RuntimeException("NodeList accepted!");
    }

    @Override
    public List<Text> visit(ModuleDeclaration n, Void arg) {
        return new Builder().build();
    }

    @Override
    public List<Text> visit(ModuleExportsDirective n, Void arg) {
        return new Builder().build();
    }

    @Override
    public List<Text> visit(ModuleOpensDirective n, Void arg) {
        return new Builder().build();
    }

    @Override
    public List<Text> visit(ModuleProvidesDirective n, Void arg) {
        return new Builder().build();
    }

    @Override
    public List<Text> visit(ModuleRequiresDirective n, Void arg) {
        return new Builder().build();
    }

    @Override
    public List<Text> visit(ModuleUsesDirective n, Void arg) {
        return new Builder().build();
    }

    @Override
    public List<Text> visit(UnparsableStmt n, Void arg) {
        return new Builder()
                .append(n.toString())
                .style(Syntax.ERROR)
                .build();
    }

    static String name(Name name) {
        String s = "";
        if (name.getQualifier().isPresent())
            s += name(name.getQualifier().get()) + ".";
        return s + name.getIdentifier();
    }

    @Override
    public List<Text> visit(ReceiverParameter n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .accept(n.getType())
                .space()
                .thenIf(n.getName().getQualifier().isPresent(), B -> B
                        .append(name(n.getName().getQualifier().get()), false)
                        .style(Syntax.TYPE)
                        .append(".", false))
                .append("this", false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(VarType n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), " ",
                        null, null, " ")
                .append("var", false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(Modifier n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append(n.getKeyword().asString(), false)
                .style(Syntax.KEYWORD)
                .build();
    }

    @Override
    public List<Text> visit(SwitchExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("switch")
                .style(Syntax.KEYWORD)
                .append("(", false)
                .accept(n.getSelector())
                .append(")")
                .blockStart()
                .acceptList(n.getEntries(), newLine(),
                        null, null, null)
                .blockEnd()
                .build();
    }

    @Override
    public List<Text> visit(YieldStmt n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("yield")
                .style(Syntax.KEYWORD)
                .accept(n.getExpression())
                .semicolon()
                .build();
    }

    @Override
    public List<Text> visit(TextBlockLiteralExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .append("\"\"\"" + newLine(), false)
                .style(Syntax.STRING)
                .append(n.asString(), false)
                .style(Syntax.STRING)
                .append("\"\"\"", false)
                .style(Syntax.STRING)
                .build();
    }

    @Override
    public List<Text> visit(PatternExpr n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .accept(n.getType())
                .space()
                .append(n.getNameAsString(), false)
                .build();
    }

    @Override
    public List<Text> visit(RecordDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .append("record")
                .style(Syntax.KEYWORD)
                .append(n.getNameAsString(), false)
                .style(Syntax.TYPE)
                .acceptList(n.getTypeParameters(), ", ",
                        "<", null, ">")
                .append("(", false)
                .acceptList(n.getParameters(), ", ",
                        null, null, null)
                .append(")")
                .acceptList(n.getImplementedTypes(), ", ",
                        "implements", Syntax.KEYWORD, " ")
                .blockStart()
                .acceptList(n.getMembers(), newLine(),
                        null, null, null)
                .blockEnd()
                .build();
    }

    @Override
    public List<Text> visit(CompactConstructorDeclaration n, Void arg) {
        return new Builder()
                .acceptIf(n.getComment())
                .acceptList(n.getAnnotations(), newLine(),
                        null, null, newLine())
                .acceptList(n.getModifiers(), " ",
                        null, null, " ")
                .acceptList(n.getTypeParameters(), ", ",
                        "<", null, "> ")
                .append(n.getNameAsString())
                .acceptList(n.getThrownExceptions(), ", ",
                        "throws ", Syntax.KEYWORD, " ")
                .accept(n.getBody()).build();
    }
}
