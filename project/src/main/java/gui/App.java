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
        MultiExpertModule expertsModule = new MultiExpertModule(labels, this);
        ManageExperts manageExpertsModule = new ManageExperts(labels, this);

        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.setHgap(5);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setPadding(new Insets(0, 0, 0, 0)); // set top, right, bottom, left

        Button gitUrl = new Button("Copy Github repo URL");
        gitUrl.setFont(Font.font("Verdana", 20));
        gitUrl.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString("https://github.com/superiorshrimp/miapd_enhanced");
            clipboard.setContent(content);
        });

        Button userButton = new Button("User mode");
        userButton.setFont(Font.font("Verdana", 20));
        userButton.setOnAction(event -> {
            primaryStage.close();
            try {
                userModule.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button expertButton = new Button("Single expert mode");
        expertButton.setFont(Font.font("Verdana", 20));
        expertButton.setOnAction(event -> {
            primaryStage.close();
            expertModule.start();
        });

        Button expertsButton = new Button("Multi-expert mode");
        expertsButton.setFont(Font.font("Verdana", 20));
        expertsButton.setOnAction(event -> {
            primaryStage.close();
            expertsModule.start();
        });

        Button manageExpertsButton = new Button("Manage experts");
        manageExpertsButton.setFont(Font.font("Verdana", 20));
        manageExpertsButton.setOnAction(event -> {
            primaryStage.close();
            manageExpertsModule.start();
        });

        Text title = new Text("Enhanced AHP phone preference ranking app");
        title.setFont(Font.font("Verdana", 20));

        VBox modesBox = new VBox();
        modesBox.setSpacing(5);
        modesBox.setAlignment(Pos.CENTER);
        modesBox.getChildren().addAll(userButton, expertButton, expertsButton);

        VBox vBox = new VBox();
        vBox.setSpacing(25);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(title, gitUrl, modesBox, manageExpertsButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(flowPane);
        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);
        borderPane.setMinHeight(200);
        borderPane.setMaxHeight(500);
        borderPane.setMinWidth(500);

        Scene scene = new Scene(borderPane);

        primaryStage.setTitle("App");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest((WindowEvent we) -> System.exit(0));
    }
}