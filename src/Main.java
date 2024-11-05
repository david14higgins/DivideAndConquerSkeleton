
public class Main {
    public static void main(String[] args) {

        long startTime = System.nanoTime();


        StrassensMatrixMultiplication strassensMatrixMultiplication = new StrassensMatrixMultiplication();
        strassensMatrixMultiplication.execute();

        long endTime = System.nanoTime();

        // Calculate the elapsed time in nanoseconds
        long duration = endTime - startTime;

        // Print the elapsed time
        System.out.println("Time taken (in nanoseconds): " + duration);


        //System.out.println(Arrays.toString(result));
    }
}