package gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class UserModule {
    private static int stageNumber = 0;
    ArrayList<Label> labels = new ArrayList<>();
    ArrayList<Pair<VBox, Integer>> vBoxes = new ArrayList<>();
    float[][] userPreferences;
    public UserModule(ArrayList<String> labels){
        for ( String label : labels){
            this.labels.add(new Label(label));
        }
        int arraySize = this.labels.size();
        this.userPreferences = new float[arraySize][arraySize];
        for (int i = 0; i < arraySize; i++){
            this.userPreferences[i][i] = 1;
        }
        for (int i = 0; i < labels.size()-1; i++) {
            Label label = this.labels.get(i);
            VBox vBox = new VBox(label);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(10, 10, 10, 10));
            vBoxes.add(new Pair<>(vBox, i*7+i+1));
        }
        Collections.shuffle(vBoxes);
        Label label = this.labels.get(labels.size()-1);
        VBox vBox = new VBox(label);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBoxes.add(new Pair<>(vBox, (labels.size()-1)*7+labels.size()-1+1));
    }
    public void start() throws IOException {
        Stage userStage = new Stage();

        this.getPhones();

        Button nextButton = new Button("Next");
        Label infoLabel = new Label("Compare importance of first feature to second");
        HBox hBox = new HBox(vBoxes.get(UserModule.stageNumber).getKey(), vBoxes.get(UserModule.stageNumber + 1).getKey());
        int xIdx = vBoxes.get(UserModule.stageNumber).getValue() / labels.size();
        int yIdx = vBoxes.get(UserModule.stageNumber).getValue() % labels.size();

        ChoiceBox<String> cb = new ChoiceBox<>(FXCollections.observableArrayList(
                "1/9", "1/7", "1/5", "1/3", "1", "3", "5", "7", "9"
        ));

        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(infoLabel, hBox, cb, nextButton);
        vBox.setAlignment(Pos.CENTER);
        FlowPane flowPane = new FlowPane(vBox);

        flowPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(flowPane);

        userStage.setScene(scene);
        userStage.show();

        nextButton.setOnAction(event -> {
            userStage.close();
            UserModule.stageNumber += 1;
            if (UserModule.stageNumber > labels.size() - 2) {
                try {
                    calculateResults();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        cb.setOnAction(event -> {
            String val = cb.getValue();
            if(val.equals("1")){
                userPreferences[xIdx][yIdx] = 1;
            }
            else if(val.length() == 1) {
                userPreferences[xIdx][yIdx] = Float.parseFloat(val);
            }
            else{
                String s = String.valueOf(val.charAt(2));
                userPreferences[xIdx][yIdx] = (float)1 / Integer.parseInt(s);
            }
        });

        userStage.setOnCloseRequest((WindowEvent we) -> System.exit(0));
    }

    //Function to get results from BE
    private void calculateResults() throws Exception {
        float size = this.userPreferences.length;
        for ( int x = 0; x < size - 2; x ++ ){
            for ( int y = 2; y < size; y++ ){
                if (x + y > 6) break;
                userPreferences[y - 2][x + y] = Math.min((float) Math.sqrt(userPreferences[y - 2][x + y - 1] * userPreferences[x + y - 1][x + y]), 9);
            }
        }

        for ( int x = 0; x < size - 1; x++ ){
            for ( int y = 1; y < size; y++ ){
                if (x + y > 6) break;
                userPreferences[x + y][y - 1] = 1/userPreferences[y - 1][x + y];
            }
        }

        StringBuilder command = new StringBuilder("python ../math/priority_vector.py");
        for(int row = 0; row<this.userPreferences.length; row++){
            for(int col = 0; col<this.userPreferences.length; col++){
                command.append(" ").append(this.userPreferences[row][col]);
            }
        }

        String[] args = command.toString().split(" ");
        Process proc = Runtime.getRuntime().exec(args);
//        System.out.println(Arrays.toString(args));
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String s = stdInput.readLine();
        s = s.substring(1, s.length() - 1);
        String[] strings = s.split(" +");
        Double[] formattedValues = new Double[strings.length];
        System.out.println(Arrays.toString(strings));
        for (int x = 0; x < strings.length; x ++){
            System.out.println(strings[x]);
            formattedValues[x] = Double.parseDouble(strings[x]);
        }

//        System.out.println(Arrays.toString(formattedValues));
        double[][] phones = getPhones();

        for (double[] phone : phones){
            for ( int y = 0; y < labels.size(); y++){
                phone[y] *= formattedValues[y];
            }
        }
        ArrayList<Double> results = new ArrayList<>();
        for (int x = 0; x < phones.length; x++){
            results.add(0.0);
            for ( int y = 0; y < labels.size(); y++){
                results.set(x, results.get(x) + phones[x][y] * formattedValues[y]) ;
            }
        }
        System.out.println(results);
        Double max = Double.NEGATIVE_INFINITY;
        int max_idx = -1;
        for (int i = 0; i < phones.length; i++){
            if (max < results.get(i)){
                max_idx = i;
                max = results.get(i);
            }
        }

        File folder = new File("../data/phones");
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> map = mapper.readValue(Paths.get(listOfFiles[max_idx].toURI()).toFile(), Map.class);

        showResults(map);
    }

    private double[][] getPhones() throws IOException {
        File folder = new File("../data/phones");
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        int[][] phoneStatisticsMatrix = new int[listOfFiles.length][labels.size()];
        double[][] phoneStatisticsMatrixNormalized = new double[listOfFiles.length][labels.size()];

        int idx = 0;
        for (File file : listOfFiles){
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> map = mapper.readValue(Paths.get(file.toURI()).toFile(), Map.class);

            int idy = 0;
            for (Label label : labels){
                phoneStatisticsMatrix[idx][idy] = (int) map.get(label.getText());
                idy ++;
            }
            idx ++;
        }

        for ( int y = 0; y < labels.size(); y++ ){
            double maxVal = Double.NEGATIVE_INFINITY;

            for ( int x = 0; x < listOfFiles.length; x++ ){
                maxVal = Math.max(maxVal, phoneStatisticsMatrix[x][y]);
            }
            for ( int x = 0; x < listOfFiles.length; x++ ){
                phoneStatisticsMatrixNormalized[x][y] = phoneStatisticsMatrix[x][y] / maxVal;
            }
        }
        System.out.println(Arrays.deepToString(phoneStatisticsMatrix));
        System.out.println(Arrays.deepToString(phoneStatisticsMatrixNormalized));

        return phoneStatisticsMatrixNormalized;
    }
    private void showResults(Map<?, ?> bestPhone){
        Stage resultsStage = new Stage();
        resultsStage.setTitle("Results");
        HBox hBox = new HBox(new Label(bestPhone.toString()));
        Scene scene = new Scene(hBox);
        resultsStage.setScene(scene);
        resultsStage.show();
    }
}
