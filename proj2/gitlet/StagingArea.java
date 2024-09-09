package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class StagingArea implements Serializable {
    // blob.blobName -> blob.id
    public HashMap<String, String> stagedFiles;
    // blob.blobName -> blob.id
    public HashMap<String, String> removedFiles;
//    public HashMap<ModificationInformation, String> modificationFiles;
//    public HashMap<String, String> untrackedFiles;

    public StagingArea() {
        stagedFiles = new HashMap<>();
        removedFiles = new HashMap<>();
//        modificationFiles = new HashMap<>();
//        untrackedFiles = new HashMap<>();
    }

}
