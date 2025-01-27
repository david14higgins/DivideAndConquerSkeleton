import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

public abstract class DaCSkeletonAbstract<P, S> {
    // Abstract methods to be implemented by subclasses
    protected abstract Function<P, S> getProblemSolver();
    protected abstract Function<P, List<P>> getSubproblemGenerator();
    protected abstract Function<List<S>, S> getSolutionCombiner();
    protected abstract Function<P, Integer> getProblemQuantifier();
    protected abstract Function<Integer, P> getProblemGenerator();
    protected abstract int getGranularity();

    private ModelFitter.BestFitModel solverBestFitModel;
    private ModelFitter.BestFitModel dividerBestFitModel;
    private ModelFitter.BestFitModel combinerBestFitModel;

    public void probeSkeletonImplementation() {
        HashMap<Integer, Long> solverRuntimes = new HashMap<>();
        HashMap<Integer, Long> dividerRuntimes = new HashMap<>();
        HashMap<Integer, Long> combinerRuntimes = new HashMap<>();

        try {
            // Build the command to start a new Java process
            String javaHome = System.getProperty("java.home");
            String javaExec = javaHome + "/bin/java";
            String className = getClass().getName();

            System.out.println(className);

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

        solverBestFitModel = modelFitter.fitModel(solverRuntimes);
        dividerBestFitModel = modelFitter.fitModel(dividerRuntimes);
        combinerBestFitModel = modelFitter.fitModel(combinerRuntimes);
    }

    private int calculateGranularityNaive(P problem) {
        double lowestRuntime = Double.MAX_VALUE;
        int problemSize = getProblemQuantifier().apply(problem);
        int bestGranularity = problemSize;
        for (int granularity = 1; granularity <= problemSize; granularity++) {
            double estimatedRuntime = estimateRuntimeWithGranularity(problem, granularity);
            if (estimatedRuntime < lowestRuntime) {
                lowestRuntime = estimatedRuntime;
                bestGranularity = granularity;
            }
        }
        System.out.println("Granularity Selected: " + bestGranularity);
        return bestGranularity;
    }

//    private double estimateRuntimeWithGranularity(P problem, int granularity) {
//        int problemSize = getProblemQuantifier().apply(problem);
//        if (problemSize < granularity) {
//            return solverBestFitModel.model.predict(problemSize);
//        } else {
//            ArrayList<P> subproblems = (ArrayList<P>) getSubproblemGenerator().apply(problem);
//            int numSubproblems = subproblems.size();
//            return dividerBestFitModel.model.predict(problemSize) + combinerBestFitModel.model.predict(problemSize) +
//                    numSubproblems * estimateRuntimeWithGranularity(subproblems.get(0), granularity);
//        }
//    }

    //Opting to not use recursion because we want a global state of the problem execution
    private double estimateRuntimeWithGranularity(P problem, int granularity) {
        P currentProblem = problem;
        int currentProblemSize = getProblemQuantifier().apply(currentProblem);
        int activeSubproblems = 1;
        int parallelism = 8; //Hardcoded for now
        boolean terminate = false;
        double estimatedRuntime = 0;

        while(!terminate) {
            System.out.println("Current Problem Size: " + currentProblemSize);
            if (currentProblemSize <= granularity) { // Base solutions
                double solverSequentialRuntime = solverBestFitModel.model.predict(currentProblemSize);
                estimatedRuntime += solverSequentialRuntime * activeSubproblems / Math.min(activeSubproblems, parallelism);
                terminate = true;
            } else { // Divide and Combine costs
                double dividerSequentialRuntime = dividerBestFitModel.model.predict(currentProblemSize);
                double combinerSequentialRuntime = combinerBestFitModel.model.predict(currentProblemSize);

                estimatedRuntime += dividerSequentialRuntime * activeSubproblems / Math.min(activeSubproblems, parallelism);
                estimatedRuntime += combinerSequentialRuntime * activeSubproblems / Math.min(activeSubproblems, parallelism);

                ArrayList<P> subproblems = (ArrayList<P>) getSubproblemGenerator().apply(currentProblem);
                activeSubproblems *= subproblems.size();
                currentProblem = subproblems.get(0);
                currentProblemSize = getProblemQuantifier().apply(currentProblem);
            }
        }
        System.out.println("Estimated Runtime: " + estimatedRuntime);
        return estimatedRuntime;
    }
    /* ------ ASSUMPTIONS -------
     - Divider splits up problems into evenly sized subproblems
     - All threads in the ForkJoinPool are used
     - We need to make assumptions on how work is split between threads
     - We make a consistent number of subproblems on division (not necessarily true)
     - Subproblems are of equal size
     - We do not consider input complexity
     */

    public S DaCSolve(P problem) {
        if(solverBestFitModel!= null && dividerBestFitModel != null && combinerBestFitModel != null) {
            int granularity = calculateGranularityNaive(problem);
            ForkJoinPool pool = new ForkJoinPool();
            System.out.println("Pool Size: " + pool.getPoolSize());
            System.out.println("Parallelism: " + pool.getParallelism());
            DaCRecursiveTask<P, S> daCRecursiveTask = new DaCRecursiveTask<>(
                    getProblemSolver(),
                    getSubproblemGenerator(),
                    getSolutionCombiner(),
                    getProblemQuantifier(),
                    problem,
                    granularity
            );
            return pool.invoke(daCRecursiveTask);
        } else {
            System.out.println("Skeleton implementation must be probed before DaC applied");
            return null;
        }
    }

}
