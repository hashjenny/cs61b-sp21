package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class StagingArea implements Serializable {
    public HashMap<String, String> stagedFiles;
    public HashMap<String, String> removedFiles;
    public HashMap<ModificationInformation, String> modificationFiles;
    public HashMap<String, String> untrackedFiles;

    public StagingArea() {
        stagedFiles = new HashMap<>();
        removedFiles = new HashMap<>();
        modificationFiles = new HashMap<>();
        untrackedFiles = new HashMap<>();
    }

}
