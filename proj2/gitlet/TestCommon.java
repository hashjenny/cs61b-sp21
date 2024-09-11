package gitlet;

import org.junit.Test;

import static gitlet.FileUtils.*;
import static gitlet.Repository.*;


public class TestCommon {
    @Test
    public void commonTest() {
//        var list = Utils.plainFilenamesIn(Repository.BRANCH);
//        if (list != null) {
//            for (var item : list) {
//                System.out.println(list);
//            }
//        }

//        var arr = readItemsFormFile(REMOVAL);

        Blob blob = null;
        var list = Utils.plainFilenamesIn(ADDITION);
        if (list != null) {
            var file = list.get(0);
            var path = Utils.join(ADDITION, file);
            blob = Utils.readObject(path, Blob.class);
        }

    }

    @Test
    public void removalTest() {
        var set = readItemsFormFile(REMOVAL);
        System.out.println(set);
        set.add("bb");
        writeItemsToFile(REMOVAL, set);
    }

    @Test
    public void removalTest2() {
        var set = readItemsFormFile(REMOVAL);
        System.out.println(set);
        set.clear();
        writeItemsToFile(REMOVAL, set);
    }

    @Test
    public void
}
