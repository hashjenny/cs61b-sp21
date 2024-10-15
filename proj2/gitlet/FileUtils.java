package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.join;

public class FileUtils {

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

    public static void copyAll(File source, File target, Collection<String> files) {
        for (var file : files) {
            if (file != null) {
                FileUtils.copy(source, target, file);
            }
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
        // if set.size() == 0, then arr is `String[0] {  }`, content is `""`
        var arr = set.toArray(new String[0]);
        var content = String.join("<<<", arr);
        Utils.writeContents(file, content);
    }

    public static void writeAllBlobs(File folder, TreeMap<String, Blob> map) {
        for (var entry: map.entrySet()) {
            var blob = entry.getValue();
            Utils.writeObject(Utils.join(folder, blob.getId()), blob);
        }
    }

    /*
    public static void writeAllRemotes(File folder, TreeMap<String, Remote> map) {
        for (var entry: map.entrySet()) {
            var remote = entry.getValue();
            Utils.writeObject(Utils.join(folder, remote.getName()), remote);
        }
    }
     */

    public static void writeAllContentFiles(File folder, TreeMap<String, String> filesMap) {
        for (var entry : filesMap.entrySet()) {
            var filename = entry.getKey();
            var blobId = entry.getValue();
            if (blobId == null) {
                continue;
            }
            var content = getBlob(blobId).getContent();
            var file = Utils.join(folder, filename);
            Utils.writeContents(file, content);
        }
    }
}
