package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author hashjenny
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    // dir
    public static final File COMMIT = join(GITLET_DIR, "_Commit");
    // file
    public static final File HEAD = join(GITLET_DIR, "_Head");
    public static final File CURRENT = join(GITLET_DIR, "_Current");
    // dir
    public static final File BRANCH = join(GITLET_DIR, "_Branch");
    // dir
    public static final File STAGING_AREA = join(GITLET_DIR, "_StagingArea");
    // dir
    public static final File ADDITION = join(STAGING_AREA, "_Addition");
    // file
    public static final File REMOVAL = join(STAGING_AREA, "_Removal");

    public static Commit head;
    public static String currentBranchName;
    public static Commit currentBranch;
    public static HashMap<String, ArrayList<Commit>> branches = new HashMap<>();
    // filename -> blob
    public static HashMap<String, Blob> addition = new HashMap<>();
    public static HashSet<String> removal = new HashSet<>();

    public static void setupGitlet() throws IOException {
        if (GITLET_DIR.exists()) {
            throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        COMMIT.mkdir();
        BRANCH.mkdir();
        HEAD.createNewFile();
        CURRENT.createNewFile();

        STAGING_AREA.mkdir();
        ADDITION.mkdir();
        REMOVAL.createNewFile();
    }

    public static void loadGitlet() {
        if (!GITLET_DIR.exists()) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        head = getCommit(Utils.readContentsAsString(HEAD));
        currentBranchName = Utils.readContentsAsString(CURRENT);
        currentBranch = getLastCommit(currentBranchName);
        addition = putAllBlobs(ADDITION);
        removal = FileUtils.readItemsFormFile(REMOVAL);

        // load all branches as HashMap (branchName -> branch(commit list))
        getAllBranches();
    }

    public static void storeGitlet() {
        Utils.writeContents(HEAD, head.id);
        Utils.writeContents(CURRENT, currentBranchName);
        FileUtils.deleteAll(ADDITION);
        FileUtils.writeAllObjects(ADDITION, addition);
        FileUtils.writeItemsToFile(REMOVAL, removal);
    }

    public static void init() {
        var initCommit = new Commit("initial commit");
        head = initCommit;
        currentBranch = initCommit;

        Utils.writeObject(Utils.join(COMMIT, initCommit.id), initCommit);
        Utils.writeContents(Utils.join(BRANCH, "master"), initCommit.id);
        Utils.writeContents(HEAD, head.id);
        Utils.writeContents(CURRENT, "master");
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

        head = commit;
        currentBranch = commit;
        Utils.writeContents(Utils.join(BRANCH, currentBranchName), currentBranch.id);

        FileUtils.copyAll(ADDITION, GITLET_DIR);
        Utils.writeObject(Utils.join(COMMIT, commit.id), commit);

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
            if (file.exists()) {
                Utils.restrictedDelete(file);
                removal.add(filename);
            }
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

    public static void status() {
        var currentBranchName = Utils.readContentsAsString(CURRENT);
        Utils.message("=== Branches ===");
        for (var branchName : branches.keySet()) {
            if (branchName.equals(currentBranchName)) {
                Utils.message("*%s", branchName);
            } else {
                Utils.message("%s", branchName);
            }
        }

        Utils.message("=== Staged Files ===");
        for (var filename : addition.keySet()) {
            Utils.message("%s", filename);
        }

        Utils.message("=== Removed Files ===");
        for (var filename : removal) {
            Utils.message("%s", filename);
        }

        var workspace = getWorkspaceFiles();
        var filesMap = getFilesMap(head);

        Utils.message("=== Modification Not Staged For Commit ===");
        // Tracked in the current commit, changed in the working directory,
        // but not staged;
        for (var entry : filesMap.entrySet()) {
            var trackedFilename = entry.getKey();
            var trackedFileId = entry.getValue();
            if (!workspace.get(trackedFilename).equals(trackedFileId)
                    && !addition.containsKey(trackedFilename)) {
                Utils.message("%s (modified)", trackedFilename);
            }
        }
        // Staged for addition, but with different contents than in the working directory;
        // Staged for addition, but deleted in the working directory;
        for (var entry : addition.entrySet()) {
            var filename = entry.getKey();
            var id = entry.getValue().id;

            var workspaceFileId = workspace.get(filename);
            if (workspaceFileId == null) {
                Utils.message("%s (deleted)", filename);
            } else if (!workspaceFileId.equals(id)) {
                Utils.message("%s (modified)", filename);
            }
        }

        // Not staged for removal, but tracked in the current commit
        // and deleted from the working directory.
        for (var trackedFile : filesMap.keySet()) {
            if (!removal.contains(trackedFile) && !workspace.containsKey(trackedFile)) {
                Utils.message("%s (deleted)", trackedFile);
            }
        }

        // present in the working directory but neither staged for addition nor tracked.
        // This includes files that have been staged for removal
        Utils.message("=== Untracked Files ===");
        var untrackedFiles = getUntrackedFiles(workspace, filesMap);
        for (var filename: untrackedFiles) {
            Utils.message("%s", filename);
        }
    }

    public static void checkout(String... args) throws IOException {
        var len = args.length;
        if (len == 1) {
            // java gitlet.Main checkout [branch name]
            var branchName = args[0];
            var branchFile = Utils.join(BRANCH, branchName);
            if (!branchFile.exists()) {
                Utils.message("No such branch exists.");
                System.exit(0);
            }

            if (branchName.equals(currentBranchName)) {
                Utils.message("No need to checkout the current branch.");
                System.exit(0);
            }

            var currentBranchFiles = getFilesMap(head);
            var workspaceFiles = getWorkspaceFiles();
            for (var filename : workspaceFiles.keySet()) {
                if (!currentBranchFiles.containsKey(filename)) {
                    Utils.message("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }

            var id = readContentsAsString(branchFile);
            var commit = getCommit(id);
            var checkoutBranchFiles = getFilesMap(commit);
            FileUtils.deleteAll(CWD);
            for (var entry : checkoutBranchFiles.entrySet()) {
                var filename = entry.getKey();
                var content = getBlob(entry.getValue()).content;
                var file = Utils.join(CWD, filename);
                Utils.writeContents(file, content);
            }
            currentBranch = commit;
            head = commit;

        } else if (len == 2) {
            // java gitlet.Main checkout -- [file name]
            if (!args[0].equals("--")) {
                Utils.message("Incorrect operands.");
                System.exit(0);
            }
            var filename = args[1];
            if (!head.files.containsKey(filename)) {
                throw Utils.error("File does not exist in that commit.");
            }
            var blobId = head.files.get(filename);
            var blob = getBlob(blobId);
            Utils.writeContents(Utils.join(CWD, blob.filename), blob.content);
        } else {
            // java gitlet.Main checkout [commit id] -- [file name]
            if (!args[1].equals("--")) {
                Utils.message("Incorrect operands.");
                System.exit(0);
            }

            var id = args[0];
            var filename = args[2];
            var commitFile = getFileFromShortenName(id, COMMIT);
            var commit = readObject(commitFile, Commit.class);
            if (!commit.files.containsKey(filename)) {
                throw Utils.error("File does not exist in that commit.");
            }
            var blobId = head.files.get(filename);
            var blob = getBlob(blobId);
            var file = Utils.join(CWD, blob.filename);
            Utils.writeContents(file, blob.content);
        }

    }

    private static File getFileFromShortenName(String name, File folder) {
        File targetFile = null;
        var files = Utils.plainFilenamesIn(folder);
        if (files != null) {
            for (var file: files) {
                if (file.startsWith(name)) {
                    targetFile = Utils.join(folder, file);
                }
            }
        }
        if (targetFile == null) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
        }
        return targetFile;
    }



    public static void branch(String branchName) {
        var branchFiles = Utils.plainFilenamesIn(BRANCH);
        if (branchFiles != null && branchFiles.contains(branchName)) {
            throw Utils.error("A branch with that name already exists.");
        }
        var file = Utils.join(BRANCH, branchName);
        Utils.writeContents(file, head.id);
    }

    public static void rmBranch(String branchName) {
        var branchFile = Utils.join(BRANCH, branchName);
        if (!branchFile.exists()) {
            throw Utils.error("A branch with that name does not exist.");
        }

        if (branchName.equals(currentBranchName)) {
            throw Utils.error("Cannot remove the current branch.");
        }
        branchFile.delete();
    }

    public static void reset(String commitId) {
        var commitFile = getFileFromShortenName(commitId, COMMIT);
        var commit = readObject(commitFile, Commit.class);
        var filesMap = getFilesMap(commit);

    }

    private static void printCommit(Commit commit) {
        Utils.message("===");
        Utils.message("commit %s", commit.id);
        Utils.message("Date: %s", commit.timestamp);
        Utils.message(commit.message);
        if (!commit.mergedParentId.isEmpty()) {
            Utils.message("Merged %s into %s.",
                    getBranchName(commit.id),
                    getBranchName(commit.mergedParentId));
        }
    }

    private static Blob getLastBlob(String filename) {
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

    public static String calcBlobId(String filename, String content) {
        return Utils.sha1(filename, content);
    }

    public static Commit getCommit(String id) {
        if (id.isEmpty()) {
            return null;
        }
        return Utils.readObject(Utils.join(COMMIT, id), Commit.class);
    }

    public static Commit getLastCommit(String branchName) {
        if (branchName.isEmpty()) {
            return null;
        }
        var branchFile = Utils.join(BRANCH, branchName);
        var id = Utils.readContentsAsString(branchFile);
        return getCommit(id);
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

    public static void switchBranch(String branchName) {
        Utils.writeContents(CURRENT, branchName);
    }

    public static String getBranchName(String commitId) {
        for (var entry : branches.entrySet()) {
            var branchName = entry.getKey();
            var branch = entry.getValue();
            for (var commit : branch) {
                if (commit.id.equals(commitId)) {
                    return branchName;
                }
            }
        }
        return null;
    }

    public static ArrayList<Commit> getBranch(Commit commit) {
        var currentId = commit.id;
        var list = new ArrayList<Commit>();
        while (!currentId.isEmpty()) {
            var current = getCommit(currentId);
            if (current != null) {
                list.add(current);
                currentId = current.parentId;
            }
        }
        return list;
    }

    public static void getAllBranches() {
        var files = Utils.plainFilenamesIn(BRANCH);
        if (files != null) {
            for (var file : files) {
                var id = Utils.readContentsAsString(Utils.join(BRANCH, file));
                var commit = getCommit(id);
                if (commit != null) {
                    var branch = getBranch(commit);
                    branches.put(file, branch);
                }
            }
        }
    }

    public static HashMap<String, String> getFilesMap(Commit commit) {
        var map = new HashMap<String, String>();
        var current = commit;
        while (!current.parentId.isEmpty()) {
            for (var entry : current.files.entrySet()) {
                if (!map.containsKey(entry.getKey()) /* && entry.getValue() != null */) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            current = getCommit(current.parentId);
        }
        return map;
    }

    public static HashMap<String, String> getWorkspaceFiles() {
        var workspaceFiles = new HashMap<String, String>();
        var files = Utils.plainFilenamesIn(CWD);
        if (files != null) {
            for (var file : files) {
                var path = Utils.join(CWD, file);
                if (path.isFile()) {
                    var content = Utils.readContentsAsString(path);
                    workspaceFiles.put(file, calcBlobId(file, content));
                }
            }
        }
        return workspaceFiles;
    }

    public static HashSet<String> getUntrackedFiles(HashMap<String, String> workspaceFiles, HashMap<String, String> currentFilesMap) {
        var untrackedFiles = new HashSet<String>();
        for (var filename : workspaceFiles.keySet()) {
            if (!currentFilesMap.containsKey(filename) && !addition.containsKey(filename)) {
                untrackedFiles.add(filename);
            }
        }
        return untrackedFiles;
    }




}
