package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Repository.*;
import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class FileUtils {
    public static void delete(File folder, String filename) {
        var file = join(folder, filename);
        file.delete();
    }

    public static void deleteAll(File folder) {
        var files = folder.listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    public static void copy(File sourceFolder, File targetFolder, String filename) {
        var source = join(sourceFolder, filename);
        var target = join(targetFolder, filename);
        try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyAll(File sourceFolder, File targetFolder) {
        var files = Utils.plainFilenamesIn(sourceFolder);
        if (files != null) {
            for (var file : files) {
                copy(sourceFolder, targetFolder, file);
            }
        }
    }

    public static HashSet<String> readItemsFormFile(File file) {
        HashSet<String> set = new HashSet<>();
        if (file.exists()) {
            var content = Utils.readContentsAsString(file);
            var arr = content.split("<<<");
            if (!arr[0].isEmpty()) {
                set = new HashSet<>(Arrays.asList(arr));
            }
        }
        return set;
    }

    public static void writeItemsToFile(File file, HashSet<String> set) {
        var arr = set.toArray(new String[0]);
        var content = String.join("<<<", arr);
        if (!content.isEmpty()) {
            Utils.writeContents(file, content);
        }
    }

    public static void writeAllObjects(File folder, HashMap<String, Blob> map) {
        for (var entry: map.entrySet()) {
            var blob = entry.getValue();
            Utils.writeObject(Utils.join(folder, blob.id), blob);
        }
    }

    public static void writeAllContentFiles(File folder, HashMap<String, String> filesMap) {
        for (var entry : filesMap.entrySet()) {
            var filename = entry.getKey();
            var content = getBlob(entry.getValue()).content;
            var file = Utils.join(folder, filename);
            Utils.writeContents(file, content);
        }
    }
}
