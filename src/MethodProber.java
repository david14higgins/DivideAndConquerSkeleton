import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

public class MethodProber<P, S> {
    private Function<P, S> problemSolver;
    private Function<P, List<P>> subproblemGenerator;
    private Function<List<S>, S> solutionCombiner;
    private Function<P, Integer> problemQuantifier;
    private Function<Integer, P> problemGenerator;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Skeleton implementation classname required to run method prober");
            return;
        }

        try {
            System.out.println("Method Prober running");
            String className = args[0];

            // Load the class
            Class<?> clazz = Class.forName(className);

            // Create an instance of the class
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // Create MethodProber instance
            MethodProber<?, ?> prober = new MethodProber<>();

            // Run checks
            prober.readProblemSolver(clazz, instance);
            prober.readSubproblemGenerator(clazz, instance);
            prober.readSolutionCombiner(clazz, instance);
            prober.readProblemQuantifier(clazz, instance);
            prober.readProblemGenerator(clazz, instance);

            prober.measureProblemSolver();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readProblemSolver(Class<?> clazz, Object instance) {
        try {
            Method method = clazz.getDeclaredMethod("getProblemSolver");
            @SuppressWarnings("unchecked")
            Function<P, S> function = (Function<P, S>) method.invoke(instance);
            this.problemSolver = function;
        } catch (Exception e) {
            System.out.println("Error in getProblemSolver: " + e.getMessage());
        }
    }

    public void readSubproblemGenerator(Class<?> clazz, Object instance) {
        try {
            Method method = clazz.getDeclaredMethod("getSubproblemGenerator");
            @SuppressWarnings("unchecked")
            Function<P, List<P>> function = (Function<P, List<P>>) method.invoke(instance);
            this.subproblemGenerator = function;
        } catch (Exception e) {
            System.out.println("Error in getSubproblemGenerator: " + e.getMessage());
        }
    }

    public void readSolutionCombiner(Class<?> clazz, Object instance) {
        try {
            Method method = clazz.getDeclaredMethod("getSolutionCombiner");
            @SuppressWarnings("unchecked")
            Function<List<S>, S> function = (Function<List<S>, S>) method.invoke(instance);
            this.solutionCombiner = function;
        } catch (Exception e) {
            System.out.println("Error in getSolutionCombiner: " + e.getMessage());
        }
    }

    public void readProblemQuantifier(Class<?> clazz, Object instance) {
        try {
            Method method = clazz.getDeclaredMethod("getProblemQuantifier");
            @SuppressWarnings("unchecked")
            Function<P, Integer> function = (Function<P, Integer>) method.invoke(instance);
            this.problemQuantifier = function;
        } catch (Exception e) {
            System.out.println("Error in getProblemQuantifier: " + e.getMessage());
        }
    }

    public void readProblemGenerator(Class<?> clazz, Object instance) {
        try {
            Method method = clazz.getDeclaredMethod("getProblemGenerator");
            @SuppressWarnings("unchecked")
            Function<Integer, P> function = (Function<Integer, P>) method.invoke(instance);
            this.problemGenerator = function;
        } catch (Exception e) {
            System.out.println("Error in getProblemGenerator: " + e.getMessage());
        }
    }

    public void measureProblemSolver() {
        int numRuntimes = 0;
        int MAX_RUNTIMES = 100;
        int TIMEOUT = 30; // Timeout in seconds
        boolean timeoutTriggered = false;
        int problemQuantity = 1;
        int ITERATIONS_PER_GRANULARITY = 3;
        Long previousRuntime = null;

        // Data structure to store problem quantities and their corresponding runtimes
        Map<Integer, Long> runtimeData = new HashMap<>();

        while (!timeoutTriggered && numRuntimes <= MAX_RUNTIMES) {
            P problem = problemGenerator.apply(problemQuantity);

            long accumulativeRuntime = 0;
            for (int iterations = 0; iterations < ITERATIONS_PER_GRANULARITY; iterations++) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<?> future = executor.submit(() -> {
                    try {
                        // Call your function with the current input size
                        problemSolver.apply(problem);
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle any exceptions from problemSolver
                    }
                });

                try {
                    long startTime = System.nanoTime();

                    // Wait for the function to complete or timeout
                    future.get(TIMEOUT, TimeUnit.SECONDS);

                    // Measure end time after the task completes
                    long endTime = System.nanoTime();
                    long elapsedTime = endTime - startTime;

                    accumulativeRuntime += elapsedTime;
                } catch (TimeoutException e) {
                    System.out.println("Timeout occurred for problem quantity: " + problemQuantity);
                    timeoutTriggered = true;
                    // Cancel the task and forcibly terminate the program
                    future.cancel(true); // Attempt to cancel the running task
                    executor.shutdownNow(); // Forcefully shutdown the executor
                    System.exit(-1);
                } catch (Exception e) {
                    e.printStackTrace(); // Handle other exceptions
                    break;
                } finally {
                    if (!executor.isShutdown()) {
                        executor.shutdownNow(); // Ensure the executor is shut down
                    }
                }
            }
            long averageRuntime = accumulativeRuntime / ITERATIONS_PER_GRANULARITY;
            runtimeData.put(problemQuantity, averageRuntime);
            double multiplier = 1.0;
            if (previousRuntime != null) {
                multiplier = (double) averageRuntime / previousRuntime;
            }
            System.out.println("Problem Quantity: " + problemQuantity + ", Avg Runtime (ns): " + averageRuntime + ", Multiplier: " + multiplier + "x");



            problemQuantity += 1;
            numRuntimes += 1;
            previousRuntime = averageRuntime;
        }

        // Print the map of runtimes for each problem quantity
        System.out.println();
        System.out.println("All problem runtimes:");
        runtimeData.forEach((key, value) -> System.out.println("Problem Quantity: " + key + " Runtime (ns): " + value));
    }
}