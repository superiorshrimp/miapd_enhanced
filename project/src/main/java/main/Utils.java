package main;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Utils{

    public static double getConsistencyIndex(ArrayList<ArrayList<Double>> matrix, int n) throws Exception{
        //example:
        //String[] args = "python ../math/consistency_index.py 1 7 0.1666 0.5 0.25 0.1666 4 0.1428 1 0.3333 5 0.2 0.1428 5 6 3 1 6 3 2 8 2 0.2 0.1666 1 8 0.2 8 4 5 0.3333 0.125 1 0.1111 2 6 7 0.5 5 9 1 2 0.25 0.2 0.125 0.125 0.5 0.5 1".split(" ");
        StringBuilder command = new StringBuilder("python ../math/consistency_index.py");
        for(int row = 0; row<n; row++){
            for(int col = 0; col<n; col++){
                command.append(" ").append(matrix.get(row).get(col));
            }
        }
        String[] args = command.toString().split(" ");
        Process proc = Runtime.getRuntime().exec(args);
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String s = stdInput.readLine();
        if(s != null){
            return Double.parseDouble(s.substring(1, 5));
        }
        throw new Exception();
    }

    public static double[][] getPhones(ArrayList<String> labels, int n) throws IOException{
        File folder = new File("../data/phones");
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        int[][] phoneStatisticsMatrix = new int[listOfFiles.length][n];
        double[][] phoneStatisticsMatrixNormalized = new double[listOfFiles.length][n];

        int idx = 0;
        for (File file : listOfFiles){
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> map = mapper.readValue(Paths.get(file.toURI()).toFile(), Map.class);

            int idy = 0;
            for (String label : labels){
                phoneStatisticsMatrix[idx][idy] = (int) map.get(label);
                idy ++;
            }
            idx ++;
        }

        for ( int y = 0; y < n; y++ ){
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

    public static Map<?, ?> getResults(ArrayList<ArrayList<Double>> matrix, ArrayList<String> labels, int n) throws Exception{
        for ( int x = 0; x < n - 2; x ++ ){
            for ( int y = 2; y < n; y++ ){
                if (x + y > 6) break;
                matrix.get(y-2).set(x+y, Math.min(Math.sqrt(matrix.get(y-2).get(x+y-1) * matrix.get(x+y-1).get(x+y)), 9));
            }
        }

        for ( int x = 0; x < n - 1; x++ ){
            for ( int y = 1; y < n; y++ ){
                if (x + y > 6) break;
                matrix.get(x+y).set(y-1, 1/matrix.get(y-1).get(x+y));
            }
        }

        StringBuilder command = new StringBuilder("python ../math/priority_vector.py");
        for(int row = 0; row<n; row++){
            for(int col = 0; col<n; col++){
                command.append(" ").append(matrix.get(row).get(col));
            }
        }

        String[] args = command.toString().split(" +");
        Process proc = Runtime.getRuntime().exec(args);
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String s = stdInput.readLine();
        s = s.substring(1, s.length() - 1);
        String[] strings = s.split(" +");
        Double[] formattedValues = new Double[strings.length];
        for (int x = 0; x < strings.length; x ++){
            System.out.println(strings[x]);
            formattedValues[x] = Double.parseDouble(strings[x]);
        }

        double[][] phones = getPhones(labels, n);

        for (double[] phone : phones){
            for ( int y = 0; y < n; y++){
                phone[y] *= formattedValues[y];
            }
        }
        ArrayList<Double> results = new ArrayList<>();
        for (int x = 0; x < phones.length; x++){
            results.add(0.0);
            for ( int y = 0; y < n; y++){
                results.set(x, results.get(x) + phones[x][y] * formattedValues[y]);
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

        return mapper.readValue(Paths.get(listOfFiles[max_idx].toURI()).toFile(), Map.class);
    }
}
