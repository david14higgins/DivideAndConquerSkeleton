import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MergeSort extends DaCSkeleton<int[], int[]> {

    @Override
    protected Function<int[], int[]> getProblemSolver() {
        return MergeSort::bubblesort;
    }

    private static int[] bubblesort(int[] array) {
        int n = array.length;
        // Create a copy of the original array to sort
        int[] sortedArray = Arrays.copyOf(array, n);

        boolean swapped;

        // Outer loop for each pass through the array
        for (int i = 0; i < n - 1; i++) {
            swapped = false;

            // Inner loop for comparing adjacent elements
            for (int j = 0; j < n - 1 - i; j++) {
                if (sortedArray[j] > sortedArray[j + 1]) {
                    // Swap if the elements are in the wrong order
                    int temp = sortedArray[j];
                    sortedArray[j] = sortedArray[j + 1];
                    sortedArray[j + 1] = temp;

                    // Set swapped to true, indicating that a swap occurred
                    swapped = true;
                }
            }

            // If no swaps occurred during the pass, the array is already sorted
            if (!swapped) {
                break;
            }
        }
        // Return the sorted array
        return sortedArray;
    }

    @Override
    protected Function<int[], List<int[]>> getSubproblemGenerator() {
        return MergeSort::splitArray;
    }

    private static List<int[]> splitArray(int[] array) {
        int mid = array.length / 2;

        int[] leftArray = new int[mid];
        int[] rightArray = new int[array.length - mid];

        //Copy data
        for(int i = 0; i < array.length; i++) {
            if (i < mid) {
                leftArray[i] = array[i];
            } else {
                rightArray[i - mid] = array[i];
            }
        }

        List<int[]> result = new ArrayList<>();
        result.add(leftArray);
        result.add(rightArray);

        return result;
    }

    @Override
    protected Function<List<int[]>, int[]> getSolutionCombiner() {
        return MergeSort::joinArray;
    }

    private static int[] joinArray(List<int[]> listOfArrays) {
        int[] leftArray = listOfArrays.get(0);
        int[] rightArray = listOfArrays.get(1);

        int[] result = new int[leftArray.length + rightArray.length];

        int i = 0, j = 0, k = 0;

        while(i < leftArray.length && j < rightArray.length) {
            if (leftArray[i] <= rightArray[j]) {
                result[k] = leftArray[i];
                i++;
            } else {
                result[k] = rightArray[j];
                j++;
            }
            k++;
        }

        //Copy remaining elements
        while(i < leftArray.length) {
            result[k] = leftArray[i];
            i++;
            k++;
        }

        while(j < rightArray.length) {
            result[k] = rightArray[j];
            j++;
            k++;
        }

        return result;
    }

    @Override
    protected Function<int[], Integer> getProblemQuantifier() {
        return MergeSort::arraySizeQuantifier;
    }

    private static int arraySizeQuantifier(int[] array) {
        return array.length;
    }


    @Override
    protected Function<Integer, int[]> getProblemGenerator() {
        return MergeSort::randomArrayGenerator;
    }

    private static int[] randomArrayGenerator(int size) {
        int[] values = new int[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) (Math.random() * 101);
        }
        return values;
    }
}
