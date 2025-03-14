import java.util.*;
public class ModelFitter {
    public interface Model {
        long predict(int x);
    }

    public static class CachedModel implements Model {
        private final Model baseModel;
        public final Map<Integer, Long> cache;

        public CachedModel(Model baseModel, Map<Integer, Long> cache) {
            this.baseModel = baseModel;
            this.cache = cache;
        }

        @Override
        public long predict(int x) {
            if (cache.containsKey(x)) {
                return cache.get(x); // Use cached value if available
            } else {
                return baseModel.predict(x); // Otherwise, predict using the model
            }
        }
    }

    public static class BestFitModel {
        public CachedModel cachedModel;
        public long error;
        public String modelName;

        public BestFitModel(CachedModel cachedModel, long error, String modelName) {
            this.cachedModel = cachedModel;
            this.error = error;
            this.modelName = modelName;
        }
    }

    private final Map<Integer, Long> data;

    public ModelFitter(Map<Integer, Long> data) {
        this.data = data;
    }

    public long calculateError(Model model) {
        long error = 0;
        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long y = entry.getValue();
            long predicted = model.predict(x);
            error += (predicted - y) * (predicted - y);
        }
        return error;
    }

    public static Model constantModel(Map<Integer, Long> data) {
        long constant = (long) data.values().stream().mapToLong(Long::longValue).average().orElse(0);
        return x -> constant;
    }

    public static Model logarithmicModel(Map<Integer, Long> data) {
        List<Double> xLog = new ArrayList<>();
        List<Long> y = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long yVal = entry.getValue();
            if (x > 0 && yVal > 0) {
                xLog.add(Math.log(x));
                y.add(yVal);
            }
        }

        int n = xLog.size();
        if (n == 0) return x -> 0;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = xLog.get(i);
            long yVal = y.get(i);
            sumX += x;
            sumY += yVal;
            sumXY += x * yVal;
            sumX2 += x * x;
        }

        double b = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double a = (sumY - b * sumX) / n;

        return x -> (long) (a * Math.log(x) + b);
    }

    public static Model linearModel(Map<Integer, Long> data) {
        long sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int n = data.size();

        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long y = entry.getValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double m = (n * sumXY - sumX * sumY) / (double) (n * sumX2 - sumX * sumX);
        double b = (sumY - m * sumX) / (double) n;

        return x -> (long) (m * x + b);
    }

    public static Model exponentialModel(Map<Integer, Long> data) {
        long sumX = 0, sumLogY = 0, sumXY = 0, sumX2 = 0;
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

        return x -> (long) (a * Math.exp(b * x));
    }

    public static Model rootModel(Map<Integer, Long> data) {
        long sumX = 0, sumY = 0, sumX2 = 0, sumXY = 0;
        int n = data.size();

        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            int x = entry.getKey();
            long y = entry.getValue();
            sumX += x;
            sumY += y;
            sumX2 += x * x;
            sumXY += x * y;
        }

        double a = (n * sumXY - sumX * sumY) / (double) (n * sumX2 - sumX * sumX);
        double b = (sumY - a * sumX) / (double) n;

        return x -> (long) (a * Math.sqrt(x) + b);
    }

    public BestFitModel fitModel() {
        List<Map.Entry<String, Model>> models = List.of(
                new AbstractMap.SimpleEntry<>("Constant", constantModel(data)),
                new AbstractMap.SimpleEntry<>("Logarithmic", logarithmicModel(data)),
                new AbstractMap.SimpleEntry<>("Linear", linearModel(data)),
                new AbstractMap.SimpleEntry<>("Exponential", exponentialModel(data)),
                new AbstractMap.SimpleEntry<>("Root", rootModel(data))
        );

        BestFitModel bestFit = null;
        long minError = Long.MAX_VALUE;

        for (Map.Entry<String, Model> entry : models) {
            Model model = entry.getValue();
            long error = calculateError(model);
            if (error < minError) {
                minError = error;
                bestFit = new BestFitModel(new CachedModel(model, data), minError, entry.getKey());
            }
        }

        return bestFit;
    }

    public static BestFitModel modelFromFile(Map<Integer, Long> data, String modelName) {
        return switch (modelName) {
            case ("Constant") -> new BestFitModel(new CachedModel(constantModel(data), data), 0, modelName);
            case ("Logarithmic") -> new BestFitModel(new CachedModel(logarithmicModel(data), data), 0, modelName);
            case ("Linear") -> new BestFitModel(new CachedModel(linearModel(data), data), 0, modelName);
            case ("Exponential") -> new BestFitModel(new CachedModel(exponentialModel(data), data), 0, modelName);
            case ("Root") -> new BestFitModel(new CachedModel(rootModel(data), data), 0, modelName);
            default -> {
                System.out.println("Model not found");
                yield null;
            }
        };
    }
}