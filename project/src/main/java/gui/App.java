package gui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Phone;

import java.io.IOException;
import java.util.ArrayList;

public class App extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        ArrayList<String> labels = Phone.getLabels();
        ExpertModule expertModule = new ExpertModule(labels, this);
        UserModule userModule = new UserModule(labels);

        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.setHgap(5);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setPadding(new Insets(0, 0, 0, 0)); // set top, right, bottom, left

        HBox hBox = new HBox();
        Button expertButton = new Button("I`m an expert");
        expertButton.setFont(Font.font("Verdana", 20));
        Button userButton = new Button("I`m a user");
        userButton.setFont(Font.font("Verdana", 20));

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(expertButton, userButton);

        Button gitUrl = new Button("Copy Github repo URL");
        gitUrl.setFont(Font.font("Verdana", 20));

        gitUrl.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString("https://github.com/superiorshrimp/miapd");
            clipboard.setContent(content);
        });

        Text title = new Text("AHP phone preference ranking app");
        title.setFont(Font.font("Verdana", 20));

        VBox vBox = new VBox();
        vBox.setSpacing(25);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(title, gitUrl, hBox);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(flowPane);
        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);
        borderPane.setMinHeight(100);
        borderPane.setMaxHeight(200);
        borderPane.setMinWidth(500);

        Scene scene = new Scene(borderPane);

        primaryStage.setTitle("App");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest((WindowEvent we) -> System.exit(0));

        expertButton.setOnAction(event -> {
            primaryStage.close();
            expertModule.start();
        });

        userButton.setOnAction(event -> {
            primaryStage.close();
            try {
                userModule.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}