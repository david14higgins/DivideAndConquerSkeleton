import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class StrassensMatrixMultiplicationImpl extends DaCSkeletonAbstract<List<int[][]>, int[][]>{
    // Base case compute method
    private static int[][] naiveMultiply(List<int[][]> matrices) {
        int[][] matrixA = matrices.get(0);
        int[][] matrixB = matrices.get(1);

        // Check if matrices are compatible for multiplication
        if (matrixA[0].length != matrixB.length) {
            throw new IllegalArgumentException("Matrix A's columns must match Matrix B's rows for multiplication.");
        }

        int[][] resultMatrix = new int[matrixA.length][matrixB[0].length];

        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                //Intialise cell of result matrix
                resultMatrix[i][j] = 0;
                for (int k = 0; k < matrixA[0].length; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return resultMatrix;
    }

    @Override
    protected Function<List<int[][]>, int[][]> getProblemSolver() {
        return StrassensMatrixMultiplicationImpl::naiveMultiply;
    }

    // Takes a pair of matrices and produces a list of pairs of matrices
    private static List<List<int[][]>> createSubprolems(List<int[][]> matrices) {
        if(matrices.size() != 2) {
            System.out.println("Expected pair of matrices");
            return null;
        }

        int[][] matrixA = matrices.get(0);
        int[][] matrixB = matrices.get(1);

        int n = matrixA.length;

        int[][] A11 = new int[n/2][n/2];
        int[][] A12 = new int[n/2][n/2];
        int[][] A21 = new int[n/2][n/2];
        int[][] A22 = new int[n/2][n/2];
        int[][] B11 = new int[n/2][n/2];
        int[][] B12 = new int[n/2][n/2];
        int[][] B21 = new int[n/2][n/2];
        int[][] B22 = new int[n/2][n/2];

        // Dividing matrix A
        split(matrixA, A11, 0 , 0);
        split(matrixA, A12, 0 , n/2);
        split(matrixA, A21, n/2, 0);
        split(matrixA, A22, n/2, n/2);

        // Dividing matrix B
        split(matrixB, B11, 0 , 0);
        split(matrixB, B12, 0 , n/2);
        split(matrixB, B21, n/2, 0);
        split(matrixB, B22, n/2, n/2);

        //Create subproblems
        List<List<int[][]>> subproblems = new ArrayList<>();

        List<int[][]> subproblem1 = new ArrayList<>(Arrays.asList(add(A11, A22), add(B11, B22)));
        subproblems.add(subproblem1);
        List<int[][]> subproblem2 = new ArrayList<>(Arrays.asList(add(A21, A22), B11));
        subproblems.add(subproblem2);
        List<int[][]> subproblem3 = new ArrayList<>(Arrays.asList(A11, sub(B12, B22)));
        subproblems.add(subproblem3);
        List<int[][]> subproblem4 = new ArrayList<>(Arrays.asList(A22, sub(B21, B11)));
        subproblems.add(subproblem4);
        List<int[][]> subproblem5 = new ArrayList<>(Arrays.asList(add(A11, A12), B22));
        subproblems.add(subproblem5);
        List<int[][]> subproblem6 = new ArrayList<>(Arrays.asList(sub(A21, A11), add(B11, B12)));
        subproblems.add(subproblem6);
        List<int[][]> subproblem7 = new ArrayList<>(Arrays.asList(sub(A12, A22), add(B21, B22)));
        subproblems.add(subproblem7);

        return subproblems;
    }

    @Override
    protected Function<List<int[][]>, List<List<int[][]>>> getSubproblemGenerator() {
        return StrassensMatrixMultiplicationImpl::createSubprolems;
    }

    private static int[][] joinMatrices(List<int[][]> matrices) {
        int[][] M1 = matrices.get(0);
        int[][] M2 = matrices.get(1);
        int[][] M3 = matrices.get(2);
        int[][] M4 = matrices.get(3);
        int[][] M5 = matrices.get(4);
        int[][] M6 = matrices.get(5);
        int[][] M7 = matrices.get(6);

        int n = M1.length * 2;

        // Compute submatrices
        int [][] C11 = add(sub(add(M1, M4), M5), M7);
        int [][] C12 = add(M3, M5);
        int [][] C21 = add(M2, M4);
        int [][] C22 = add(sub(add(M1, M3), M2), M6);

        int[][] resultMatrix = new int[n][n];

        // join 4 submatrices into result matrix
        join(C11, resultMatrix, 0 , 0);
        join(C12, resultMatrix, 0 , n/2);
        join(C21, resultMatrix, n/2, 0);
        join(C22, resultMatrix, n/2, n/2);
        return resultMatrix;
    }

    @Override
    protected Function<List<int[][]>, int[][]> getSolutionCombiner() {
        return StrassensMatrixMultiplicationImpl::joinMatrices;
    }

    private static int matrixSizeQuantifier(List<int[][]> matrices) {
        int sideLength = matrices.get(0).length;
        return (int) (Math.log(sideLength) / Math.log(2));
    }
    @Override
    protected Function<List<int[][]>, Integer> getProblemQuantifier() {
        return StrassensMatrixMultiplicationImpl::matrixSizeQuantifier;
    }

    private static List<int[][]> problemGenerator(int dimensionPower) {
        int[][] matrixA = generateRandomMatrix((int) Math.pow(2, dimensionPower), 0, 1000);
        int[][] matrixB = generateRandomMatrix((int) Math.pow(2, dimensionPower), 0, 1000);
        return new ArrayList<>(Arrays.asList(matrixA, matrixB));
    }

    @Override
    protected Function<Integer, List<int[][]>> getProblemGenerator() {
        return StrassensMatrixMultiplicationImpl::problemGenerator;
    }

    @Override
    protected int getGranularity() {
        return 5;
    }

    // ----- Helper Methods -----

    // Function to sub two matrices
    public static int[][] sub(int[][] matrixA, int[][] matrixB)
    {
        int n = matrixA.length;
        int[][] resultMatrix = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                resultMatrix[i][j] = matrixA[i][j] - matrixB[i][j];
        return resultMatrix;
    }
    // Function to add two matrices
    public static int[][] add(int[][] matrixA, int[][] matrixB)
    {
        int n = matrixA.length;
        int[][] resultMatrix = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
        return resultMatrix;
    }

    // Method to split parent matrix into child matrices
    public static void split(int[][] parentMatrix, int[][] childMatrix, int startParentRowIndex, int startParentColumnIndex)
    {
        for(int childRowIndex = 0, parentRowIndex = startParentRowIndex; childRowIndex < childMatrix.length; childRowIndex++, parentRowIndex++)
            for(int childColumnIndex = 0, parentColumnIndex = startParentColumnIndex; childColumnIndex < childMatrix.length; childColumnIndex++, parentColumnIndex++)
                childMatrix[childRowIndex][childColumnIndex] = parentMatrix[parentRowIndex][parentColumnIndex];
    }

    // Function to join child matrices into parent matrix
    public static void join(int[][] childMatrix, int[][] parentMatrix, int startParentRowIndex, int startParentColumnIndex)
    {
        for(int childRowIndex = 0, parentRowIndex = startParentRowIndex; childRowIndex < childMatrix.length; childRowIndex++, parentRowIndex++)
            for(int childColumnIndex = 0, parentColumnIndex = startParentColumnIndex; childColumnIndex < childMatrix.length; childColumnIndex++, parentColumnIndex++)
                parentMatrix[parentRowIndex][parentColumnIndex] = childMatrix[childRowIndex][childColumnIndex];
    }



    // Generates a random matrix where each element is a random value between the upper and lower bound
    public static int[][] generateRandomMatrix(int dimension, int lowerBound, int upperBound) {
        int[][] generatedMatrix = new int[dimension][dimension];
        Random random = new Random();

        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                generatedMatrix[i][j] = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
            }
        }
        return generatedMatrix;
    }

    public static void printMatrix(int[][] matrix){
        for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[0].length; j++){
                System.out.print(matrix[i][j]+ " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static int[][] readMatrixFromFile(String filename) {
        String filepath = "MatricesTextfiles/" + filename;

        List<int[]> matrixRows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;

            while((line = br.readLine()) != null){
                String[] rowStr = line.trim().split("\\s+");

                //Convert to integers
                int[] rowInt = new int[rowStr.length];
                for (int i = 0; i < rowStr.length; i++ ) {
                    rowInt[i] = Integer.parseInt(rowStr[i]);
                }

                //Add to matrix rows
                matrixRows.add(rowInt);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Convert the list of rows to a 2d array
        int[][] matrix = new int[matrixRows.size()][];
        matrix = matrixRows.toArray(matrix);

        return matrix;
    }

    public static void writeMatrixToFile(int[][] matrix, String filename) {
        String filepath = "MatricesTextfiles/" + filename;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (int[] row : matrix) {
                for (int i = 0; i < row.length; i++) {
                    writer.write(Integer.toString(row[i]));
                    if (i < row.length - 1) {
                        writer.write(" "); // Add space between columns
                    }
                }
                writer.newLine(); // Move to the next line for the next row
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing the matrix to the file.");
            throw new RuntimeException(e);
        }
    }
}
