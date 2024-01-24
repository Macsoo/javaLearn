package org.openjfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class MainApp extends Application {
    static Queue<Node> chapterNodes = new LinkedList<>();
    static ScrollPane root;
    public static void next() {
        if (chapterNodes.isEmpty()) return;
        //Add TextFlow
        ((VBox)root.getContent()).getChildren().add(chapterNodes.poll());
        if (chapterNodes.isEmpty()) return;
        //Add CodeEditor
        ((VBox)root.getContent()).getChildren().add(chapterNodes.poll());
        if (root.getVvalue() != root.getVmin())
            Platform.runLater(() -> {
                root.layout();
                root.setVvalue(root.getVmax());
            });
    }

    static void setScene(Scene scene, Stage stage) {
        root = (ScrollPane)scene.getRoot();
        VBox under = (VBox)root.getContent();
        chapterNodes.clear();
        chapterNodes.addAll(under.getChildren());
        under.getChildren().clear();
        next();
        stage.setScene(scene);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResource("FiraCode-Regular.ttf").toExternalForm(), 24);
        Font.loadFont(getClass().getResource("FiraCode-Bold.ttf").toExternalForm(), 24);
        Font.loadFont(getClass().getResource("PTSerif-Regular.ttf").toExternalForm(), 24);
        Font.loadFont(getClass().getResource("PTSerif-Bold.ttf").toExternalForm(), 24);
        Font.loadFont(getClass().getResource("PTSerif-Italic.ttf").toExternalForm(), 24);
        Font.loadFont(getClass().getResource("PTSerif-BoldItalic.ttf").toExternalForm(), 24);
        List<Scene> sceneList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("fxmls")))) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            reader.lines().forEachOrdered(fxml -> {
                Scene scene;
                try {
                    scene = new Scene(fxmlLoader.load(getClass().getResourceAsStream(fxml)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
                scene.setOnKeyPressed(keyEvent -> {
                    switch (keyEvent.getCode()) {
                        case ESCAPE -> primaryStage.close();
                        case END -> ((ScrollPane)scene.getRoot()).setVvalue(1.0);
                        case F12 -> next();
                    }
                });
                sceneList.add(scene);
            });
        }
        primaryStage.setFullScreen(false);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.setTitle("JavaLearn");
        setScene(sceneList.get(0), primaryStage);
        //primaryStage.setScene(sceneList.get(0));
        primaryStage.show();
    }
}