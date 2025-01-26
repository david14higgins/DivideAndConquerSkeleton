import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {


    public static void main(String[] args) {
//        MergeSortImpl mergeSortImpl = new MergeSortImpl();
//        mergeSortImpl.probeSkeletonImplementation();
//        int arraySize = 10000;
//        int[] inputArray = randomArrayGenerator(arraySize);
//        int[] result = mergeSortImpl.DaCSolve(inputArray);
//        System.out.println(Arrays.toString(result));


        StrassensMatrixMultiplicationImpl strassensMatrixMultiplication = new StrassensMatrixMultiplicationImpl();
        strassensMatrixMultiplication.probeSkeletonImplementation();

        int[][] matrixA = createMatrix(512);
        int[][] matrixB = createMatrix(512);
        ArrayList<int[][]> matrices = new ArrayList<>(Arrays.asList(matrixA, matrixB));

        int[][] result = strassensMatrixMultiplication.DaCSolve(matrices);

        //Do stuff with skeleton
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
