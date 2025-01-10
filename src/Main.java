import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class Main {


    public static void main(String[] args) {
        MergeSortImpl mergeSortImpl = new MergeSortImpl();
        probeSkeletonImplementation(mergeSortImpl);

        //StrassensMatrixMultiplicationImpl strassensMatrixMultiplication = new StrassensMatrixMultiplicationImpl();
        //probeSkeletonImplementation(strassensMatrixMultiplication);
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
                    String[] lineValues = line.split(" ");
                    switch (lineValues[0]) {
                        case "SOLVER": solverRuntimes.put(Integer.parseInt(lineValues[1]), Long.valueOf(lineValues[2])); break;
                        case "DIVIDER": dividerRuntimes.put(Integer.parseInt(lineValues[1]), Long.valueOf(lineValues[2])); break;
                        case "COMBINER": combinerRuntimes.put(Integer.parseInt(lineValues[1]), Long.valueOf(lineValues[2])); break;
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

        ModelFitter modelFitter = new ModelFitter();

        HashMap<Integer, Long> linearData = new HashMap<>();
        for (int x = 1; x <= 10; x++) {
            long y = 2 * x + 5;
            linearData.put(x, y);
        }

        ModelFitter.BestFitModel testModel = modelFitter.fitModel(linearData);
        System.out.println("Test best fit model: " + testModel.modelName);
        System.out.println("Error: " + testModel.error);
        System.out.println("Test 5: " + testModel.model.predict(50));


//        ModelFitter.BestFitModel solverBestFitModel = modelFitter.fitModel(solverRuntimes);
//        ModelFitter.BestFitModel dividerBestFitModel = modelFitter.fitModel(dividerRuntimes);
//        ModelFitter.BestFitModel combinerBestFitModel = modelFitter.fitModel(combinerRuntimes);
//
//        // Output the results
//        System.out.println("Solver best fit model: " + solverBestFitModel.modelName);
//        System.out.println("Error: " + solverBestFitModel.error);
//        System.out.println("Test 20: " + solverBestFitModel.model.predict(20));
//        System.out.println("Test 900: " + solverBestFitModel.model.predict(900));
//        System.out.println("Test 9000: " + solverBestFitModel.model.predict(9000));
//        System.out.println();
//
//        System.out.println("Divider best fit model: " + dividerBestFitModel.modelName);
//        System.out.println("Error: " + dividerBestFitModel.error);
//        System.out.println();
//
//        System.out.println("Combiner best fit model: " + combinerBestFitModel.modelName);
//        System.out.println("Error: " + combinerBestFitModel.error);
//        System.out.println();


        //Create a model object that abstracts the underlying model and just returns an estimated runtime for a given complexity
    }
}
