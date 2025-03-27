public class MergeSortEvaluation {

    public static void evaluateMergeSort() {
        MergeSort mergeSort = new MergeSort();
        int[] values = randomArrayGenerator(16777216);
        mergeSort.DaCSolve(values);
    }

    private static int[] randomArrayGenerator(int size) {
        int[] values = new int[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) (Math.random() * 101);
        }
        return values;
    }
}
