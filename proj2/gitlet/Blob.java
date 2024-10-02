package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable, Dumpable {
    private final String id;
    private final String filename;
    private final String content;

    public Blob(String filename) {
        this.filename = filename;
        this.content = Utils.readContentsAsString(new File(filename));
        this.id = Repository.calcBlobId(filename, content);
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
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
