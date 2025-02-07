import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

public abstract class DaCSkeleton<P, S> {
    // Abstract methods to be implemented by subclasses
    protected abstract Function<P, S> getProblemSolver();
    protected abstract Function<P, List<P>> getSubproblemGenerator();
    protected abstract Function<List<S>, S> getSolutionCombiner();
    protected abstract Function<P, Integer> getProblemQuantifier();
    protected abstract Function<Integer, P> getProblemGenerator();

    private ModelFitter2.BestFitModel solverBestFitModel;
    private ModelFitter2.BestFitModel dividerBestFitModel;
    private ModelFitter2.BestFitModel combinerBestFitModel;

    public DaCSkeleton() {

    }

    public void probeSkeletonImplementation() {
        HashMap<Integer, Long> solverRuntimes = new HashMap<>();
        HashMap<Integer, Long> dividerRuntimes = new HashMap<>();
        HashMap<Integer, Long> combinerRuntimes = new HashMap<>();

        try {
            // Build the command to start a new Java process
            String javaHome = System.getProperty("java.home");
            String javaExec = javaHome + "/bin/java";
            String className = getClass().getName();

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

            //System.out.println("Still executing in Main.java");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Output read values
        System.out.println(solverRuntimes);
        System.out.println(dividerRuntimes);
        System.out.println(combinerRuntimes);

        // Create an instance of ModelFitter with the given data
        ModelFitter2 modelFitterSolver = new ModelFitter2(solverRuntimes);
        ModelFitter2 modelFitterDivider = new ModelFitter2(dividerRuntimes);
        ModelFitter2 modelFitterCombiner = new ModelFitter2(combinerRuntimes);

        // Fit the models for each runtime dataset
        solverBestFitModel = modelFitterSolver.fitModel();
        dividerBestFitModel = modelFitterDivider.fitModel();
        combinerBestFitModel = modelFitterCombiner.fitModel();
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
            if (currentProblemSize <= granularity) { // Base solutions
                double solverSequentialRuntime = solverBestFitModel.cachedModel.predict(currentProblemSize);
                estimatedRuntime += solverSequentialRuntime * activeSubproblems / Math.min(activeSubproblems, parallelism);
                terminate = true;
            } else { // Divide and Combine costs
                double dividerSequentialRuntime = dividerBestFitModel.cachedModel.predict(currentProblemSize);
                double combinerSequentialRuntime = combinerBestFitModel.cachedModel.predict(currentProblemSize);

                estimatedRuntime += dividerSequentialRuntime * activeSubproblems / Math.min(activeSubproblems, parallelism);
                estimatedRuntime += combinerSequentialRuntime * activeSubproblems / Math.min(activeSubproblems, parallelism);

                ArrayList<P> subproblems = (ArrayList<P>) getSubproblemGenerator().apply(currentProblem);
                activeSubproblems *= subproblems.size();
                currentProblem = subproblems.get(0);
                currentProblemSize = getProblemQuantifier().apply(currentProblem);
            }
        }
        //System.out.println("Granularity: " + granularity + ", Estimated Runtime: " + estimatedRuntime);
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
        checkImplementationLogs();

        if(solverBestFitModel!= null && dividerBestFitModel != null && combinerBestFitModel != null) {
            int granularity = calculateGranularityNaive(problem);
            ForkJoinPool pool = new ForkJoinPool();
            DaCRecursiveTask<P, S> daCRecursiveTask = new DaCRecursiveTask<>(
                    getProblemSolver(),
                    getSubproblemGenerator(),
                    getSolutionCombiner(),
                    getProblemQuantifier(),
                    problem,
                    granularity
            );
            long startTime = System.nanoTime();
            S result = pool.invoke(daCRecursiveTask);
            long executionTime = System.nanoTime() - startTime;
            System.out.println("Actual Execution Time: " + executionTime);
            return result;
        } else {
            System.out.println("Skeleton implementation must be probed before DaC applied");
            return null;
        }
    }

    public S DaCSolveWithGranularity(P problem, int granularity) {
        if(solverBestFitModel!= null && dividerBestFitModel != null && combinerBestFitModel != null) {
            ForkJoinPool pool = new ForkJoinPool();
            DaCRecursiveTask<P, S> daCRecursiveTask = new DaCRecursiveTask<>(
                    getProblemSolver(),
                    getSubproblemGenerator(),
                    getSolutionCombiner(),
                    getProblemQuantifier(),
                    problem,
                    granularity
            );
            long startTime = System.nanoTime();
            S result = pool.invoke(daCRecursiveTask);
            long executionTime = System.nanoTime() - startTime;
            System.out.println("Granularity: " + granularity + ", Actual Execution Time: " + executionTime);
            return result;
        } else {
            System.out.println("Skeleton implementation must be probed before DaC applied");
            return null;
        }
    }

    private void checkImplementationLogs() {
        String className = getClass().getName();
        String logFileName = ImplementationRuntimeLogger.implementationLogged(className);
        if (logFileName != null) {
            System.out.println("Implementation logged, reading from log file: " + logFileName);
            ImplementationRuntimeLogger.readData(this);
        } else {
            System.out.println("Implementation not logged, probing skeleton");
            probeSkeletonImplementation();
            ImplementationRuntimeLogger.saveData(this);
        }
    }

    //--------- Getters and Setters ---------

    public ModelFitter2.BestFitModel getSolverBestFitModel() {
        return solverBestFitModel;
    }

    public ModelFitter2.BestFitModel getDividerBestFitModel() {
        return dividerBestFitModel;
    }

    public ModelFitter2.BestFitModel getCombinerBestFitModel() {
        return combinerBestFitModel;
    }

    public void setSolverBestFitModel(ModelFitter2.BestFitModel solverBestFitModel) {
        this.solverBestFitModel = solverBestFitModel;
    }

    public void setDividerBestFitModel(ModelFitter2.BestFitModel dividerBestFitModel) {
        this.dividerBestFitModel = dividerBestFitModel;
    }

    public void setCombinerBestFitModel(ModelFitter2.BestFitModel combinerBestFitModel) {
        this.combinerBestFitModel = combinerBestFitModel;
    }

}
