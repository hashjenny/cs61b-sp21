package gitlet;

import org.junit.*;

import java.io.IOException;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class TestGitlet {

    @Before
    public void setup() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(
                new String[]{"powershell.exe", "-Command", "Remove-Item -Path .gitlet -Recurse -Force"});
        process.waitFor();
    }


    @Test
    public void initTest() throws IOException {
        setupGitlet();
        init();
        var branch = Utils.readContentsAsString(join(BRANCH, "master"));
        System.out.println("branch---------------");
        System.out.println(branch);

        var id = Utils.readContentsAsString(HEAD);
        var commit = getCommit(id);
        System.out.println("commit---------------");
        System.out.println(commit.id);
        System.out.println(commit.parentId);
        storeGitlet();
    }

}
