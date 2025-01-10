import java.util.*;
import java.util.function.Function;

public class ModelFitter {
    public interface Model {
        double predict(int x);
    }

    public static class BestFitModel {
        public Model model;
        public double error;
        public String modelName;  // Add model name

        public BestFitModel(Model model, double error, String modelName) {
            this.model = model;
            this.error = error;
            this.modelName = modelName;  // Store the model name
        }
    }

    // Helper function to compute the error (sum of squared differences between actual and predicted values)
    private static double calculateError(Map<Integer, Long> data, Model model) {
        double error = 0;
        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long y = entry.getValue();
            double predicted = model.predict(x);
            error += Math.pow(predicted - y, 2);  // Sum of squared errors
        }
        return error;
    }

    // Constant model
    public static Model constantModel(Map<Integer, Long> data) {
        double constant = data.values().stream().mapToLong(Long::longValue).average().orElse(0.0);
        return x -> constant;
    }

    // Logarithmic model using least squares regression
    public static Model logarithmicModel(Map<Integer, Long> data) {
        // Use log(x) vs y fitting method
        List<Double> xLog = new ArrayList<>();
        List<Double> y = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long yVal = entry.getValue();
            if (x > 0 && yVal > 0) { // Avoid log(0) and negative y values
                xLog.add(Math.log(x));
                y.add((double) yVal);
            }
        }

        int n = xLog.size();
        if (n == 0) return x -> 0; // Avoid empty data sets

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = xLog.get(i);
            double yVal = y.get(i);
            sumX += x;
            sumY += yVal;
            sumXY += x * yVal;
            sumX2 += x * x;
        }

        // Solving for a and b using least squares method
        double b = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double a = (sumY - b * sumX) / n;

        return x -> a * Math.log(x) + b;
    }

    // Linear model
    public static Model linearModel(Map<Integer, Long> data) {
        // Use linear regression to find the best fit y = mx + b
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int n = data.size();

        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long y = entry.getValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double b = (sumY - m * sumX) / n;

        return x -> m * x + b;
    }

    // Exponential model
    public static Model exponentialModel(Map<Integer, Long> data) {
        double sumX = 0, sumLogY = 0, sumXY = 0, sumX2 = 0;
        int n = data.size();

        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long y = entry.getValue();
            sumX += x;
            sumLogY += Math.log(y);
            sumXY += x * Math.log(y);
            sumX2 += x * x;
        }

        double b = (n * sumXY - sumX * sumLogY) / (n * sumX2 - sumX * sumX);
        double a = Math.exp((sumLogY - b * sumX) / n);

        return x -> a * Math.exp(b * x);
    }

    // Root model
    public static Model rootModel(Map<Integer, Long> data) {
        double sumX = 0, sumY = 0, sumX2 = 0, sumXY = 0;
        int n = data.size();

        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long y = entry.getValue();
            sumX += x;
            sumY += y;
            sumX2 += x * x;
            sumXY += x * y;
        }

        double a = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double b = (sumY - a * sumX) / n;

        return x -> a * Math.sqrt(x) + b;
    }

    // Main fitting method that will try each model and return the one with the lowest error
    public BestFitModel fitModel(Map<Integer, Long> data) {
        List<Map.Entry<String, Model>> models = List.of(
                new AbstractMap.SimpleEntry<>("Constant", constantModel(data)),
                new AbstractMap.SimpleEntry<>("Logarithmic", logarithmicModel(data)),
                new AbstractMap.SimpleEntry<>("Linear", linearModel(data)),
                new AbstractMap.SimpleEntry<>("Exponential", exponentialModel(data)),
                new AbstractMap.SimpleEntry<>("Root", rootModel(data))
        );

        BestFitModel bestFit = null;
        double minError = Double.MAX_VALUE;

        for (Map.Entry<String, Model> entry : models) {
            Model model = entry.getValue();
            double error = calculateError(data, model);
            if (error < minError) {
                minError = error;
                bestFit = new BestFitModel(model, minError, entry.getKey());  // Store model name
            }
        }

        return bestFit;
    }
}
