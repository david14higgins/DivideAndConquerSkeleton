import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class DaCRecursiveTask <P, S> extends RecursiveTask<S> {

    private final Function<P, S> problemSolver;

    private final Function<P, List<P>> subproblemGenerator;

    private final Function<List<S>, S> solutionCombiner;

    private final Function<P, Integer> problemQuantifier;

    private final P problem;

    public DaCRecursiveTask(Function<P, S> problemSolver,
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
}
