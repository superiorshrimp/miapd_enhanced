package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ExpertModule{
    ArrayList<String> labels;
    Application app;

    GridPane matrixGrid = new GridPane();
    ArrayList<ArrayList<Double>> matrix;
    ArrayList<ArrayList<Text>> matrixContent;
    ArrayList<ArrayList<ChoiceBox<String>>> matrixChoiceBox;

    ChoiceBox<String> expertChoiceBox;
    public ExpertModule(ArrayList<String> labels, Application app){
        this.app = app;
        this.labels = labels;

        this.matrix = new ArrayList<>(this.labels.size());
        this.matrixContent = new ArrayList<>(this.labels.size());
        this.matrixChoiceBox = new ArrayList<>(this.labels.size());

        for(int row = 0; row<this.labels.size(); row++){
            this.matrix.add(new ArrayList<>(this.labels.size()));
            this.matrixContent.add(new ArrayList<>(this.labels.size()));
            this.matrixChoiceBox.add(new ArrayList<>(this.labels.size()));
        }

        this.setUpGridPane();
        this.expertChoiceBox = new ChoiceBox<>(this.getExpertsList());
    }

    public void start(){
        for(int row = 0; row<this.labels.size(); row++){
            for(int col = 0; col<this.labels.size(); col++){
                if(col == row){
                    Text content = new Text("1");
                    content.setFont(Font.font("Verdana", 20));
                    content.setFill(Color.BLUE);
                    matrixGrid.add(content, col+1, row+1);
                    GridPane.setHalignment(content, HPos.CENTER);
                    matrixContent.get(row).add(content);
                    matrix.get(row).add((double)(1));
                    this.matrixChoiceBox.get(row).add(null);
                }
                else if(col < row){
                    Text content = new Text("-");
                    content.setFont(Font.font("Verdana", 20));
                    content.setFill(Color.RED);
                    matrixContent.get(row).add(content);
                    matrixGrid.add(content, col+1, row+1);
                    GridPane.setHalignment(content, HPos.CENTER);
                    matrix.get(row).add((double)(-1));
                    this.matrixChoiceBox.get(row).add(null);
                }
                else{
                    ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                        "1/9", "1/7", "1/5", "1/3", "1", "3", "5", "7", "9"
                    ));
                    this.matrixChoiceBox.get(row).add(choiceBox);
                    choiceBox.setPrefSize(50, 50);
                    this.choiceBoxObserve(choiceBox, row, col);
                    matrixContent.get(row).add(null);
                    matrixGrid.add(choiceBox, col+1, row+1);
                    GridPane.setHalignment(choiceBox, HPos.CENTER);
                    matrix.get(row).add((double)(-1));
                }
            }
        }

        Button loadButton = new Button("Load");
        loadButton.setPrefWidth(133);

        Button previousButton = new Button("Previous");
        previousButton.setPrefWidth(133);
        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(133);
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(loadButton, previousButton, nextButton);

        VBox root = new VBox();
        root.setSpacing(20);

        root.getChildren().addAll(matrixGrid, buttons, expertChoiceBox);

        Scene scene = new Scene(root);

        Stage expertStage = new Stage();
        expertStage.setScene(scene);
        expertStage.show();

        expertStage.setOnCloseRequest((WindowEvent we) -> System.exit(0));

        nextButton.setOnAction(event -> {
            if(this.isAllFilled()){
                expertStage.close();
                this.calculate();
            }
            else{
                System.out.println("fill all remaining fields");
            }
        });

        previousButton.setOnAction(event -> {
            expertStage.close();
            try {
                app.start(new Stage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        loadButton.setOnAction(event -> this.load());

    }

    public void calculate(){
//        for(int row = 0; row<this.labels.size(); row++){
//            for (int col = 0; col < this.labels.size(); col++){
//                System.out.print(this.matrix.get(row).get(col) + " ");
//            }
//            System.out.println();
//        }

        double consistencyIndex = -1;
        try {
            consistencyIndex = Utils.getConsistencyIndex(this.matrix, this.labels.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("consistency index: " + consistencyIndex);
        if(consistencyIndex > 0.1){
            System.out.println("Warning: consistency index is greater than 0.1, therefore results may not be correct!");
        }

        try {
            this.showResults(Utils.getResults(this.matrix, this.labels, this.labels.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResults(Map<?, ?> bestPhone){
        Stage resultsStage = new Stage();
        resultsStage.setTitle("Results");
        HBox hBox = new HBox(new Label(bestPhone.toString()));
        Scene scene = new Scene(hBox);
        resultsStage.setScene(scene);
        resultsStage.show();
    }

    private void load(){
        String p = this.expertChoiceBox.getSelectionModel().getSelectedItem();
        if(p == null){
            p = "priorities0";
        }
        String path = "../data/priorities/" + p + ".txt";
        Path filePath = Path.of(path);
        String str = null;
        try {
            str = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert str != null;
        String[] vals = str.split(" ");

        int i = 0;
        for(int row = 0; row<this.labels.size(); row++){
            for(int col = 0; col<this.labels.size(); col++){
                String val = vals[i];
                if(col<=row){
                    if(val.equals("1")){
                        matrix.get(row).set(col, (double)1);
                        Text content = matrixContent.get(row).get(col);
                        content.setText("1");
                        content.setFill(Color.GREEN);
                    }
                    else if(val.length() == 1){
                        matrix.get(row).set(col, Double.parseDouble(val));
                        Text content = matrixContent.get(row).get(col);
                        content.setText(val);
                        content.setFill(Color.GREEN);
                    }
                    else{
                        String s = String.valueOf(val.charAt(2));
                        matrix.get(row).set(col, (double)1 / Integer.parseInt(s));
                        Text content = matrixContent.get(row).get(col);
                        content.setText(val);
                        content.setFill(Color.GREEN);
                    }
                }
                else{
                    if(val.equals("1")){
                        matrix.get(row).set(col, (double)1);
                        this.matrixChoiceBox.get(row).get(col).getSelectionModel().select(val);

                    }
                    else if(val.length() == 1){
                        matrix.get(row).set(col, Double.parseDouble(val));
                        this.matrixChoiceBox.get(row).get(col).getSelectionModel().select(val);
                    }
                    else{
                        String s = String.valueOf(val.charAt(2));
                        matrix.get(row).set(col, (double)1 / Integer.parseInt(s));
                        this.matrixChoiceBox.get(row).get(col).getSelectionModel().select(val);
                    }
                }
                i++;
            }
        }
    }

    private void setUpGridPane(){
        matrixGrid.setGridLinesVisible(true);

        for(int i = 0; i<this.labels.size(); i++){
            Text c1 = new Text(this.labels.get(i));
            Text c2 = new Text(this.labels.get(i));
            matrixGrid.add(c1, 0, i+1);
            matrixGrid.add(c2, i+1, 0);
            GridPane.setHalignment(c1, HPos.CENTER);
            GridPane.setHalignment(c2, HPos.CENTER);
        }

        int rowCount = this.labels.size();
        int columnCount = this.labels.size();

        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(50);
        rc.setMaxHeight(50);

        for (int i = 0; i < rowCount+1; i++) {
            matrixGrid.getRowConstraints().add(rc);
        }

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(50);
        cc.setMaxWidth(50);

        for (int i = 0; i < columnCount+1; i++) {
            matrixGrid.getColumnConstraints().add(cc);
        }
    }

    private void choiceBoxObserve(ChoiceBox<String> choiceBox, int row, int col){
        choiceBox.setOnAction((event) -> {
            String val = choiceBox.getSelectionModel().getSelectedItem();
            if(val.equals("1")){
                matrix.get(row).set(col, (double)1);
                matrix.get(col).set(row, (double)1);
                Text content = matrixContent.get(col).get(row);
                content.setText("1");
                content.setFill(Color.GREEN);
            }
            else if(val.length() == 1){
                matrix.get(row).set(col, Double.parseDouble(val));
                matrix.get(col).set(row, (double) 1 / Integer.parseInt(val));
                Text content = matrixContent.get(col).get(row);
                content.setText("1/" + val);
                content.setFill(Color.GREEN);
            }
            else{
                String s = String.valueOf(val.charAt(2));
                matrix.get(row).set(col, (double)1 / Integer.parseInt(s));
                matrix.get(col).set(row, Double.parseDouble(s));
                Text content = matrixContent.get(col).get(row);
                content.setText(val.substring(2));
                content.setFill(Color.GREEN);
            }
        });
    }

    private boolean isAllFilled(){
        for(int row = 0; row<this.labels.size(); row++){
            for(int col = 0; col<this.labels.size(); col++){
                if(this.matrix.get(row).get(col) < 0){
                    return false;
                }
            }
        }
        return true;
    }

    private ObservableList<String> getExpertsList(){
        ObservableList<String> expertsList = FXCollections.observableArrayList();

        String directoryPath = "../data/priorities/";

        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles != null){
            for(File file : listOfFiles){
                if(file.isFile() && !file.getName().equals("priorities0.txt")){
                    expertsList.add(file.getName().substring(0, file.getName().length()-4));
                }
            }
        }

        return expertsList;
    }
}
