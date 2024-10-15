package gitlet;

import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class TestGitlet {

    @Before
    public void setup() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[]{
            "powershell.exe",
            "-Command",
            "Remove-Item -Path .gitlet -Recurse -Force"});
        process.waitFor();

        new File("f.txt").delete();
        new File("g.txt").delete();
        new File("h.txt").delete();
        new File("k.txt").delete();
        new File("m.txt").delete();
    }


    @Test
    public void initTest() {
        setupGitlet();
        init();
        var branch = Utils.readContentsAsString(join(BRANCH, "master"));
        System.out.println("branch---------------");
        System.out.println(branch);

        var id = Utils.readContentsAsString(HEAD);
        var commit = getCommit(id);
        System.out.println("commit---------------");
        System.out.println(commit.getId());
        System.out.println(commit.getParentId());
        storeGitlet();
    }

    public void commonAction1() {

        Utils.writeContents(Utils.join(CWD, "f.txt"), "wug");
        Utils.writeContents(Utils.join(CWD, "g.txt"), "nonwug");

        loadGitlet();
        add("f.txt");
        storeGitlet();

        loadGitlet();
        add("g.txt");
        storeGitlet();

        loadGitlet();
        commit("Two files");
        storeGitlet();
    }

    public void commonAction2() {
        loadGitlet();
        branch("other");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "h.txt"), "wug2");
        loadGitlet();
        add("h.txt");
        storeGitlet();

        loadGitlet();
        rm("g.txt");
        storeGitlet();

        loadGitlet();
        commit("Add h.txt and remove g.txt");
        storeGitlet();

        loadGitlet();
        checkout("other");
        storeGitlet();

        loadGitlet();
        rm("f.txt");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "k.txt"), "wug3");
        loadGitlet();
        add("k.txt");
        storeGitlet();

        loadGitlet();
        commit("Add k.txt and remove f.txt");
        storeGitlet();
    }

    public String getDiffFile(File[] f1, File[] f2) {
        var set1 = new HashSet<File>(Arrays.asList(f1));

        var set2 = new HashSet<File>(Arrays.asList(f2));

        set2.removeAll(set1);
        var item  =  set2.iterator().next();
        return item.getName();
    }

    @Test
    public void test36a() {

        setupGitlet();
        init();

        loadGitlet();
        branch("B1");
        storeGitlet();

        loadGitlet();
        branch("B2");
        storeGitlet();

        loadGitlet();
        checkout("B1");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "h.txt"), "h");
        loadGitlet();
        add("h.txt");
        storeGitlet();

        loadGitlet();
        commit("Add h.txt");
        storeGitlet();

        loadGitlet();
        checkout("B2");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "f.txt"), "f");
        loadGitlet();
        add("f.txt");
        storeGitlet();

        loadGitlet();
        commit("Add f.txt");
        storeGitlet();

        loadGitlet();
        branch("C1");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "g.txt"), "g");
        loadGitlet();
        add("g.txt");
        storeGitlet();

        loadGitlet();
        rm("f.txt");
        storeGitlet();

        loadGitlet();
        commit("g.txt added, f.txt removed");
        storeGitlet();

        loadGitlet();
        checkout("B1");
        storeGitlet();

        loadGitlet();
        merge("C1");
        storeGitlet();

        loadGitlet();
        merge("B2");
        storeGitlet();

        loadGitlet();
        log();
        storeGitlet();

    }

    @Test
    public void test33() {
        setupGitlet();
        init();

        commonAction1();

        commonAction2();

        loadGitlet();
        checkout("master");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "k.txt"), "wug");

        loadGitlet();
        merge("other");
        storeGitlet();

        loadGitlet();
        log();
        storeGitlet();

    }

    // reset
    @Test
    public void test37() {
        setupGitlet();
        init();

        var commitFolder = new File("E:\\0.CSLectures\\cs61b\\cs61b-sp21\\proj2\\.gitlet\\_Commit");
        var initFiles = commitFolder.listFiles();

        commonAction1();

        var commonAction1Files = commitFolder.listFiles();
        var filename = getDiffFile(initFiles, commonAction1Files);

        commonAction2();

        loadGitlet();
        checkout("master");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "m.txt"), "wug");
        loadGitlet();
        add("m.txt");
        storeGitlet();

        loadGitlet();
        reset(filename);
        storeGitlet();

        loadGitlet();
        checkout("other");
        storeGitlet();
    }

    @Test
    public void remoteTest() {
        setupGitlet();
        init();

        loadGitlet();
        addRemote("test", ".remote/test/.gitlet");
        storeGitlet();

        loadGitlet();
        addRemote("test2", ".remote/test/.gitlet");
        storeGitlet();

        loadGitlet();
        rmRemote("test");
        storeGitlet();
    }
}
