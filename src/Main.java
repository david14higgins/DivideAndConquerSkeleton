import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class Main {


    public static void main(String[] args) {
        StrassensMatrixMultiplicationImpl strassensMatrixMultiplication = new StrassensMatrixMultiplicationImpl();
        probeSkeletonImplementation(strassensMatrixMultiplication);
        //Do stuff with skeleton
    }

    public static void probeSkeletonImplementation(Object skeletonImpl) {
        HashMap<Integer, Long> solverRuntimes = new HashMap<>();
        HashMap<Integer, Long> dividerRuntimes = new HashMap<>();
        HashMap<Integer, Long> combinerRuntimes = new HashMap<>();

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

                    String[] lineValues = line.split(" ");
                    switch (lineValues[0]) {
                        case "SOLVER": solverRuntimes.put(Integer.getInteger(lineValues[1]), Long.valueOf(lineValues[2])); break;
                        case "DIVIDER": dividerRuntimes.put(Integer.getInteger(lineValues[1]), Long.valueOf(lineValues[2])); break;
                        case "COMBINER": combinerRuntimes.put(Integer.getInteger(lineValues[1]), Long.valueOf(lineValues[2])); break;
                        default: break;
                    }
                }
            }
            process.waitFor(); // Wait for the process to complete

            System.out.println("Still executing in Main.java");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Output read values
        System.out.println(solverRuntimes);
        System.out.println(dividerRuntimes);
        System.out.println(combinerRuntimes);
    }
}
