package gitlet;

import org.junit.Test;

import static gitlet.FileUtils.*;
import static gitlet.Repository.*;


public class TestCommon {
    @Test
    public void commonTest() {

        Blob blob = null;
        var list = Utils.plainFilenamesIn(ADDITION);
        if (list != null) {
            var file = list.get(0);
            var path = Utils.join(ADDITION, file);
            blob = Utils.readObject(path, Blob.class);
        }

    }


    @Test
    public void writeContentTest() {
        var file = Utils.join(CWD, "writefile");
        Utils.writeContents(file, "write");
    }
}
