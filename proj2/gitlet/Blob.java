package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    public final String id;
    public final String filename;
    public final String content;

    public Blob(String filename) {
        this.filename = filename;
        this.content = Utils.readContentsAsString(new File(filename));
        this.id = Repository.calcBlobId(filename, content);
    }
}
