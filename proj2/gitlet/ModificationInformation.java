package gitlet;

import java.io.Serializable;

public class ModificationInformation implements Serializable {
    public String filename;
    public Modification modification;

    public ModificationInformation(String filename, Modification modification) {
        this.filename = filename;
        this.modification = modification;
    }

    public enum Modification implements Serializable {
        DELETED, MODIFIED
    }
}
