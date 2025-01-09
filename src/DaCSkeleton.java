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


//            ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "your-application.jar", "YourSolverClass", problem.toString());
}
