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

    public void modelProblemSolver() {
        int maxProblemQuantity = 10;

        long[] durations = new long[10];

        for (int i = 1; i <= maxProblemQuantity; i++) {
            P generatedProblem = problemGenerator.apply(i);
            long startTime = System.nanoTime();
            S result = problemSolver.apply(generatedProblem);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            durations[i-1] = duration;
        }

        double durationScaleAccum = 0;
        for (int j = 0; j < durations.length - 1; j++) {
            double durationScale = (double) durations[j + 1] / durations[j];
            System.out.println("Duration Scale from quantity " + (j + 1) + " to " + (j + 2) + ": " + durationScale);
            durationScaleAccum += durationScale;
        }
        double averageDurationScale = durationScaleAccum / (durations.length - 1);
        System.out.println("Average duration scale: " + averageDurationScale);
    }
    
    public void measureProblemSolver() {
        int TIMEOUT = 100; // Timeout in seconds
        boolean timeoutTriggered = false;
        int problemQuantity = 1;

        // Data structure to store problem quantities and their corresponding runtimes
        Map<Integer, Double> runtimeData = new HashMap<>();

        while (!timeoutTriggered) {
            P problem = problemGenerator.apply(problemQuantity);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            int finalProblemQuantity = problemQuantity;

            // Start time measurement before submitting the task

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

                // Store the runtime in milliseconds in the map
                runtimeData.put(finalProblemQuantity, elapsedTime / 1_000_000.0);
            } catch (TimeoutException e) {
                System.out.println("Timeout occurred for problem quantity: " + problemQuantity);
                timeoutTriggered = true;

                // Cancel the task and forcibly terminate the program
                future.cancel(true); // Attempt to cancel the running task
                executor.shutdownNow(); // Forcefully shutdown the executor
            } catch (Exception e) {
                e.printStackTrace(); // Handle other exceptions
                break;
            } finally {
                if (!executor.isShutdown()) {
                    executor.shutdownNow(); // Ensure the executor is shut down
                }
            }

            problemQuantity += 1;
        }

        // Print the map of runtimes for each problem quantity
        System.out.println("All problem runtimes:");
        runtimeData.forEach((key, value) -> System.out.println("Problem Quantity: " + key + " Runtime (ms): " + value));
    }





    /*
        Consider building a problem generator
        This is the inverse to a problem quantifier
        It takes a problem quantity (an integer) and produces a sample problem input
        For example 1 -> 1x1 matrix, 2 -> 2x2 matrix
        This is another element that needs to be specified by the user
        However, once specified, our engine can generate problem inputs and run an algorithm to determine an appropriate
        level of granularity and parallelism
        Can then create a mapping of problem quantity to granularity parameters
        Should it be a mapping? Or simply one granularity value that is optimal for all problem quantities on a system
     */
}
