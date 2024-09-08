package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author hashjenny
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    // dir
    public static final File COMMIT = join(GITLET_DIR, "_Commit");
    // file
    public static final File HEAD = join(GITLET_DIR, "_Head");
    // dir
    public static final File BRANCH = join(GITLET_DIR, "_Branch");
    // dir
    public static final File STAGING_AREA = join(GITLET_DIR, "_StagingArea");

    public static Head head;
    public static HashMap<String,Branch> branches = new HashMap<>();
    public static StagingArea stagingArea;

    public static void setupGitletFolder() throws IOException {
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        COMMIT.mkdir();
        BRANCH.mkdir();
        HEAD.createNewFile();
        STAGING_AREA.createNewFile();
    }

    public static void loadGitletFolder(){
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        head = Utils.readObject(HEAD, Head.class);

        var branchFiles = Utils.plainFilenamesIn(BRANCH);
        if (branchFiles != null) {
            for (var f : branchFiles) {
                var branch = Utils.readObject(new File(f), Branch.class);
                branches.put(branch.branchName, branch);
            }
        }

        stagingArea = Utils.readObject(STAGING_AREA, StagingArea.class);
    }

    public static void init() {
        var initCommit = new Commit("initial commit");
        var branch = new Branch("master", initCommit.id);
        var head = new Head(initCommit.id, branch.branchName);

        Utils.writeObject(Utils.join(BRANCH, branch.branchName), branch);
        Utils.writeObject(Utils.join(COMMIT, initCommit.id), initCommit);
        Utils.writeObject(HEAD, head);
    }

    public static void add(String filename) {
        if (!Utils.join(CWD, filename).exists()) {
            throw new GitletException("File does not exist.");
        }

        var blob = new Blob(filename);
        stagingArea.removedFiles.remove(blob.filename);
        if (getLastCommitFile(blob.filename).content.equals(blob.content)) {
            stagingArea.stagedFiles.remove(blob.filename);
        }
        stagingArea.stagedFiles.put(blob.filename, blob.id);
    }

    private static Blob getLastCommitFile(String filename) {
        var branchName = head.branchName;
        var id = branches.get(branchName).currentCommitID;
        String blobId = "";

        var commit = getCommit(id);
        while (!commit.getParentId().isEmpty()) {
            if (commit.commitFiles.get(filename) != null) {
                blobId = commit.commitFiles.get(filename);
            } else {
                commit = getCommit(commit.getParentId());
            }
        }

        if (blobId.isEmpty()) {
            return null;
        }
        return getBlob(blobId);
    }

    private static Blob getBlob(String id) {
        return Utils.readObject(Utils.join(GITLET_DIR, id), Blob.class);
    }

    private static Commit getCommit(String id) {
        return Utils.readObject(Utils.join(COMMIT, id), Commit.class);
    }


}
