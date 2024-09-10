package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    // file
    public static final File MODIFICATION = join(GITLET_DIR, "_Modification");
    // file
    public static final File UNTRACKED = join(GITLET_DIR, "_Untracked");

    public static Commit head;
    public static HashMap<String, ArrayList<Commit>> branches = new HashMap<>();
    public static ArrayList<Commit> currentBranch = new ArrayList<>();
    // filename -> blob
    public static HashMap<String, Blob> addition = new HashMap<>();
    public static HashSet<String> removal = new HashSet<>();
    // TODO: git status
    public static HashMap<ModificationInformation, String> modificationMap = new HashMap<>();
    public static HashMap<String, String> untrackedMap = new HashMap<>();

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
        head = getCommit(Utils.readContentsAsString(HEAD));
        addition = putAllBlobs(ADDITION);
        removal = FileUtils.readItemsFormFile(REMOVAL);

    }

    public static void storeGitlet() {
        Utils.writeContents(HEAD, head.id);
        FileUtils.deleteAll(ADDITION);
        FileUtils.writeAllObjects(ADDITION, addition);
        FileUtils.writeItemsToFile(REMOVAL, removal);
    }

    public static void init() {
        var initCommit = new Commit("initial commit");
//        var branch = new ArrayList<Commit>();
//        branch.add(initCommit);
//        branches.put("master", branch);
//        head = new Head(initCommit.id, "master");
        head = initCommit;
        Utils.writeObject(join(BRANCH, "master"), new Branch("master", initCommit.id));
        Utils.writeObject(Utils.join(COMMIT, initCommit.id), initCommit);
        Utils.writeContents(HEAD, head.id);
    }

    public static void add(String filename) throws IOException {
        if (!Utils.join(CWD, filename).exists()) {
            throw Utils.error("File does not exist.");
        }

        var blob = new Blob(filename);
        removal.remove(blob.filename);

        var lastBlob = getLastBlob(blob.filename);
        if (lastBlob != null) {
            if (lastBlob.content.equals(blob.content)) {
                addition.remove(blob.filename);
                FileUtils.delete(ADDITION, blob.id);
            }
        }

        addition.put(blob.filename, blob);
    }

    public static void commit(String msg) throws IOException {
        if (addition.isEmpty() && removal.isEmpty()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }

        var commit = new Commit(msg, head.id);
        for (var entry : addition.entrySet()) {
            var blob = entry.getValue();
            commit.addFile(blob.filename, blob.id);
        }
        for (var removedFile : removal) {
            commit.addFile(removedFile, null);
        }

        FileUtils.copyAll(ADDITION, GITLET_DIR);
        Utils.writeObject(Utils.join(COMMIT, commit.id), commit);
        head = commit;
        addition.clear();
        removal.clear();
    }

    public static void rm(String filename) {
        if (!addition.containsKey(filename) && getLastBlob(filename) == null) {
            throw Utils.error("No reason to remove the file.");
        }
        addition.remove(filename);
        if (getLastBlob(filename) != null) {
            var file = Utils.join(CWD, filename);
//            if (file.exists()) {
//                file.delete();
//                removal.add(filename);
//            }
            Utils.restrictedDelete(file);
            removal.add(filename);
        }
    }

    public static void log() {
        var commit = head;
        while (commit != null) {
            printCommit(commit);
            commit = getCommit(commit.parentId);
        }
    }

    public static void globalLog() {
        var files = Utils.plainFilenamesIn(COMMIT);
        if (files != null) {
            for (var file : files) {
                var path = Utils.join(COMMIT, file);
                var commit = Utils.readObject(path, Commit.class);
                printCommit(commit);
            }
        }
    }

    private static void printCommit(Commit commit) {
        Utils.message("===");
        Utils.message("commit %s", commit.id);
        Utils.message("Date: %s", commit.timestamp);
        Utils.message(commit.message);
        if (!commit.mergedParentId.isEmpty()) {
            // FIXME: merge branch name but not commit id
            Utils.message("Merged %s into %s.", commit.mergedParentId, commit.mergedParentId);
        }
    }

    public static void find(String commitMessage) {
        var files = Utils.plainFilenamesIn(COMMIT);
        var notMatchFlag = true;
        if (files != null) {
            for (var file : files) {
                var path = Utils.join(COMMIT, file);
                var commit = Utils.readObject(path, Commit.class);
                if (commit.message.equals(commitMessage)) {
                    Utils.message(commit.id);
                    notMatchFlag = false;
                }
            }
        }
        if (notMatchFlag) {
            throw Utils.error("Found no commit with that message.");
        }
    }

    private static Blob getLastBlob(String filename) {
//        var branchName = head.branchName;
//        var id = branches.get(branchName).currentCommitID;
        String blobId = "";
        var commit = head;
        while (commit != null) {
            if (commit.files.get(filename) != null) {
                blobId = commit.files.get(filename);
                break;
            } else {
                commit = getCommit(commit.parentId);
            }
        }

        if (blobId == null || blobId.isEmpty()) {
            return null;
        }
        return getBlob(blobId);
    }

    public static Blob getBlob(String id) {
        return Utils.readObject(Utils.join(GITLET_DIR, id), Blob.class);
    }

    public static Commit getCommit(String id) {
        if (id.isEmpty()) {
            return null;
        }
        return Utils.readObject(Utils.join(COMMIT, id), Commit.class);
    }

    public static HashMap<String, Blob> putAllBlobs(File folder) {
        var target = new HashMap<String, Blob>();
        var files = Utils.plainFilenamesIn(folder);
        if (files != null) {
            for (var f : files) {
                var blob = Utils.readObject(Utils.join(folder, f), Blob.class);
                target.put(blob.filename, blob);
            }
        }
        return target;
    }


}
