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

        StrassensMatrixMultiplication strassensMatrixMultiplication = new StrassensMatrixMultiplication();

        int[][] matrixA = createMatrix(256);
        int[][] matrixB = createMatrix(256);

        ArrayList<int[][]> matrices = new ArrayList<>(Arrays.asList(matrixA, matrixB));

        int[][] result1 = strassensMatrixMultiplication.DaCSolve(matrices);

        //System.out.println("DaCSolve result:");
        //System.out.println(Arrays.deepToString(result1));

//        for (int granularity = 1; granularity <= 9; granularity++) {
//            int[][] result2 = strassensMatrixMultiplication.DaCSolveWithGranularity(matrices, granularity);
//        }


//        MergeSort mergeSort = new MergeSort();
//
//        int[] values = randomArrayGenerator(1000000);
//
//        int[] sortedValues = mergeSort.DaCSolve(values);
//
//        System.out.println(Arrays.toString(sortedValues));

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
