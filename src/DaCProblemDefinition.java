import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;
import java.util.List;

public class DaCProblemDefinition<P, S> extends RecursiveTask<S> {

    private final Function<P, S> problemSolver;

    private final Function<P, List<P>> subproblemGenerator;

    private final Function<List<S>, S> solutionCombiner;

    private final Function<P, Integer> problemQuantifier;

    private final P problem;

    public DaCProblemDefinition(Function<P, S> problemSolver,
                                Function<P, List<P>> subproblemGenerator,
                                Function<List<S>, S> solutionCombiner,
                                Function<P, Integer> problemQuantifier,
                                P problem) {
        this.problemSolver = problemSolver;
        this.subproblemGenerator = subproblemGenerator;
        this.solutionCombiner = solutionCombiner;
        this.problemQuantifier = problemQuantifier;
        this.problem = problem;
    }



//    public S solveProblem(P problem) {
//        if (problemQuantifier.apply(problem) <= granularity) {
//            return problemSolver.apply(problem);
//        } else {
//            List<P> subproblems = this.subproblemGenerator.apply(problem);
//
//            List<S> solutions = new ArrayList<>();
//            for (P subproblem : subproblems) {
//                S solution = solveProblem(subproblem);
//                solutions.add(solution);
//            }
//
//            return solutionCombiner.apply(solutions);
//        }
//        return compute();
//    }

    @Override
    protected S compute() {
        // Base case: if problem is small enough, solve directly
        int granularity = 1;
        if (problemQuantifier.apply(problem) <= granularity) {
            return problemSolver.apply(problem);
        } else {
            // Otherwise, divide the problem and solve subproblems in parallel
            List<P> subproblems = subproblemGenerator.apply(problem);
            List<DaCProblemDefinition<P, S>> tasks = new ArrayList<>();

            // Fork a task for each subproblem
            for (P subproblem : subproblems) {
                DaCProblemDefinition<P, S> task = new DaCProblemDefinition<>(problemSolver, subproblemGenerator, solutionCombiner, problemQuantifier, subproblem);
                tasks.add(task);
                task.fork(); // Asynchronously execute the task
            }

            // Collect results by joining tasks
            List<S> solutions = new ArrayList<>();
            for (DaCProblemDefinition<P, S> task : tasks) {
                solutions.add(task.join()); // Wait for task to complete and get result
            }

            // Combine results
            return solutionCombiner.apply(solutions);
        }
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
