package net.bewis09.bewisclient.process;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

public class Updater {
    public static void main(String[] args) {
        File sourceFile = new File(args[0]);
        File targetFolder = new File(args[1]);
        File existingFile = new File(args[2]);
        File targetFile = new File(targetFolder + "/" + sourceFile.getName());

        if (!sourceFile.exists() || !targetFolder.isDirectory() || !existingFile.exists() || targetFile.exists()) {
            System.exit(1);
        }

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath());
        } catch (Exception ignored) {

        } finally {
            try {
                try (FileInputStream file = new FileInputStream(sourceFile)) {
                    try (FileInputStream file2 = new FileInputStream(targetFile)) {
                        if (file.read() != file2.read()) {
                            System.exit(targetFile.delete() ? 1 : 2);
                        } else {
                            if (!existingFile.delete() && existingFile.exists()) {
                                System.exit(targetFile.delete() ? 1 : 2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }
}
