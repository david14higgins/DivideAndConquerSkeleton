import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class Main {


    public static void main(String[] args) {
        MergeSortImpl mergeSortImpl = new MergeSortImpl();
        mergeSortImpl.probeSkeletonImplementation();
        int[] result = mergeSortImpl.DaCSolve(new int[] {1, 5, 8, 10, 12, 15, 3, 4});


        //StrassensMatrixMultiplicationImpl strassensMatrixMultiplication = new StrassensMatrixMultiplicationImpl();
        //probeSkeletonImplementation(strassensMatrixMultiplication);
        //Do stuff with skeleton
    }


}
