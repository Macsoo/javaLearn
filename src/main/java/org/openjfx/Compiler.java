package org.openjfx;

import javax.tools.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Compiler {

    public sealed interface CompilationResult {
        record Success(List<Class<?>> classList) implements CompilationResult {
        }

        record CompilationError(List<String> errorList) implements CompilationResult {
        }

        record UnsufficientRightsError(String message) implements CompilationResult {
        }

        record NoPublicClass() implements CompilationResult {
        }
    }

    public static CompilationResult compile(String... targets) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StringWriter compilerOutput = new StringWriter();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                diagnostics,
                null,
                null);
        List<Class<?>> compiledClasses = new ArrayList<>();
        File folder = new File("compilation");
        if (!folder.exists()) {
            if (!folder.mkdir())
                return new CompilationResult.UnsufficientRightsError("Cannot create compilation folder");
        } else for (File file : Objects.requireNonNull(folder.listFiles()))
            if (!file.delete())
                return new CompilationResult.UnsufficientRightsError("Cannot clear compilation directory");
        for (String target : targets) {
            Pattern pattern = Pattern.compile("public\\sclass\\s([A-Za-z]+)");
            Matcher matcher = pattern.matcher(target);
            String fileName;
            if (matcher.find())
                fileName = matcher.group(1);
            else return new CompilationResult.NoPublicClass();
            File testJava = new File("compilation/" + fileName + ".java");
            try {
                try (FileWriter fileWriter = new FileWriter(testJava)) {
                    fileWriter.write(target);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(List.of(testJava));
            JavaCompiler.CompilationTask compilationTask = compiler.getTask(
                    compilerOutput,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    compilationUnit
            );

            if (!compilationTask.call()) {
                List<String> errors = new ArrayList<>();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    errors.add("Error on line %d:%n%s%n".formatted(
                            diagnostic.getLineNumber(),
                            diagnostic.getMessage(null)));
                }
                return new CompilationResult.CompilationError(errors);
            }
        }
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{folder.toURI().toURL()})) {
            for (File classFile : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".class"))))
                compiledClasses.add(classLoader.loadClass(classFile.getName().split("\\.")[0]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new CompilationResult.Success(compiledClasses);
    }

    public enum MethodVisibility {
        PRIVATE,
        PROTECTED,
        PUBLIC,
        PACKAGE_PRIVATE
    }

    /**
     * The possible result of a method call.
     */
    public sealed interface MethodCallResult {
        /**
         * The method run successfully.
         *
         * @param result The return value of the method call, or {@code null} if the method is void.
         */
        record Success(Object result) implements MethodCallResult {
        }

        /**
         * The method with the given name and parameter types wasn't found in the class.
         *
         * @param signature The method signature that was searched.
         */
        record NoSuchMethod(String signature) implements MethodCallResult {
            NoSuchMethod(String name, Object... params) {
                this(name +
                     "(" + Arrays.stream(params)
                             .map(Object::getClass)
                             .map(Class::toGenericString)
                             .collect(Collectors.joining(", ")) + ")");
            }
        }

        /**
         * The method was found, but under the visibility rules couldn't be called.
         *
         * @param visibility The visibility of the called method.
         */
        record IllegalAccess(MethodVisibility visibility) implements MethodCallResult {
            IllegalAccess(int methodModifiers) {
                this(
                        Modifier.isPrivate(methodModifiers) ? MethodVisibility.PRIVATE :
                                Modifier.isProtected(methodModifiers) ? MethodVisibility.PROTECTED :
                                        Modifier.isPublic(methodModifiers) ? MethodVisibility.PUBLIC :
                                                MethodVisibility.PACKAGE_PRIVATE
                );
            }
        }

        /**
         * The method call resulted in an exception being thrown.
         *
         * @param exception The thrown exception.
         */
        record InnerException(Throwable exception) implements MethodCallResult {
        }

        /**
         * The method is an instance method, but it was called as if it was a static one.
         */
        record InstanceMethod() implements MethodCallResult {
        }
    }

    /**
     * The possible result of a constructor call.
     */
    public sealed interface ConstructorCallResult {
        /**
         * The constructor run successfully.
         *
         * @param newInstance The created instance.
         */
        record Success(Object newInstance) implements ConstructorCallResult {
        }

        /**
         * The constructor with the given parameter types wasn't found in the class.
         *
         * @param signature The constructor signature that was searched.
         */
        record NoSuchConstructor(String signature) implements ConstructorCallResult {
            NoSuchConstructor(String className, Object... params) {
                this("new " + className + "(" + Arrays.stream(params)
                        .map(Object::getClass)
                        .map(Class::toGenericString)
                        .collect(Collectors.joining(", ")) + ")");
            }
        }

        /**
         * The constructor was found, but under the visibility rules couldn't be called.
         *
         * @param visibility The visibility of the called constructor.
         */
        record IllegalAccess(MethodVisibility visibility) implements ConstructorCallResult {
            IllegalAccess(int methodModifiers) {
                this(
                        Modifier.isPrivate(methodModifiers) ? MethodVisibility.PRIVATE :
                                Modifier.isProtected(methodModifiers) ? MethodVisibility.PROTECTED :
                                        Modifier.isPublic(methodModifiers) ? MethodVisibility.PUBLIC :
                                                MethodVisibility.PACKAGE_PRIVATE
                );
            }
        }

        /**
         * The constructor call resulted in an exception being thrown.
         *
         * @param exception The thrown exception.
         */
        record InnerException(Throwable exception) implements ConstructorCallResult {
        }

        /**
         * A constructor couldn't be called for the given class, possibly because it's an abstract class or an interface,
         * or it lacks a default constructor.
         */
        record NotInstantiable() implements ConstructorCallResult {
        }
    }

    public static boolean hasMainMethod(Class<?> compiledClass) {
        try {
            Method method = compiledClass.getMethod("main", String.class.arrayType());
            if (method.getReturnType() != void.class)
                return false;
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers))
                return false;
            if (!Modifier.isStatic(modifiers))
                return false;
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static MethodCallResult callMainMethod(Class<?> compiledClass) {
        Method method;
        try {
            method = compiledClass.getMethod("main", String.class.arrayType());
            if (method.getReturnType() != void.class)
                throw new NoSuchMethodException();
        } catch (NoSuchMethodException e) {
            return new MethodCallResult.NoSuchMethod("public static void main(String[])");
        }
        try {
            return new MethodCallResult.Success(method.invoke(null, (Object) new String[]{}));
        } catch (InvocationTargetException e) {
            return new MethodCallResult.InnerException(e.getTargetException());
        } catch (IllegalAccessException | NullPointerException e) {
            return new MethodCallResult.NoSuchMethod("public static void main(String[])");
        }
    }

    public static MethodCallResult callStaticMethod(Class<?> compiledClass, String methodName, Object... params) {
        Method method;
        try {
            Class<?>[] parameterClasses = objectsToClasses(params);
            method = compiledClass.getMethod(methodName, parameterClasses);
        } catch (NoSuchMethodException e) {
            return new MethodCallResult.NoSuchMethod(methodName, params);
        }
        try {
            return new MethodCallResult.Success(method.invoke(null, params));
        } catch (InvocationTargetException e) {
            return new MethodCallResult.InnerException(e.getTargetException());
        } catch (IllegalAccessException e) {
            return new MethodCallResult.IllegalAccess(method.getModifiers());
        } catch (NullPointerException e) {
            return new MethodCallResult.InstanceMethod();
        }
    }

    public static MethodCallResult callInstanceMethod(Object instance, String methodName, Object... params) {
        Class<?> compiledClass = instance.getClass();
        Method method;
        try {
            Class<?>[] parameterClasses = objectsToClasses(params);
            method = compiledClass.getMethod(methodName, parameterClasses);
        } catch (NoSuchMethodException e) {
            return new MethodCallResult.NoSuchMethod(methodName, params);
        }
        try {
            return new MethodCallResult.Success(method.invoke(instance, params));
        } catch (InvocationTargetException e) {
            return new MethodCallResult.InnerException(e.getTargetException());
        } catch (IllegalAccessException e) {
            return new MethodCallResult.IllegalAccess(method.getModifiers());
        } catch (NullPointerException e) {
            return new MethodCallResult.InstanceMethod();
        }
    }

    private static Class<?>[] objectsToClasses(Object[] params) {
        return Arrays.stream(params)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);
    }

    public static ConstructorCallResult newInstanceOf(Class<?> compiledClass, Object... params) {
        Constructor<?> constructor;
        try {
            constructor = compiledClass.getConstructor(objectsToClasses(params));
        } catch (NoSuchMethodException e) {
            return new ConstructorCallResult.NoSuchConstructor(compiledClass.getSimpleName(), params);
        }
        try {
            return new ConstructorCallResult.Success(constructor.newInstance(params));
        } catch (InvocationTargetException e) {
            return new ConstructorCallResult.InnerException(e.getTargetException());
        } catch (InstantiationException e) {
            return new ConstructorCallResult.NotInstantiable();
        } catch (IllegalAccessException e) {
            return new ConstructorCallResult.IllegalAccess(constructor.getModifiers());
        }
    }
}
