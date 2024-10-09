import java.util.ArrayList;
import java.util.function.Function;
import java.util.List;

public class DivideAndConquerTask<P, S> {

    private final int granularity = 20;

    private Function<P, S> problemSolver;

    private Function<P, List<P>> subproblemGenerator;

    private Function<List<S>, S> solutionCombiner;

    private Function<P, Integer> problemQuantifier;

    public DivideAndConquerTask(Function<P, S> problemSolver,
                                Function<P, List<P>> subproblemGenerator,
                                Function<List<S>, S> solutionCombiner,
                                Function<P, Integer> problemQuantifier) {
        this.problemSolver = problemSolver;
        this.subproblemGenerator = subproblemGenerator;
        this.solutionCombiner = solutionCombiner;
        this.problemQuantifier = problemQuantifier;
    }

    public S solveProblem(P problem) {
        if (problemQuantifier.apply(problem) <= granularity) {
            return problemSolver.apply(problem);
        } else {
            List<P> subproblems = this.subproblemGenerator.apply(problem);

            List<S> solutions = new ArrayList<>();
            for (P subproblem : subproblems) {
                S solution = solveProblem(subproblem);
                solutions.add(solution);
            }

            return solutionCombiner.apply(solutions);
        }
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
