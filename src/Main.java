import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
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


//        StrassensMatrixMultiplicationImpl strassensMatrixMultiplication = new StrassensMatrixMultiplicationImpl();
//        strassensMatrixMultiplication.probeSkeletonImplementation();
//
//        int[][] matrixA = createMatrix(1024);
//        int[][] matrixB = createMatrix(1024);
//        ArrayList<int[][]> matrices = new ArrayList<>(Arrays.asList(matrixA, matrixB));
//
//        for (int i = 3; i < 10; i++) {
//            int[][] result = strassensMatrixMultiplication.DaCSolveWithGranularity(matrices, i);
//        }
        //Do stuff with skeleton

        Path filePath = Path.of("src/MergeSortImpl.java");
        String classHexCode = ImplementationRuntimeLogger.hashFile(filePath);
        if (classHexCode != null) {
            System.out.println("Hash: " + classHexCode);
        } else {
            System.out.println("Error hashing file");
        }
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
