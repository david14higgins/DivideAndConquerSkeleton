import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        //StrassensMatrixMultiplication strassensMatrixMultiplication = new StrassensMatrixMultiplication();
        //strassensMatrixMultiplication.execute();
        //int cores = Runtime.getRuntime().availableProcessors();
        //System.out.println("Number of cores: " + cores);

        //MergeSort mergeSort = new MergeSort();
        //mergeSort.execute();
//        try {
//            // Build the command to start a new Java process
//            String javaHome = System.getProperty("java.home");
//            String javaExec = javaHome + "/bin/java";
//
//            ProcessBuilder processBuilder = new ProcessBuilder(
//                    javaExec,
//                    "-cp",
//                    "out/production/DivideAndConquerAbstraction", // Set the path to compiled classes or JAR
//                    "ReflectionRunner", // Main class to invoke via reflection
//                    "SkeletonImplementation", // Target class name
//                    "greet",            // Target method name
//                    "World"             // Method arguments
//            );
//
//            // Start the process
//            Process process = processBuilder.start();
//
//            // Capture the process output
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
//                }
//            }
//
//            process.waitFor(); // Wait for the process to complete
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        int[] problem = new int[] {61, 47, 12, 91, 4, 28, 86, 39, 58, 32,
        88, 35, 37, 46, 93, 25, 20, 30, 16, 45,
        8, 36, 72, 63, 57, 52, 75, 66, 78, 81,
        96, 55, 2, 51, 80, 7, 34, 38, 50, 9,
        22, 48, 95, 10, 83, 77, 54, 29, 71, 65};
        MergeSortImpl mergeSortImpl = new MergeSortImpl();
        int[] sorted = mergeSortImpl.solveProblem(problem);
        System.out.println(Arrays.toString(sorted));
    }
}
