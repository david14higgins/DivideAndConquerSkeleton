import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        StrassensMatrixMultiplicationImpl strassensMatrixMultiplication = new StrassensMatrixMultiplicationImpl();
        probeSkeletonImplementation(strassensMatrixMultiplication);
        //Do stuff with skeleton
    }

    public static void probeSkeletonImplementation(Object skeletonImpl) {
        try {
            // Build the command to start a new Java process
            String javaHome = System.getProperty("java.home");
            String javaExec = javaHome + "/bin/java";
            String className = skeletonImpl.getClass().getName();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    javaExec,
                    "-cp",
                    "out/production/DivideAndConquerAbstraction", // Set the path to compiled classes or JAR
                    "MethodProber", // Main class to invoke via reflection
                    className // Target class name
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

            System.out.println("Still executing in Main.java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
