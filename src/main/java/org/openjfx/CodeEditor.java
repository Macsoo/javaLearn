package org.openjfx;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class CodeEditor extends StackPane {
    TextFlow input;
    Button runButton;
    TextFlow output;
    JavaParser javaParser;
    String successOutput;

    @FXML
    String code;

    public void setCode(String code) {
        CompilationUnit unit = javaParser.parse(code).getResult().orElseThrow();
        List<Text> texts = new TextCreator().visit(unit, null);
        ObservableList<Node> inputChildren = input.getChildren();
        boolean skip = false;
        for (Text text: texts) {
            if (skip) {
                skip = false;
                continue;
            }
            if (text.getText().startsWith("TEXTLINE:")) {
                skip = true;
                inputChildren.add(getCodeInput(text.getText().substring(9), true));
            } else if (text.getText().startsWith("TEXTAREA:")) {
                skip = true;
                inputChildren.add(getCodeInput(text.getText().substring(9), false));
            } else if (text.getText().startsWith("SUCCESS:")) {
                successOutput = text.getText().substring(8);
            }
            else inputChildren.add(text);
        }
    }

    TextArea last = null;

    private TextArea getCodeInput(String placeholder, boolean restricted) {
        TextArea tf = new TextArea();
        if (restricted) {
            tf.setTextFormatter(new TextFormatter<>(change -> {
                if (change.isAdded()) {
                    if (change.getText().indexOf('\n') > -1) {
                        change.setText(change.getText().replace("\n", ""));
                        change.setCaretPosition(change.getCaretPosition() - 1);
                        change.setAnchor(change.getAnchor() - 1);
                    }
                    if (change.getText().indexOf('\t') > -1) {
                        change.setText(change.getText().replace("\t", ""));
                        change.setCaretPosition(change.getCaretPosition() - 1);
                        change.setAnchor(change.getAnchor() - 1);
                    }
                }
                return change;
            }));
            tf.setPrefRowCount(1);
            tf.prefColumnCountProperty().bind(tf.textProperty().length());
        } else {
            tf.setTextFormatter(new TextFormatter<>(change -> {
                if (change.isAdded()) {
                    if (change.getText().indexOf('\t') > -1) {
                        change.setText(change.getText().replace("\t", "    "));
                        change.setCaretPosition(change.getCaretPosition() + 3);
                        change.setAnchor(change.getAnchor() + 3);
                    }
                }
                return change;
            }));
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                String[] rows = newValue.split("\n", -1);
                tf.setPrefRowCount(rows.length);
                tf.setPrefColumnCount(Arrays.stream(rows).map(String::length).max(Integer::compareTo).orElseThrow());
            });
            tf.setUserData(true);
        }
        tf.setWrapText(true);
        tf.getStyleClass().add("code");
        tf.setPadding(new Insets(0));
        tf.maxWidth(Double.POSITIVE_INFINITY);
        tf.setText(placeholder);
        if (last != null && last.getUserData() == null) {
            last.setOnKeyPressed(key -> {
                if (key.getCode() != KeyCode.TAB) return;
                tf.requestFocus();
            });
        }
        last = tf;
        return tf;
    }

    void runHandle(ActionEvent x) {
        ObservableList<Node> children = output.getChildren();
        children.clear();
        Compiler.CompilationResult compilationResult = Compiler.compile(getCode());
        switch (compilationResult) {
            case Compiler.CompilationResult.Success success -> {
                Class<?> mainHolder = null;
                for (Class<?> compiledClass : success.classList()) {
                    if (Compiler.hasMainMethod(compiledClass)) {
                        if (mainHolder != null) {
                            Text err = new Text("Unambiguous main methods.");
                            err.getStyleClass().add("error");
                            children.add(err);
                            return;
                        }
                        mainHolder = compiledClass;
                    }
                }
                if (mainHolder == null) {
                    Text err = new Text("No class with main method.");
                    err.getStyleClass().add("error");
                    children.add(err);
                    return;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                System.setOut(new PrintStream(baos));
                Compiler.callMainMethod(mainHolder);
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                String out = baos.toString().stripTrailing().replace("\r\n", "\n");
                Text value = new Text(out);
                value.getStyleClass().add("code");
                children.add(value);
                if (out.equals(successOutput)) {
                    MainApp.next();
                }
            }
            case Compiler.CompilationResult.CompilationError compilationError -> {
                for (String err: compilationError.errorList()) {
                    Text line = new Text(err);
                    line.getStyleClass().add("error");
                    children.add(line);
                }
            }
            case Compiler.CompilationResult.UnsufficientRightsError unsufficientRightsError -> {
                Text err = new Text("Unsufficient rights: " + unsufficientRightsError.message());
                err.getStyleClass().add("error");
                children.add(err);
            }
            case Compiler.CompilationResult.NoPublicClass noPublicClass -> {
                Text err = new Text("No public class found.");
                err.getStyleClass().add("error");
                children.add(err);
            }
        }
    }

    public CodeEditor() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setBorder(new Border(new BorderStroke(
                Color.web("#ABB2BF"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10.0),
                BorderStroke.MEDIUM)));
        vBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        input = new TextFlow();
        runButton = new Button("Run");
        runButton.setOnAction(this::runHandle);
        output = new TextFlow();
        final ParserConfiguration conf = new ParserConfiguration();
        conf.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        javaParser = new JavaParser(conf);
        vBox.getChildren().add(input);
        vBox.getChildren().add(runButton);
        vBox.getChildren().add(output);
        getChildren().add(vBox);
    }

    public String getCode() {
        StringBuilder sb = new StringBuilder();
        for(Node child: input.getChildren()) {
            switch (child) {
                case Text t -> sb.append(t.getText());
                case TextArea tf -> sb.append(tf.getText());
                default -> {
                    return "";
                }
            }
        }
        return sb.toString();
    }
}
