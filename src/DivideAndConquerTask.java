import java.util.function.Function;

public interface DivideAndConquerTask {

    //T represents the problem type whilst R represents the solution type
    <T, R> void baseSolve(Function<T, R> solver);

//    void divide();
//
//    void joinSolution();

}
