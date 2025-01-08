import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
public class Main {
    public static void main(String[] args) {

        //StrassensMatrixMultiplication strassensMatrixMultiplication = new StrassensMatrixMultiplication();
        //strassensMatrixMultiplication.execute();
        //int cores = Runtime.getRuntime().availableProcessors();
        //System.out.println("Number of cores: " + cores);

        //MergeSort mergeSort = new MergeSort();
        //mergeSort.execute();
        try {
            // Build the command to start a new Java process
            String javaHome = System.getProperty("java.home");
            String javaExec = javaHome + "/bin/java";

            ProcessBuilder processBuilder = new ProcessBuilder(
                    javaExec,
                    "-cp",
                    "out/production/DivideAndConquerAbstraction", // Set the path to compiled classes or JAR
                    "ReflectionRunner", // Main class to invoke via reflection
                    "SkeletonImplementation", // Target class name
                    "greet",            // Target method name
                    "World"             // Method arguments
            );

            // Start the process
            Process process = processBuilder.start();

            // Capture the process output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            process.waitFor(); // Wait for the process to complete
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
