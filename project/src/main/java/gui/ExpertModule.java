package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
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
import java.util.ArrayList;
import java.util.Map;

public class ExpertModule{
    ArrayList<String> labels;
    Application app;

    GridPane matrixGrid = new GridPane();
    ArrayList<ArrayList<Double>> matrix;
    ArrayList<ArrayList<Text>> matrixContent;
    ArrayList<ArrayList<String>> matrixContentText;
    public ExpertModule(ArrayList<String> labels, Application app){
        this.app = app;
        this.labels = labels;

        this.matrix = new ArrayList<>(this.labels.size());
        this.matrixContent = new ArrayList<>(this.labels.size());
        this.matrixContentText = new ArrayList<>(this.labels.size());

        for(int row = 0; row<this.labels.size(); row++){
            this.matrix.add(new ArrayList<>(this.labels.size()));
            this.matrixContent.add(new ArrayList<>(this.labels.size()));
            this.matrixContentText.add(new ArrayList<>(this.labels.size()));
        }

        this.setUpGridPane();
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
                    this.matrixContentText.get(row).add("1");
                    matrix.get(row).add((double)(1));
                }
                else if(col < row){
                    Text content = new Text("-");
                    content.setFont(Font.font("Verdana", 20));
                    content.setFill(Color.RED);
                    matrixContent.get(row).add(content);
                    matrixGrid.add(content, col+1, row+1);
                    GridPane.setHalignment(content, HPos.CENTER);
                    this.matrixContentText.get(row).add("-");
                    matrix.get(row).add((double)(-1));
                }
                else{
                    ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                        "1/9", "1/7", "1/5", "1/3", "1", "3", "5", "7", "9"
                    ));
                    choiceBox.setPrefSize(50, 50);
                    this.choiceBoxObserve(choiceBox, row, col);
                    matrixContent.get(row).add(null);
                    this.matrixContentText.get(row).add("-");
                    matrixGrid.add(choiceBox, col+1, row+1);
                    GridPane.setHalignment(choiceBox, HPos.CENTER);
                    matrix.get(row).add((double)(-1));
                }
            }
        }

        Button loadButton = new Button("Load");
        loadButton.setPrefWidth(100);
        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(100);

        Button previousButton = new Button("Previous");
        previousButton.setPrefWidth(100);
        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(100);
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(loadButton, saveButton, previousButton, nextButton);

        VBox root = new VBox();
        root.setSpacing(20);

        root.getChildren().addAll(matrixGrid, buttons);

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

        String path = "../data/priorities/priorities.txt";

        saveButton.setOnAction(event -> this.save(path));

        loadButton.setOnAction(event -> this.load(path));

    }

    public void calculate(){
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

    private void save(String path){
        if(this.isAllFilled()){
            StringBuilder sb = new StringBuilder();

            for(int row = 0; row<this.labels.size(); row++){
                for(int col = 0; col<this.labels.size(); col++){
                    if(row != 0 || col != 0){
                        sb.append(" ");
                    }
                    int c = col;
                    int r = row;
                    if(row < col){
                        c = row;
                        r = col;
                    }
                    sb.append(this.matrixContentText.get(r).get(c));
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
                bw.write(sb.toString());
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("priorities not saved; fill all fields");
        }
    }

    private void load(String path){
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
                if(val.equals("1")){
                    matrix.get(row).set(col, (double)1);
                    matrix.get(col).set(row, (double)1);
                    Text content = matrixContent.get(col).get(row);
                    this.matrixContentText.get(col).set(row, "1");
                    if(content != null){
                        content.setText("1");
                        content.setFill(Color.GREEN);
                    }
                }
                else if(val.length() == 1){
                    matrix.get(row).set(col, Double.parseDouble(val));
                    matrix.get(col).set(row, (double) 1 / Integer.parseInt(val));
                    Text content = matrixContent.get(col).get(row);
                    this.matrixContentText.get(col).set(row, "1/" + val);
                    if(content != null){
                        content.setText(val);
                        content.setFill(Color.GREEN);
                    }
                }
                else{
                    String s = String.valueOf(val.charAt(2));
                    matrix.get(row).set(col, (double)1 / Integer.parseInt(s));
                    matrix.get(col).set(row, Double.parseDouble(s));
                    Text content = matrixContent.get(col).get(row);
                    this.matrixContentText.get(col).set(row, s);
                    if(content != null){
                        content.setText(val);
                        content.setFill(Color.GREEN);
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
                this.matrixContentText.get(col).set(row, "1");
                content.setText("1");
                content.setFill(Color.GREEN);
            }
            else if(val.length() == 1){
                matrix.get(row).set(col, Double.parseDouble(val));
                matrix.get(col).set(row, (double) 1 / Integer.parseInt(val));
                Text content = matrixContent.get(col).get(row);
                this.matrixContentText.get(col).set(row, "1/" + val);
                content.setText("1/" + val);
                content.setFill(Color.GREEN);
            }
            else{
                String s = String.valueOf(val.charAt(2));
                matrix.get(row).set(col, (double)1 / Integer.parseInt(s));
                matrix.get(col).set(row, Double.parseDouble(s));
                Text content = matrixContent.get(col).get(row);
                this.matrixContentText.get(col).set(row, s);
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
}
