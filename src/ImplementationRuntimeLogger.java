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

    public static void saveData(DaCSkeletonAbstract<?, ?> skeleton) {
        // Define the directory path and file name
        Path dirPath = Path.of("ImplementationLogs");
        Path filePath = dirPath.resolve(hashFile(Path.of("src/" + skeleton.getClass().getName() + ".java")) + ".dat");

        // Create the directory if it doesn't exist
        try {
            Files.createDirectories(dirPath); // This ensures the directory exists
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exit if directory creation fails
        }

        // Write to the file in the "ImplementationLogs" directory
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(skeleton.solverBestFitModel.modelName); // Save the solver model
            oos.writeObject(skeleton.solverBestFitModel.cachedModel.cache); // Save the divider model
            oos.writeObject(skeleton.dividerBestFitModel.modelName); // Save the combiner model
            oos.writeObject(skeleton.dividerBestFitModel.cachedModel.cache); // Save the solver runtimes
            oos.writeObject(skeleton.combinerBestFitModel.modelName); // Save the divider runtimes
            oos.writeObject(skeleton.combinerBestFitModel.cachedModel.cache); // Save the combiner runtimes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
