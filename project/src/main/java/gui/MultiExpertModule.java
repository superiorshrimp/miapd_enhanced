package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.Math.pow;

public class MultiExpertModule{
    ArrayList<String> labels;
    Application app;

    ArrayList<ArrayList<Double>> matrix;

    LinkedList<CheckBox> expertList = new LinkedList<>();
    public MultiExpertModule(ArrayList<String> labels, Application app){
        this.app = app;
        this.labels = labels;

        this.matrix = new ArrayList<>(this.labels.size());

        for(int row = 0; row<this.labels.size(); row++){
            this.matrix.add(new ArrayList<>(this.labels.size()));
        }
    }

    public void start(){
        Button previousButton = new Button("Previous");
        previousButton.setPrefWidth(200);

        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(200);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(previousButton, nextButton);

        VBox vbox = new VBox();
        vbox.setSpacing(5);

        for(String expert : this.getExperts()){
            CheckBox cb = new CheckBox(expert);
            vbox.getChildren().add(cb);
            this.expertList.add(cb);
        }

        VBox root = new VBox();
        root.setSpacing(20);

        root.getChildren().addAll(vbox, buttons);

        Scene scene = new Scene(root);

        Stage expertStage = new Stage();
        expertStage.setScene(scene);
        expertStage.show();

        expertStage.setOnCloseRequest((WindowEvent we) -> System.exit(0));

        nextButton.setOnAction(event -> {
            if(this.isAnyFilled()){
                expertStage.close();
                this.calculate();
            }
            else{
                System.out.println("choose at least one expert");
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

    }

    private int getExpertsCount(){
        int counter = 0;
        for(CheckBox cb : this.expertList){
            if(cb.isSelected()){
                counter++;
            }
        }
        return counter;
    }

    private void loadExpert(ArrayList<ArrayList<Double>> expertMatrix, String name){
        String path = "../data/priorities/" + name + ".txt";
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
                    expertMatrix.get(row).add((double)1);
                }
                else if(val.length() == 1){
                    expertMatrix.get(row).add(Double.parseDouble(val));
                }
                else{
                    String s = String.valueOf(val.charAt(2));
                    expertMatrix.get(row).add((double)1 / Integer.parseInt(s));
                }
                i++;
            }
        }
    }

    private double getGeometricMean(int row, int col, ArrayList<ArrayList<ArrayList<Double>>> expertsMatrix, int n){
        double val = 1;
        for(ArrayList<ArrayList<Double>> arrayLists : expertsMatrix){
            val *= arrayLists.get(row).get(col);
        }
        return pow(val, 1.0/n);
    }

    private ArrayList<ArrayList<ArrayList<Double>>> loadExperts(){
        int n = this.getExpertsCount();
        ArrayList<ArrayList<ArrayList<Double>>> expertsMatrix = new ArrayList<>(n);

        int i = 0;
        int j = 0;
        for(CheckBox cb : this.expertList){
            if(cb.isSelected()){
                expertsMatrix.add(new ArrayList<>(this.labels.size()));
                for(int row = 0; row<this.labels.size(); row++){
                    expertsMatrix.get(j).add(new ArrayList<>(this.labels.size()));
                }
                this.loadExpert(expertsMatrix.get(j), this.getExperts().get(i));

//                if (j < 3) {
//                    for (int row = 0; row < this.labels.size(); row++) {
//                        for (int col = 0; col < this.labels.size(); col++) {
//                            System.out.print(expertsMatrix.get(j).get(row).get(col) + " ");
//                        }
//                        System.out.println();
//                    }
//                    System.out.println();
//                }

                j++;
            }
            i++;
        }

        return expertsMatrix;
    }

    private void fillAggregatedMatrix(){
        ArrayList<ArrayList<ArrayList<Double>>> expertsMatrix = this.loadExperts();

        for(int row = 0; row<this.labels.size(); row++){
            for (int col = 0; col < this.labels.size(); col++){
                this.matrix.get(row).add(getGeometricMean(row, col, expertsMatrix, expertsMatrix.size()));
            }
        }

//        System.out.println("aggregated matrix: ");
//        for(int row = 0; row<this.labels.size(); row++){
//            for (int col = 0; col < this.labels.size(); col++){
//                System.out.print(this.matrix.get(row).get(col) + " ");
//            }
//            System.out.println();
//        }
    }

    public void calculate(){
        this.fillAggregatedMatrix();
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

    private boolean isAnyFilled(){
        for(CheckBox cb : this.expertList){
            if(cb.isSelected()){
                return true;
            }
        }
        return false;
    }

    private LinkedList<String> getExperts(){
        LinkedList<String> experts = new LinkedList<>();
        String directoryPath = "../data/priorities/";

        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles != null){
            for(File file : listOfFiles){
                if(file.isFile() && !file.getName().equals("priorities0.txt")){
                    experts.add(file.getName().substring(0, file.getName().length()-4));
                }
            }
        }

        return experts;
    }
}
