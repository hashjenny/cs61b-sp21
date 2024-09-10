package gitlet;

import java.util.HashMap;
import java.util.HashSet;

public class StagingArea {
    // blob.blobName -> blob
    public HashMap<String, Blob> addition;
    // blob.blobName
    public HashSet<String> removal;
//    public HashMap<ModificationInformation, String> modificationFiles;
//    public HashMap<String, String> untrackedFiles;

    public StagingArea() {
        addition = new HashMap<>();
        removal = new HashSet<>();
//        modificationFiles = new HashMap<>();
//        untrackedFiles = new HashMap<>();
    }

}
