package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author hashjenny
 */
public class Repository {
    /*
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
    public static final File ADDITION = join(GITLET_DIR, "_Addition");
    // file
    public static final File REMOVAL = join(GITLET_DIR, "_Removal");

    private static Commit head;
    private static String currentBranchName;
    private static Commit currentBranch;
    private static TreeMap<String, ArrayList<Commit>> branches = new TreeMap<>();
    // filename -> blob
    private static TreeMap<String, Blob> addition = new TreeMap<>();
    private static HashSet<String> removal = new HashSet<>();

    public static void setupGitlet() {
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMIT.mkdir();
        BRANCH.mkdir();
//        HEAD.createNewFile();
//        CURRENT.createNewFile();

        ADDITION.mkdir();
//        REMOVAL.createNewFile();
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

        // load all branches as TreeMap (branchName -> branch(commit list))
        getAllBranches();
    }

    public static void storeGitlet() {
        Utils.writeContents(HEAD, head.getId());
        Utils.writeContents(CURRENT, currentBranchName);
        FileUtils.deleteAll(ADDITION);
        FileUtils.writeAllObjects(ADDITION, addition);
        FileUtils.writeItemsToFile(REMOVAL, removal);
    }

    public static void init() {
        var initCommit = new Commit("initial commit");
        head = initCommit;
        currentBranch = initCommit;
        currentBranchName = "master";

        Utils.writeObject(Utils.join(COMMIT, initCommit.getId()), initCommit);
        Utils.writeContents(Utils.join(BRANCH, "master"), initCommit.getId());
        Utils.writeContents(HEAD, head.getId());
        Utils.writeContents(CURRENT, "master");
    }

    public static void add(String filename) {
        if (!Utils.join(CWD, filename).exists()) {
            Utils.message("File does not exist.");
            System.exit(0);
        }

        var blob = new Blob(filename);

        var lastBlob = getLastBlob(filename);
        if (lastBlob != null
                && lastBlob.getContent().equals(blob.getContent())) {
            addition.remove(filename);
        } else {
            addition.put(filename, blob);
        }
        removal.remove(filename);

    }

    public static void commit(String msg) {
        if (msg.isEmpty()) {
            Utils.message("Please enter a commit message.");
            System.exit(0);
        }
        if (addition.isEmpty() && removal.isEmpty()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }


        var commit = new Commit(msg, head.getId());
        for (var entry : addition.entrySet()) {
            var blob = entry.getValue();
            commit.addFile(blob.getFilename(), blob.getId());
        }
        for (var removedFile : removal) {
            commit.addFile(removedFile, null);
        }

        head = commit;
        currentBranch = commit;
        Utils.writeContents(Utils.join(BRANCH, currentBranchName), currentBranch.getId());

        FileUtils.copyAll(ADDITION, GITLET_DIR);
        Utils.writeObject(Utils.join(COMMIT, commit.getId()), commit);

        addition.clear();
        removal.clear();
    }

    public static void rm(String filename) {
        if (!addition.containsKey(filename) && getLastBlob(filename) == null) {
            Utils.message("No reason to remove the file.");
            System.exit(0);
        }
        addition.remove(filename);
        if (getLastBlob(filename) != null) {
            var file = Utils.join(CWD, filename);
            if (file.exists()) {
                Utils.restrictedDelete(file);
            }
            removal.add(filename);
        }
    }

    public static void log() {
        var commit = head;
        while (commit != null) {
            printCommit(commit);
            commit = getCommit(commit.getParentId());
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
                if (commit.getMessage().equals(commitMessage)) {
                    Utils.message(commit.getId());
                    notMatchFlag = false;
                }
            }
        }
        if (notMatchFlag) {
            Utils.message("Found no commit with that message.");
            System.exit(0);
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
        Utils.message("");

        Utils.message("=== Staged Files ===");
        for (var filename : addition.keySet()) {
            Utils.message("%s", filename);
        }
        Utils.message("");

        Utils.message("=== Removed Files ===");
        for (var filename : removal) {
            Utils.message("%s", filename);
        }
        Utils.message("");

        var workspace = getWorkspaceFiles();
        var filesMap = getFilesMap(head);

        Utils.message("=== Modifications Not Staged For Commit ===");
        // Tracked in the current commit, changed in the working directory,
        // but not staged;
        for (var entry : filesMap.entrySet()) {
            var trackedFilename = entry.getKey();
            var trackedFileId = entry.getValue();
            if (workspace.get(trackedFilename) != null
                    && !workspace.get(trackedFilename).equals(trackedFileId)
                    && !addition.containsKey(trackedFilename)) {
                Utils.message("%s (modified)", trackedFilename);
            }
        }
        // Staged for addition, but with different contents than in the working directory;
        // Staged for addition, but deleted in the working directory;
        for (var entry : addition.entrySet()) {
            var filename = entry.getKey();
            var id = entry.getValue().getId();

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
            if (!removal.contains(trackedFile)
                    && !workspace.containsKey(trackedFile)
                    && filesMap.get(trackedFile) != null) {
                Utils.message("%s (deleted)", trackedFile);
            }
        }
        Utils.message("");

        // present in the working directory but neither staged for addition nor tracked.
        // This includes files that have been staged for removal
        Utils.message("=== Untracked Files ===");
        var untrackedFiles = getUntrackedFiles(workspace, filesMap);
        for (var filename : untrackedFiles) {
            Utils.message("%s", filename);
        }
        Utils.message("");
    }

    public static void checkout(String... args) {
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
            checkUntrackedFile(workspaceFiles, currentBranchFiles);
//            for (var filename : workspaceFiles.keySet()) {
//                if (!currentBranchFiles.containsKey(filename)) {
//                    Utils.message("There is an untracked file in the way; delete it, or add and commit it first.");
//                    System.exit(0);
//                }
//            }

            var id = readContentsAsString(branchFile);
            var commit = getCommit(id);
            var checkoutBranchFiles = getFilesMap(commit);
            FileUtils.deleteAll(CWD);
            FileUtils.writeAllContentFiles(CWD, checkoutBranchFiles);
            currentBranch = commit;
            currentBranchName = branchName;
            head = commit;

        } else if (len == 2) {
            // java gitlet.Main checkout -- [file name]
            if (!args[0].equals("--")) {
                Utils.message("Incorrect operands.");
                System.exit(0);
            }
            var filename = args[1];
            if (!head.getFiles().containsKey(filename)) {
                Utils.message("File does not exist in that commit.");
                System.exit(0);
            }
            var blobId = head.getFiles().get(filename);
            var blob = getBlob(blobId);
            Utils.writeContents(Utils.join(CWD, blob.getFilename()), blob.getContent());
        } else {
            // java gitlet.Main checkout [commit id] -- [file name]
            if (!args[1].equals("--")) {
                Utils.message("Incorrect operands.");
                System.exit(0);
            }

            var id = args[0];
            var filename = args[2];
            var commit = getCommitFromShortenName(id);
            if (!commit.getFiles().containsKey(filename)) {
                Utils.message("File does not exist in that commit.");
                System.exit(0);
            }
            var blobId = commit.getFiles().get(filename);
            var blob = getBlob(blobId);
            var file = Utils.join(CWD, blob.getFilename());
            Utils.writeContents(file, blob.getContent());
        }

    }

    public static void branch(String branchName) {
        var branchFiles = Utils.plainFilenamesIn(BRANCH);
        if (branchFiles != null && branchFiles.contains(branchName)) {
            Utils.message("A branch with that name already exists.");
            System.exit(0);
        }
        var file = Utils.join(BRANCH, branchName);
        Utils.writeContents(file, head.getId());
    }

    public static void rmBranch(String branchName) {
        var branchFile = Utils.join(BRANCH, branchName);
        if (!branchFile.exists()) {
            Utils.message("A branch with that name does not exist.");
            System.exit(0);
        }

        if (branchName.equals(currentBranchName)) {
            Utils.message("Cannot remove the current branch.");
            System.exit(0);
        }
        branchFile.delete();
    }

    public static void reset(String commitId) {
        var commit = getCommitFromShortenName(commitId);
        var currentBranchFiles = getFilesMap(head);
        checkUntrackedFile(getWorkspaceFiles(), currentBranchFiles);

        var commitFilesMap = getFilesMap(commit);
        FileUtils.deleteAll(CWD);
        FileUtils.writeAllContentFiles(CWD, commitFilesMap);

        addition.clear();
        removal.clear();
        head = commit;
        currentBranch = commit;
    }

    public static void merge(String givenBranchName) {
        var givenBranchFile = Utils.join(BRANCH, givenBranchName);
        if (!givenBranchFile.exists()) {
            Utils.message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (givenBranchName.equals(currentBranchName)) {
            Utils.message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (!addition.isEmpty() || !removal.isEmpty()) {
            Utils.message("You have uncommitted changes.");
            System.exit(0);
        }
        var workspaceFiles = getWorkspaceFiles();

    }

    // print utils
    private static void printCommit(Commit commit) {
        Utils.message("===");
        Utils.message("commit %s", commit.getId());
        Utils.message("Date: %s", commit.getTimestamp());
        Utils.message(commit.getMessage());
        if (!commit.getMergedParentId().isEmpty()) {
            Utils.message("Merged %s into %s.",
                    getBranchNameByCommitId(commit.getId()),
                    getBranchNameByCommitId(commit.getMergedParentId()));
        }
        Utils.message("");
    }

    // blob utils
    private static Blob getLastBlob(String filename) {
        String blobId = "";
        var commit = head;
        while (commit != null) {
            if (commit.getFiles().get(filename) != null) {
                blobId = commit.getFiles().get(filename);
                break;
            } else {
                commit = getCommit(commit.getParentId());
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

    public static TreeMap<String, Blob> putAllBlobs(File folder) {
        var target = new TreeMap<String, Blob>();
        var files = Utils.plainFilenamesIn(folder);
        if (files != null) {
            for (var f : files) {
                var blob = Utils.readObject(Utils.join(folder, f), Blob.class);
                target.put(blob.getFilename(), blob);
            }
        }
        return target;
    }

    // commit utils
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

    private static Commit getCommitFromShortenName(String name) {
        File targetFile = null;
        var files = Utils.plainFilenamesIn(COMMIT);
        if (files != null) {
            for (var file : files) {
                if (file.startsWith(name)) {
                    targetFile = Utils.join(COMMIT, file);
                }
            }
        }
        if (targetFile == null) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(targetFile, Commit.class);
    }

    // branch utils
    public static String getBranchNameByCommitId(String commitId) {
        for (var entry : branches.entrySet()) {
            var branchName = entry.getKey();
            var branch = entry.getValue();
            for (var commit : branch) {
                if (commit.getId().equals(commitId)) {
                    return branchName;
                }
            }
        }
        return null;
    }

    public static ArrayList<Commit> getBranch(Commit commit) {
        var currentId = commit.getId();
        var list = new ArrayList<Commit>();
        while (!currentId.isEmpty()) {
            var current = getCommit(currentId);
            if (current != null) {
                list.add(current);
                currentId = current.getParentId();
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

    // file utils
    // TreeMap file => filename, blobId
    public static TreeMap<String, String> getFilesMap(Commit commit) {
        var map = new TreeMap<String, String>();
        var current = commit;
        while (!current.getParentId().isEmpty()) {
            for (var entry : current.getFiles().entrySet()) {
                if (!map.containsKey(entry.getKey()) /* && entry.getValue() != null */) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            current = getCommit(current.getParentId());
        }
        return map;
    }

    // TreeMap file => filename, blobId
    public static TreeMap<String, String> getWorkspaceFiles() {
        var workspaceFiles = new TreeMap<String, String>();
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

    // HashSet file => filename
    public static HashSet<String> getUntrackedFiles(TreeMap<String, String> workspaceFiles, TreeMap<String, String> currentFilesMap) {
        var untrackedFiles = new HashSet<String>();
        for (var filename : workspaceFiles.keySet()) {
            if (!currentFilesMap.containsKey(filename) && !addition.containsKey(filename)) {
                untrackedFiles.add(filename);
            }
        }
        return untrackedFiles;
    }

    public static void checkUntrackedFile(TreeMap<String, String> workspaceFiles, TreeMap<String, String> filesMap) {
        for (var filename : workspaceFiles.keySet()) {
            if (!filesMap.containsKey(filename)) {
                Utils.message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    public static void readItemsFormREMOVAL(File file) {
        if (file.exists()) {
            var content = Utils.readContentsAsString(file);
            var arr = content.split("<<<");
            if (!arr[0].isEmpty()) {
                removal = new HashSet<>(Arrays.asList(arr));
            }
        }
    }
}
