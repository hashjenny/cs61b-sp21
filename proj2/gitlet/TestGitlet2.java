package gitlet;

import org.junit.Test;

import java.io.IOException;

import static gitlet.Repository.*;

public class TestGitlet2 {

    @Test
    public void loadGitletTest() {
        loadGitlet();
        var a = 1;
    }

    @Test
    public void addTest1() throws IOException {
        loadGitlet();
        add("temp/gitlet-design.md");
        storeGitlet();
    }

    @Test
    public void addTest2() throws IOException {
        loadGitlet();
        add("gitlet.md"); // error here
        storeGitlet();
    }

    @Test
    public void addTest3() throws IOException {
        loadGitlet();
        add("test1");
        storeGitlet();
    }

    @Test
    public void addTest4() throws IOException {
        loadGitlet();
        // edit test1
        add("test1");
        storeGitlet();
    }

    @Test
    public void commitTest() throws IOException {
        loadGitlet();
        commit("add gitlet.md and test1");
        storeGitlet();
    }

    @Test
    public void commitTest2() throws IOException {
        loadGitlet();
        commit("add gitlet.md and test1");
        storeGitlet();
    }

    @Test
    public void addAndRmTest() throws IOException {
        loadGitlet();
        add("test2");
        storeGitlet();
    }

    @Test
    public void addAndRmTest2() {
        loadGitlet();
        rm("test2");
        storeGitlet();
    }

    @Test
    public void rmTest2() {
        loadGitlet();
        rm("test1");
        storeGitlet();
    }

    @Test
    public void rmAndCommitTest() throws IOException {
        loadGitlet();
        commit("rm test1");
        storeGitlet();
    }

    @Test
    public void rmTestWithFailure() {
        loadGitlet();
        rm("rm test3");
        storeGitlet();
    }

    @Test
    public void logTest() {
        loadGitlet();
        log();
    }

    @Test
    public void globalLogTest() {
        loadGitlet();
        globalLog();
    }

    @Test
    public void findTest() {
        loadGitlet();
        find("rm test1");
        find("rm test11"); // error
    }
}
