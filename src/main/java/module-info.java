module javaLearnGui.main {
    requires com.github.javaparser.core;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.compiler;
    requires jdk.compiler;
    exports org.openjfx to javafx.graphics;
    opens org.openjfx;
}