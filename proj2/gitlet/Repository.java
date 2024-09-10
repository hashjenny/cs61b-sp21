package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    // dir
    public static final File ADDITION = join(STAGING_AREA, "_Addition");
    // file
    public static final File REMOVAL = join(STAGING_AREA, "_Removal");
//    public static final File MODIFICATION = join(GITLET_DIR, "_Modification");
//    public static final File UNTRACKED = join(GITLET_DIR, "_Untracked");


    public static Commit head;
    public static HashMap<String, ArrayList<Commit>> branches = new HashMap<>();
    public static ArrayList<Commit> currentBranch = new ArrayList<>();
    public static StagingArea stagingArea = new StagingArea();
    // TODO: git status
    public static HashMap<ModificationInformation, String> modificationFiles = new HashMap<>();
    public static HashMap<String, String> untrackedFiles = new HashMap<>();

    public static void setupGitlet() throws IOException {
        if (GITLET_DIR.exists()) {
            throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        COMMIT.mkdir();
        BRANCH.mkdir();
        HEAD.createNewFile();

        STAGING_AREA.mkdir();
        ADDITION.mkdir();
        REMOVAL.createNewFile();
    }

    public static void loadGitlet(){
        if (!GITLET_DIR.exists()) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
//        head = Utils.readObject(HEAD, Head.class);

//        var branchFile = join(BRANCH, head.branchName);
//        currentBranch = Utils.

        head = getCommit(Utils.readContentsAsString(HEAD));
//        putAllBlobs(ADDITION, stagingArea.additionFiles);
//        stagingArea.removalFiles = Utils.readObject(REMOVAL, HashSet.class);
//
//        branches = Utils.readObject(BRANCH, HashMap.class);
    }

    public static void storeGitlet() {
        Utils.writeContents(HEAD, head.id);

//        REMOVAL.delete();
//        writeObject(REMOVAL, stagingArea.removal);
    }

    public static void init() {
        var initCommit = new Commit("initial commit");
        var branch = new ArrayList<Commit>();
        branch.add(initCommit);
        branches.put("master", branch);
//        head = new Head(initCommit.id, "master");
        head = initCommit;
        var branchFile = join(BRANCH, "master");
        Utils.writeObject(branchFile, new Branch("master", initCommit.id));
        Utils.writeObject(Utils.join(COMMIT, initCommit.id), initCommit);
//        Utils.writeObject(HEAD, head);
        Utils.writeContents(HEAD, head.id);
    }

    public static void add(String filename) throws IOException {
        if (!Utils.join(CWD, filename).exists()) {
            throw Utils.error("File does not exist.");
        }

        var blob = new Blob(filename);
        stagingArea.removal.remove(blob.filename);

        var lastBlob = getLastCommitFile(blob.filename);
        if (lastBlob != null) {
            if (lastBlob.content.equals(blob.content)) {
                stagingArea.addition.remove(blob.filename);
                FileUtils.delete(ADDITION, blob.id);
            }
        }

        stagingArea.addition.put(blob.filename, blob);
        var file = join(ADDITION, blob.id);
        writeObject(file, blob);
    }

    private static Blob getLastCommitFile(String filename) {
//        var branchName = head.branchName;
//        var id = branches.get(branchName).currentCommitID;

        String blobId = "";
        var commit = head;
        while (!commit.getParentId().isEmpty()) {
            if (commit.files.get(filename) != null) {
                blobId = commit.files.get(filename);
            } else {
                commit = getCommit(commit.getParentId());
            }
        }

        if (blobId == null || blobId.isEmpty()) {
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

    private static void putAllBlobs(File folder, HashMap<String, Blob> target) {
        var files = Utils.plainFilenamesIn(folder);
        if (files != null) {
            for (var f : files) {
                var blob = Utils.readObject(new File(f), Blob.class);
                target.put(blob.filename, blob);
            }
        }
    }


}
