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

    // Concrete method to solve a problem
    public S solveProblem(P problem) {
        ForkJoinPool pool = new ForkJoinPool();
        DaCRecursiveTask<P, S> daCRecursiveTask = new DaCRecursiveTask<>(
                getProblemSolver(),
                getSubproblemGenerator(),
                getSolutionCombiner(),
                getProblemQuantifier(),
                problem,
                getGranularity()
        );
        return pool.invoke(daCRecursiveTask);
    }
}
