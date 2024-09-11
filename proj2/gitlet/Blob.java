package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable, Dumpable {
    public final String id;
    public final String filename;
    public final String content;

    public Blob(String filename) {
        this.filename = filename;
        this.content = Utils.readContentsAsString(new File(filename));
        this.id = Repository.calcBlobId(filename, content);
    }

    @Override
    public void dump() {
        Utils.message("------------------");
        Utils.message("Blob id: %s", id);
        Utils.message("Blob filename: %s", filename);
        Utils.message("Blob content: %s", content);
        Utils.message("------------------");
    }
}
