import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

    public static String implementationLogged(String className) {
        // Check the ImplementationLogs directory exists
        Path dirPath = Path.of("ImplementationLogs");
        if(!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            return null;
        }

        Path fileName = dirPath.resolve(hashFile(Path.of("src/" + className + ".java")) + ".dat");

        if (Files.exists(fileName)) {
            return fileName.toString();
        } else {
            return null;
        }
    }

    public static void saveData(DaCSkeletonAbstract<?, ?> skeleton) {
        // Define the directory path and file name
        Path dirPath = Path.of("ImplementationLogs");
        Path fileName = dirPath.resolve(hashFile(Path.of("src/" + skeleton.getClass().getName() + ".java")) + ".dat");

        // Create the directory if it doesn't exist
        try {
            Files.createDirectories(dirPath); // This ensures the directory exists
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exit if directory creation fails
        }

        // Write to the file in the "ImplementationLogs" directory
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName.toFile()))) {
            oos.writeObject(skeleton.solverBestFitModel.modelName); // Save the solver model
            oos.writeObject(skeleton.solverBestFitModel.cachedModel.cache); // Save the divider model
            oos.writeObject(skeleton.dividerBestFitModel.modelName); // Save the combiner model
            oos.writeObject(skeleton.dividerBestFitModel.cachedModel.cache); // Save the solver runtimes
            oos.writeObject(skeleton.combinerBestFitModel.modelName); // Save the divider runtimes
            oos.writeObject(skeleton.combinerBestFitModel.cachedModel.cache); // Save the combiner runtimes
        } catch (IOException e) {
            e.printStackTrace();
        }

        readData(fileName);
    }

    public static void readData(DaCSkeletonAbstract<?, ?> skeleton) {

        
        // Ensure the file exists
        if (!Files.exists(filePath)) {
            System.out.println("File not found.");
            return null;
        }

        // Initialize the object to return
        DaCSkeletonAbstract<?, ?> skeleton = null;

        // Read the data from the file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            String solverModelName = (String) ois.readObject(); // Read the solver model name
            Map<Integer, Long> solverCache = (Map<Integer, Long>) ois.readObject(); // Read the solver cache
            String dividerModelName = (String) ois.readObject(); // Read the divider model name
            Map<Integer, Long> dividerCache = (Map<Integer, Long>) ois.readObject(); // Read the divider cache
            String combinerModelName = (String) ois.readObject(); // Read the combiner model name
            Map<Integer, Long> combinerCache = (Map<Integer, Long>) ois.readObject(); // Read the combiner cache

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return skeleton;
    }




}
