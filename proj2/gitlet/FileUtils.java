package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static gitlet.Utils.join;

public class FileUtils {
    public static void delete(File folder, String filename) {
        var file = join(folder, filename);
        file.delete();
    }

    public static void deleteAll(File folder) {
        var files = folder.listFiles();
        if (files != null) {
            for (var file : files) {
                file.delete();
            }
        }
    }

    public static void copy(File sourceFolder, File targetFolder, String filename) throws IOException {
        var source = join(sourceFolder, filename);
        var target = join(targetFolder, filename);
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
