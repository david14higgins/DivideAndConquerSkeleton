import java.util.HashMap;
import java.util.Map;

public class ModelTesting {

    public void testExponentialModels() {
        // Example test dataset (x, y)
        Map<Integer, Long> testData = new HashMap<>();
        testData.put(1, 2L);
        testData.put(2, 5L);
        testData.put(3, 15L);
        testData.put(4, 30L);
        testData.put(5, 80L);

        // Create ModelFitter instance
        ModelFitter fitter = new ModelFitter(testData);
        ModelFitter2 fitter2 = new ModelFitter2(testData);

        // Get the exponential model
        ModelFitter.Model expModel = ModelFitter.exponentialModel(testData);
        ModelFitter2.Model expModel2 = ModelFitter2.exponentialModel(testData);


        // Calculate error
        long error = fitter.calculateError(expModel);
        long error2 = fitter2.calculateError(expModel2);

        System.out.println("Exponential Error: " + error);
        System.out.println("Exponential Error2: " + error2);
    }

    public void testLinearModel() {
        // Example test dataset (x, y)
        Map<Integer, Long> testData = new HashMap<>();
        testData.put(1, 2L);
        testData.put(2, 5L);
        testData.put(3, 8L);
        testData.put(4, 11L);
        testData.put(5, 14L);

        // Create ModelFitter instance
        ModelFitter fitter = new ModelFitter(testData);
        ModelFitter.Model linearModel = ModelFitter.linearModel(testData);

        ModelFitter2 fitter2 = new ModelFitter2(testData);
        ModelFitter2.Model linearModel2 = ModelFitter2.linearModel(testData);

        // Calculate error
        long error = fitter.calculateError(linearModel);
        long error2 = fitter2.calculateError(linearModel2);

        System.out.println("Linear Model Error: " + error);
        System.out.println("Linear Model Error2: " + error2);
    }

    public void testLogarithmicModel() {
        // Example test dataset (x, y)
        Map<Integer, Long> testData = new HashMap<>();
        testData.put(1, 2L);
        testData.put(2, 3L);
        testData.put(3, 5L);
        testData.put(4, 7L);
        testData.put(5, 8L);

        // Create ModelFitter instance
        ModelFitter fitter = new ModelFitter(testData);
        ModelFitter.Model logModel = ModelFitter.logarithmicModel(testData);

        ModelFitter2 fitter2 = new ModelFitter2(testData);
        ModelFitter2.Model logModel2 = ModelFitter2.logarithmicModel(testData);

        // Calculate error
        long error = fitter.calculateError(logModel);
        long error2 = fitter2.calculateError(logModel2);

        System.out.println("Logarithmic Model Error: " + error);
        System.out.println("Logarithmic Model Error2: " + error2);
    }

    public void testConstantModel() {
        // Example test dataset (x, y)
        Map<Integer, Long> testData = new HashMap<>();
        testData.put(1, 5L);
        testData.put(2, 5L);
        testData.put(3, 5L);
        testData.put(4, 5L);
        testData.put(5, 5L);

        // Create ModelFitter instance
        ModelFitter fitter = new ModelFitter(testData);
        ModelFitter.Model constantModel = ModelFitter.constantModel(testData);

        ModelFitter2 fitter2 = new ModelFitter2(testData);
        ModelFitter2.Model constantModel2 = ModelFitter2.constantModel(testData);

        // Calculate error
        long error = fitter.calculateError(constantModel);
        long error2 = fitter2.calculateError(constantModel2);

        System.out.println("Constant Model Error: " + error);
        System.out.println("Constant Model Error2: " + error2);
    }

    public void testRootModel() {
        // Example test dataset (x, y)
        Map<Integer, Long> testData = new HashMap<>();
        testData.put(1, 2L);
        testData.put(4, 4L);
        testData.put(9, 6L);
        testData.put(16, 8L);
        testData.put(25, 10L);

        // Create ModelFitter instance
        ModelFitter fitter = new ModelFitter(testData);
        ModelFitter.Model rootModel = ModelFitter.rootModel(testData);

        ModelFitter2 fitter2 = new ModelFitter2(testData);
        ModelFitter2.Model rootModel2 = ModelFitter2.rootModel(testData);

        // Calculate error
        long error = fitter.calculateError(rootModel);
        long error2 = fitter2.calculateError(rootModel2);

        System.out.println("Root Model Error: " + error);
        System.out.println("Root Model Error2: " + error2);
    }



}
