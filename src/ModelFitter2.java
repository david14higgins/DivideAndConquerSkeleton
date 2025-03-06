//import org.apache.commons.math4.legacy.fitting.*;
//import org.apache.commons.math4.legacy.analysis.function.*;
//import org.apache.commons.math4.legacy.stat.regression.SimpleRegression;
import org.apache.commons.math4.legacy.fitting.*;
import org.apache.commons.math4.legacy.stat.regression.*;
import java.util.*;

public class ModelFitter2 {
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
            return cache.getOrDefault(x, baseModel.predict(x));
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

    public ModelFitter2(Map<Integer, Long> data) {
        this.data = data;
    }

    private long calculateError(Model model) {
        return data.entrySet().stream()
                .mapToLong(e -> {
                    long predicted = model.predict(e.getKey());
                    return (predicted - e.getValue()) * (predicted - e.getValue());
                })
                .sum();
    }

    public static Model constantModel(Map<Integer, Long> data) {
        double avg = data.values().stream().mapToLong(Long::longValue).average().orElse(0);
        return x -> (long) avg;
    }

    public static Model logarithmicModel(Map<Integer, Long> data) {
        WeightedObservedPoints points = new WeightedObservedPoints();
        for (var entry : data.entrySet()) {
            if (entry.getKey() > 0 && entry.getValue() > 0)
                points.add(Math.log(entry.getKey()), entry.getValue());
        }
        if (points.toList().isEmpty()) return x -> 0;

        SimpleRegression regression = new SimpleRegression();
        points.toList().forEach(p -> regression.addData(p.getX(), p.getY()));
        double a = regression.getIntercept();
        double b = regression.getSlope();
        return x -> (long) (a + b * Math.log(x));
    }

    public static Model linearModel(Map<Integer, Long> data) {
        SimpleRegression regression = new SimpleRegression();
        data.forEach((x, y) -> regression.addData(x, y));
        double a = regression.getIntercept();
        double b = regression.getSlope();
        return x -> (long) (a + b * x);
    }

    public static Model exponentialModel(Map<Integer, Long> data) {
        WeightedObservedPoints points = new WeightedObservedPoints();
        for (var entry : data.entrySet()) {
            if (entry.getValue() > 0)
                points.add(entry.getKey(), Math.log(entry.getValue()));
        }
        if (points.toList().isEmpty()) return x -> 0;

        SimpleRegression regression = new SimpleRegression();
        points.toList().forEach(p -> regression.addData(p.getX(), p.getY()));
        double a = Math.exp(regression.getIntercept());
        double b = regression.getSlope();
        return x -> (long) (a * Math.exp(b * x));
    }

    public static Model rootModel(Map<Integer, Long> data) {
        WeightedObservedPoints points = new WeightedObservedPoints();
        for (var entry : data.entrySet()) {
            points.add(Math.sqrt(entry.getKey()), entry.getValue());
        }
        if (points.toList().isEmpty()) return x -> 0;

        SimpleRegression regression = new SimpleRegression();
        points.toList().forEach(p -> regression.addData(p.getX(), p.getY()));
        double a = regression.getIntercept();
        double b = regression.getSlope();
        return x -> (long) (a + b * Math.sqrt(x));
    }

    public BestFitModel fitModel() {
        List<Map.Entry<String, Model>> models = List.of(
                new AbstractMap.SimpleEntry<>("Constant", constantModel(data)),
                new AbstractMap.SimpleEntry<>("Logarithmic", logarithmicModel(data)),
                new AbstractMap.SimpleEntry<>("Linear", linearModel(data)),
                new AbstractMap.SimpleEntry<>("Exponential", exponentialModel(data)),
                new AbstractMap.SimpleEntry<>("Root", rootModel(data))
        );

        return models.stream()
                .map(entry -> new BestFitModel(new CachedModel(entry.getValue(), data), calculateError(entry.getValue()), entry.getKey()))
                .min(Comparator.comparingLong(model -> model.error))
                .orElse(null);
    }

    public static BestFitModel modelFromFile(Map<Integer, Long> data, String modelName) {
        return switch (modelName) {
            case "Constant" -> new BestFitModel(new CachedModel(constantModel(data), data), 0, modelName);
            case "Logarithmic" -> new BestFitModel(new CachedModel(logarithmicModel(data), data), 0, modelName);
            case "Linear" -> new BestFitModel(new CachedModel(linearModel(data), data), 0, modelName);
            case "Exponential" -> new BestFitModel(new CachedModel(exponentialModel(data), data), 0, modelName);
            case "Root" -> new BestFitModel(new CachedModel(rootModel(data), data), 0, modelName);
            default -> {
                System.out.println("Model not found");
                yield null;
            }
        };
    }
}
