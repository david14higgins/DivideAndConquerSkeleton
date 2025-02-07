import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {


    public static void main(String[] args) {

//        StrassensMatrixMultiplication strassensMatrixMultiplication = new StrassensMatrixMultiplication();
//
//        int[][] matrixA = createMatrix(1024);
//        int[][] matrixB = createMatrix(1024);
//        ArrayList<int[][]> matrices = new ArrayList<>(Arrays.asList(matrixA, matrixB));
//
//        strassensMatrixMultiplication.DaCSolve(matrices);

        MergeSort mergeSort = new MergeSort();

        int[] values = randomArrayGenerator(1000000);

        int[] sortedValues = mergeSort.DaCSolve(values);

        System.out.println(Arrays.toString(sortedValues));

    }

    private static int[] randomArrayGenerator(int size) {
        int[] values = new int[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) (Math.random() * 101);
        }
        return values;
    }

    private static int[][] createMatrix(int widthHeight) {
        if (widthHeight <= 0) {
            throw new IllegalArgumentException("Size 'n' must be greater than 0");
        }

        int[][] array = new int[widthHeight][widthHeight];
        for (int i = 0; i < widthHeight; i++) {
            for (int j = 0; j < widthHeight; j++) {
                array[i][j] = (int) (Math.random() * 101); // Initialises all elements to 0
            }
        }
        return array;
    }
}
