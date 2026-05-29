import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Updater {
    public static void main(String[] args) throws IOException, InterruptedException {
        File sourceFile = new File(args[0]);
        File existingFile = new File(args[1]);
        File targetFolder = existingFile.getParentFile();
        File targetFile = new File(targetFolder + "/" + sourceFile.getName());

        if (!sourceFile.exists() || !targetFolder.isDirectory() || !existingFile.exists() || targetFile.exists()) {
            System.exit(1);
        }

        try {
            try {
                Files.copy(sourceFile.toPath(), targetFile.toPath());
            } finally {
                if (Arrays.equals(readFile(sourceFile), readFile(targetFile))) {
                    for (int i = 0; i < 10; i++) {
                        if (!existingFile.exists()) break;
                        Thread.sleep(2000);
                        try {
                            existingFile.delete();
                        } catch (Exception e) {
                            // Ignore
                        }
                    }
                }
            }
        } finally {
            if (existingFile.exists()) targetFile.delete();
        }
    }

    public static byte[] readFile(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fileInputStream.readAllBytes();
        }
    }
}