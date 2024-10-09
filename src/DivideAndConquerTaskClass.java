import java.util.ArrayList;
import java.util.function.Function;
import java.util.List;

public class DivideAndConquerTaskClass<P, S> {

    private final int granularity = 20;

    private Function<P, S> problemSolver;

    private Function<P, List<P>> subproblemGenerator;

    private Function<List<S>, S> solutionCombiner;

    private Function<P, Integer> problemQuantifier;

    public DivideAndConquerTaskClass(Function<P, S> problemSolver,
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


}
