import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class ImplementationRuntimeLogger {
    public static String hashFile(Path filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(filePath);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            return null; // Or return "ERROR" or another indicator
        }
    }

    public void saveData(DaCSkeletonAbstract<?, ?> skeleton, ModelFitter.BestFitModel model, Map<Integer, Long> runtimeData) {
        String fileName = hashFile(Path.of(skeleton.getClass().getName())) + ".dat";
        System.out.println("Saving to file: " + fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(skeleton.solverBestFitModel.modelName); // Save the solver model
            oos.writeObject(skeleton.solverBestFitModel.cachedModel); // Save the divider model
            oos.writeObject(skeleton.dividerBestFitModel.modelName); // Save the combiner model
            oos.writeObject(skeleton.dividerBestFitModel.cachedModel); // Save the solver runtimes
            oos.writeObject(skeleton.combinerBestFitModel.modelName); // Save the divider runtimes
            oos.writeObject(skeleton.combinerBestFitModel.cachedModel); // Save the combiner runtimes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
