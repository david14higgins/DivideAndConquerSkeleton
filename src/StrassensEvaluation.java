import java.util.ArrayList;
import java.util.Arrays;

public class StrassensEvaluation {

    public static void evaluateStrassens() {
        //Estimated runtimes
        StrassensMatrixMultiplication strassensMatrixMultiplication = new StrassensMatrixMultiplication();
        int[][] matrixA = createMatrix(2048);
        int[][] matrixB = createMatrix(2048);
        strassensMatrixMultiplication.DaCSolve(Arrays.asList(matrixA, matrixB));
    }


    public static void actualRuntimes(int problemSize) {
        int avgIterations = 1;
        StrassensMatrixMultiplication strassensMatrixMultiplication = new StrassensMatrixMultiplication();

        int[][] matrixA = createMatrix(problemSize);
        int[][] matrixB = createMatrix(problemSize);

        ArrayList<int[][]> matrices = new ArrayList<>(Arrays.asList(matrixA, matrixB));

        long executionAccumulator = 0;

        for (int granularity : strassensMatrixMultiplication.possibleGranularities(matrices)) {
            for (int i = 0; i < avgIterations; i++) {
                long executionTime = strassensMatrixMultiplication.DaCRuntimeWithGranularity(matrices, granularity);
                executionAccumulator += executionTime;
            }
            long executionAverage = executionAccumulator / avgIterations;
            System.out.println("Granularity: " + granularity + " Average Execution Time: " + executionAverage);
        }
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
