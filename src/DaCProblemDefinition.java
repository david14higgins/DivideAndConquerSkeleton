import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.List;

public class DaCProblemDefinition<P, S>{

    private final Function<P, S> problemSolver;

    private final Function<P, List<P>> subproblemGenerator;

    private final Function<List<S>, S> solutionCombiner;

    private final Function<P, Integer> problemQuantifier;


    public DaCProblemDefinition(Function<P, S> problemSolver,
                                Function<P, List<P>> subproblemGenerator,
                                Function<List<S>, S> solutionCombiner,
                                Function<P, Integer> problemQuantifier) {
        this.problemSolver = problemSolver;
        this.subproblemGenerator = subproblemGenerator;
        this.solutionCombiner = solutionCombiner;
        this.problemQuantifier = problemQuantifier;
    }

    public S solveProblem(P problem) {
        ForkJoinPool pool = new ForkJoinPool();
        DaCRecursiveTask<P, S> daCRecursiveTask = new DaCRecursiveTask<>(problemSolver, subproblemGenerator, solutionCombiner, problemQuantifier, problem);
        return pool.invoke(daCRecursiveTask);
    }

    /*
        Create ForkJoinPool version!
     */

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
