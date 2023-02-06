package test;

import main.Phone;
import main.Utils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class test {
    @Test
    public void getConsistencyIndex() throws IOException, InterruptedException {
        StringBuilder command = new StringBuilder("python ../math/consistency_index.py 1 7 0.1666 0.5 0.25 0.1666 4 0.1428 1 0.3333 5 0.2 0.1428 5 6 3 1 6 3 2 8 2 0.2 0.1666 1 8 0.2 8 4 5 0.3333 0.125 1 0.1111 2 6 7 0.5 5 9 1 2 0.25 0.2 0.125 0.125 0.5 0.5 1");
        String[] args = command.toString().split(" ");
        Process proc = Runtime.getRuntime().exec(args);
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String s = stdInput.readLine();
        if(s != null){
            var s2 = Double.parseDouble(s.substring(1, 5));
            assertEquals(0.7, s2);
        }
    }

    @Test
    public void getPhonesTest() throws IOException {
        ArrayList<String> labels = Phone.getLabels();
        var x = Utils.getPhones(labels, labels.size());
        double[][] y = {{0.009617234083477588, 0.8, 0.5, 0.8293782340295526, 0.5, 0.6558, 0.25}, {0.23081361800346223, 1.0, 0.6666666666666666, 0.982880070156492, 0.5, 1.0, 0.375}, {0.0, 1.0, 1.0, 1.0, 1.0, 0.92, 1.0}};
        assertArrayEquals(y, x);
    }

    @Test
    public void getPriorityVectorTest() throws InterruptedException, IOException {
        StringBuilder command = new StringBuilder("python ../math/priority_vector.py 1 7 0.1666 0.5 0.25 0.1666 4 0.1428 1 0.3333 5 0.2 0.1428 5 6 3 1 6 3 2 8 2 0.2 0.1666 1 8 0.2 8 4 5 0.3333 0.125 1 0.1111 2 6 7 0.5 5 9 1 2 0.25 0.2 0.125 0.125 0.5 0.5 1");

        String[] args = command.toString().split(" ");
        Process proc = null;
        proc = Runtime.getRuntime().exec(args);
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

        Double[] x = {0.09264744, 0.09335371, 0.26599549, 0.13046206, 0.10067653, 0.28890181, 0.02796296};
        assertArrayEquals(x, formattedValues);
    }
}
