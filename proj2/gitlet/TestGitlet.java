package gitlet;

import org.junit.*;

import java.io.File;
import java.io.IOException;

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

    @Test
    public void test33() {
        setupGitlet();
        init();
        Utils.writeContents(Utils.join(CWD, "f.txt"), "f");
        Utils.writeContents(Utils.join(CWD, "g.txt"), "g");

        loadGitlet();
        add("f.txt");
        storeGitlet();

        loadGitlet();
        add("g.txt");
        storeGitlet();

        loadGitlet();
        commit("Two files");
        storeGitlet();

        loadGitlet();
        branch("other");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "h.txt"), "h");
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
        status();
        storeGitlet();

        loadGitlet();
        checkout("other");
        storeGitlet();

        loadGitlet();
        rm("f.txt");
        storeGitlet();

        Utils.writeContents(Utils.join(CWD, "k.txt"), "k");
        loadGitlet();
        add("k.txt");
        storeGitlet();

        loadGitlet();
        commit("Add k.txt and remove f.txt");
        storeGitlet();

        loadGitlet();
        checkout("master");
        storeGitlet();

        loadGitlet();
        merge("other");
        storeGitlet();

        loadGitlet();
        log();
        storeGitlet();

    }

}
