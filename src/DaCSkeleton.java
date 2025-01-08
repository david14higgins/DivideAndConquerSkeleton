import java.util.concurrent.*;
import java.util.function.Function;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class DaCSkeleton<P, S>{

    private final Function<P, S> problemSolver;

    private final Function<P, List<P>> subproblemGenerator;

    private final Function<List<S>, S> solutionCombiner;

    private final Function<P, Integer> problemQuantifier;

    private final Function<Integer, P> problemGenerator;

    private final int GRANULARITY;


    public DaCSkeleton(Function<P, S> problemSolver,
                       Function<P, List<P>> subproblemGenerator,
                       Function<List<S>, S> solutionCombiner,
                       Function<P, Integer> problemQuantifier,
                       Function<Integer, P> problemGenerator,
                       int granularity) {
        this.problemSolver = problemSolver;
        this.subproblemGenerator = subproblemGenerator;
        this.solutionCombiner = solutionCombiner;
        this.problemQuantifier = problemQuantifier;
        this.problemGenerator = problemGenerator;
        this.GRANULARITY = granularity;
    }

    public S solveProblem(P problem) {
        ForkJoinPool pool = new ForkJoinPool();
        DaCRecursiveTask<P, S> daCRecursiveTask = new DaCRecursiveTask<>(problemSolver, subproblemGenerator, solutionCombiner, problemQuantifier, problem, GRANULARITY);
        return pool.invoke(daCRecursiveTask);
    }

    public void invokeMethodProbe() {

    }

    public void measureProblemSolver() {
        int numRuntimes = 0;
        int MAX_RUNTIMES = 1000;
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
                    break;
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
//            ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "your-application.jar", "YourSolverClass", problem.toString());
}
