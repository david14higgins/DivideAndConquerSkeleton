import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
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

            // Retrieve problem methods
            prober.readProblemSolver(clazz, instance);
            prober.readSubproblemGenerator(clazz, instance);
            prober.readSolutionCombiner(clazz, instance);
            prober.readProblemQuantifier(clazz, instance);
            prober.readProblemGenerator(clazz, instance);

            prober.probingAlgorithm();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void probingAlgorithm() {
        // Probing parameters
        final int MAX_SAMPLES = 100, ITERATIONS_PER_GRANULARITY = 3, TIMEOUT = 120;

        // Runtime state
        boolean timeoutTriggered = false;
        int problemQuantity = 1, numSamples = 0;


        while (!timeoutTriggered && numSamples <= MAX_SAMPLES) {
            // Generate the problem
            P problem = problemGenerator.apply(problemQuantity);

            long solverAccumulativeRuntime = 0;
            long dividerAccumulativeRuntime = 0;
            long combinerAccumulativeRuntime = 0;

            for (int i = 0; i < ITERATIONS_PER_GRANULARITY; i++) {
                ExecutorService executor = Executors.newSingleThreadExecutor();

                // Measure `problemSolver`
                Future<S> solverFuture = executor.submit(() -> problemSolver.apply(problem));
                try {
                    long solverStart = System.nanoTime();
                    S solution = solverFuture.get(TIMEOUT, TimeUnit.SECONDS);
                    solverAccumulativeRuntime += System.nanoTime() - solverStart;

                    // Measure `subproblemGenerator`
                    long subproblemStart = System.nanoTime();
                    List<P> subproblems = subproblemGenerator.apply(problem);
                    dividerAccumulativeRuntime += System.nanoTime() - subproblemStart;

                    // Measure `solutionCombiner`
                    List<S> subproblemSolutions = subproblems.stream().map(problemSolver).toList();
                    long combinerStart = System.nanoTime();
                    S combinedSolution = solutionCombiner.apply(subproblemSolutions);
                    combinerAccumulativeRuntime += System.nanoTime() - combinerStart;

                } catch (TimeoutException e) {
                    System.out.println("Timeout occurred for problem quantity: " + problemQuantity);
                    timeoutTriggered = true;
                    solverFuture.cancel(true);
                    executor.shutdownNow();
                    System.exit(-1);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                } finally {
                    executor.shutdownNow();
                }
            }

            // Calculate average runtimes
            long solverAvgRuntime = solverAccumulativeRuntime / ITERATIONS_PER_GRANULARITY;
            long subproblemAvgRuntime = dividerAccumulativeRuntime / ITERATIONS_PER_GRANULARITY;
            long combinerAvgRuntime = combinerAccumulativeRuntime / ITERATIONS_PER_GRANULARITY;

            System.out.printf("SOLVER: %d %d%n",problemQuantity, solverAvgRuntime);
            System.out.printf("DIVIDER: %d %d%n", problemQuantity, subproblemAvgRuntime);
            System.out.printf("COMBINER: %d %d%n", problemQuantity, combinerAvgRuntime);

            problemQuantity++;
            numSamples++;
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


}