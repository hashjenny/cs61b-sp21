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
    // file - text
    public static final File HEAD = join(GITLET_DIR, "_Head");
    public static final File CURRENT = join(GITLET_DIR, "_Current");
    // dir - text
    public static final File BRANCH = join(GITLET_DIR, "_Branch");
    // dir - Blob
    public static final File ADDITION = join(GITLET_DIR, "_Addition");
    // file - text
    public static final File REMOVAL = join(GITLET_DIR, "_Removal");
    // dir - remote
    // public static final File REMOTE = join(GITLET_DIR, "_Remote");

    private static Commit head;
    private static String currentBranchName;
    private static Commit currentBranch;
    private static final TreeMap<String, ArrayList<Commit>> BRANCHES = new TreeMap<>();
    // filename -> blob
    private static TreeMap<String, Blob> addition = new TreeMap<>();
    private static HashSet<String> removal = new HashSet<>();
    // remoteName -> remotePath
    // private static TreeMap<String, Remote> remotes = new TreeMap<>();

    public static void setupGitlet() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMIT.mkdir();
        BRANCH.mkdir();
        ADDITION.mkdir();
        // REMOTE.mkdir();
    }

    public static void loadGitlet() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        head = getCommit(Utils.readContentsAsString(HEAD));
        currentBranchName = Utils.readContentsAsString(CURRENT);
        currentBranch = getBranchCommit(currentBranchName);
        addition = putAllBlobs(ADDITION);
        removal = FileUtils.readItemsFormFile(REMOVAL);
        // remotes = putAllRemotes(REMOTE);
        // load all branches as TreeMap (branchName -> branch(commit list))
        getAllBranches();
    }

    public static void storeGitlet() {
        Utils.writeContents(HEAD, head.getId());
        Utils.writeContents(CURRENT, currentBranchName);
        FileUtils.deleteAll(ADDITION);
        FileUtils.writeAllBlobs(ADDITION, addition);
        FileUtils.writeItemsToFile(REMOVAL, removal);
        // FileUtils.deleteAll(REMOTE);
        // FileUtils.writeAllRemotes(REMOTE, remotes);
        var currentBranchFile = Utils.join(BRANCH, currentBranchName);
        Utils.writeContents(currentBranchFile, currentBranch.getId());
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
            message("File does not exist.");
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
            message("Please enter a commit message.");
            System.exit(0);
        }
        if (addition.isEmpty() && removal.isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }


        var commit = new Commit(msg, head.getId());
        commitOperation(commit);
    }

    private static void commitOperation(Commit commit) {
        for (var entry : addition.entrySet()) {
            var blob = entry.getValue();
            commit.addFile(blob.getFilename(), blob.getId());
        }
        for (var removedFile : removal) {
            commit.addFile(removedFile, null);
            Utils.join(CWD, removedFile).delete();
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
            message("No reason to remove the file.");
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
                    message(commit.getId());
                    notMatchFlag = false;
                }
            }
        }
        if (notMatchFlag) {
            message("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void status() {
        message("=== Branches ===");
        for (var branchName : BRANCHES.keySet()) {
            if (branchName.equals(currentBranchName)) {
                message("*%s", branchName);
            } else {
                message("%s", branchName);
            }
        }
        message("");

        message("=== Staged Files ===");
        for (var filename : addition.keySet()) {
            message("%s", filename);
        }
        message("");

        message("=== Removed Files ===");
        for (var filename : removal) {
            message("%s", filename);
        }
        message("");

        var workspace = getWorkspaceFiles();
        var filesMap = getFilesMap(head);

        message("=== Modifications Not Staged For Commit ===");
        // Tracked in the current commit, changed in the working directory,
        // but not staged;
        for (var entry : filesMap.entrySet()) {
            var trackedFilename = entry.getKey();
            var trackedFileId = entry.getValue();
            if (workspace.get(trackedFilename) != null
                    && !workspace.get(trackedFilename).equals(trackedFileId)
                    && !addition.containsKey(trackedFilename)) {
                message("%s (modified)", trackedFilename);
            }
        }
        // Staged for addition, but with different contents than in the working directory;
        // Staged for addition, but deleted in the working directory;
        for (var entry : addition.entrySet()) {
            var filename = entry.getKey();
            var id = entry.getValue().getId();

            var workspaceFileId = workspace.get(filename);
            if (workspaceFileId == null) {
                message("%s (deleted)", filename);
            } else if (!workspaceFileId.equals(id)) {
                message("%s (modified)", filename);
            }
        }

        // Not staged for removal, but tracked in the current commit
        // and deleted from the working directory.
        for (var trackedFile : filesMap.keySet()) {
            if (!removal.contains(trackedFile)
                    && !workspace.containsKey(trackedFile)
                    && filesMap.get(trackedFile) != null) {
                message("%s (deleted)", trackedFile);
            }
        }
        message("");

        // present in the working directory but neither staged for addition nor tracked.
        // This includes files that have been staged for removal
        message("=== Untracked Files ===");
        var untrackedFiles = getUntrackedFiles(workspace, filesMap);
        for (var filename : untrackedFiles) {
            message("%s", filename);
        }
        message("");
    }

    public static void checkout(String... args) {
        var len = args.length;
        if (len == 1) {
            // java gitlet.Main checkout [branch name]
            var branchName = args[0];
            var branchFile = Utils.join(BRANCH, branchName);
            if (!branchFile.exists()) {
                message("No such branch exists.");
                System.exit(0);
            }

            if (branchName.equals(currentBranchName)) {
                message("No need to checkout the current branch.");
                System.exit(0);
            }

            var currentBranchFiles = getFilesMap(head);
            var workspaceFiles = getWorkspaceFiles();
            checkUntrackedFile(workspaceFiles, currentBranchFiles);

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
                message("Incorrect operands.");
                System.exit(0);
            }
            var filename = args[1];
            if (!head.getFiles().containsKey(filename)) {
                message("File does not exist in that commit.");
                System.exit(0);
            }
            var blobId = head.getFiles().get(filename);
            var blob = getBlob(blobId);
            Utils.writeContents(Utils.join(CWD, blob.getFilename()), blob.getContent());
        } else {
            // java gitlet.Main checkout [commit id] -- [file name]
            if (!args[1].equals("--")) {
                message("Incorrect operands.");
                System.exit(0);
            }

            var id = args[0];
            var filename = args[2];
            var commit = getCommitFromShortenName(id);
            if (!commit.getFiles().containsKey(filename)) {
                message("File does not exist in that commit.");
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
            message("A branch with that name already exists.");
            System.exit(0);
        }
        var file = Utils.join(BRANCH, branchName);
        Utils.writeContents(file, head.getId());
    }

    public static void rmBranch(String branchName) {
        var branchFile = Utils.join(BRANCH, branchName);
        if (!branchFile.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }

        if (branchName.equals(currentBranchName)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        branchFile.delete();
    }

    public static void reset(String commitId) {
        var commit = getCommitFromShortenName(commitId);
        var currentBranchFiles = getFilesMap(head);
        // Removes tracked files that are not present in that commit.
        for (var filename : addition.keySet()) {
            if (!currentBranchFiles.containsKey(filename)) {
                addition.remove(filename);
                Utils.join(CWD, filename).delete();
            }
        }

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
        checkMergeError(givenBranchName);
        var conflictFlag = false;
        var givenBranchCommit = BRANCHES.get(givenBranchName).get(0);
        var commonAncestor = getCommonAncestor(givenBranchCommit);
        checkUntrackedFile();
        if (commonAncestor == null) {
            return;
        } else if (commonAncestor.getId().equals(givenBranchCommit.getId())) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (commonAncestor.getId().equals(head.getId())) {
            fastForwarded(givenBranchName, givenBranchCommit);
        }
        var givenBranchFiles = getMergedFilesMap(givenBranchCommit, commonAncestor);
        var currentBranchFiles = getMergedFilesMap(head, commonAncestor);
        var ancestorFiles = getFilesMap(commonAncestor);
        for (var entry : ancestorFiles.entrySet()) {
            var filename = entry.getKey();
            var fileId = entry.getValue();
            if (currentBranchFiles.containsKey(filename)
                    && fileId.equals(currentBranchFiles.get(filename))
                    && givenBranchFiles.get(filename) == null) {
                currentBranchFiles.remove(filename);
            }
            if (givenBranchFiles.containsKey(filename)
                    && fileId.equals(givenBranchFiles.get(filename))
                    && currentBranchFiles.get(filename) == null) {
                givenBranchFiles.remove(filename);
            }
        }
        var bothSet = new TreeSet<>(currentBranchFiles.keySet());
        bothSet.retainAll(givenBranchFiles.keySet());
        var uniqueGivenSet = new TreeSet<>(givenBranchFiles.keySet());
        uniqueGivenSet.removeAll(currentBranchFiles.keySet());
        for (var filename : bothSet) {
            var currentFile = currentBranchFiles.get(filename);
            var givenFile = givenBranchFiles.get(filename);
            if ((currentFile != null && !currentFile.equals(givenFile))
                    || (givenFile != null && !givenFile.equals(currentFile))) {
                String currentContent = "";
                String givenContent = "";
                if (currentFile != null) {
                    var currentBlob = Utils.readObject(Utils.join(GITLET_DIR, currentFile),
                            Blob.class);
                    currentContent = currentBlob.getContent();
                }
                if (givenFile != null) {
                    var givenBlob = Utils.readObject(Utils.join(GITLET_DIR, givenFile), Blob.class);
                    givenContent = givenBlob.getContent();
                }
                var content = "<<<<<<< HEAD\n" + currentContent
                        + "=======\n" + givenContent + ">>>>>>>\n";
                Utils.writeContents(Utils.join(CWD, filename), content);
                var blob = new Blob(filename);
                addition.put(filename, blob);
                conflictFlag = true;
            }
        }
        for (var filename : uniqueGivenSet) {
            var givenFile = givenBranchFiles.get(filename);
            if (givenFile == null) {
                removal.add(filename);
            } else {
                var blob = Utils.readObject(Utils.join(GITLET_DIR, givenFile), Blob.class);
                addition.put(filename, blob);
                Utils.writeContents(Utils.join(CWD, blob.getFilename()), blob.getContent());
            }
        }
        var msg = "Merged " + givenBranchName + " into " + currentBranchName + ".";
        var commit = new Commit(msg, head.getId(), givenBranchCommit.getId());
        commitOperation(commit);
        if (conflictFlag) {
            message("Encountered a merge conflict.");
        }
    }

    private static void checkMergeError(String givenBranchName) {
        if (!BRANCHES.containsKey(givenBranchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (givenBranchName.equals(currentBranchName)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (!addition.isEmpty() || !removal.isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
    }

    private static void fastForwarded(String givenBranchName, Commit givenBranchCommit) {
        var givenBranchFiles = getFilesMap(givenBranchCommit);
        FileUtils.deleteAll(CWD);
        FileUtils.writeAllContentFiles(CWD, givenBranchFiles);
        currentBranch = givenBranchCommit;
        currentBranchName = givenBranchName;
        head = givenBranchCommit;
        message("Current branch fast-forwarded.");
        System.exit(0);
    }

    /*
    public static void addRemote(String... args) {
        // java gitlet.Main add-remote [remote name] [name of remote directory]/.gitlet
        var remoteName = args[0];
        if (remotes.containsKey(remoteName)) {
            message("A remote with that name already exists.");
            System.exit(0);
        }
        var remoteUrl = args[1];
        var url = remoteUrl.replace('/', File.separatorChar);
        var remote = new Remote(remoteName, url);
        remotes.put(remoteName, remote);
    }

    public static void rmRemote(String remoteName) {
        if (!remotes.containsKey(remoteName)) {
            message("A remote with that name does not exist.");
            System.exit(0);
        }
        remotes.remove(remoteName);
    }

    public static void push(String... args) {
        var remoteName = args[0];
        var remoteBranchName = args[1];
        var remote = remotes.get(remoteName);
        if (!new File(remote.getUrl()).exists()) {
            message("Remote directory not found.");
            System.exit(0);
        }
        var currentBranchCommits = BRANCHES.get(currentBranch.getId());
        var commitIds = new ArrayList<String>();
        var blobIds = new ArrayList<String>();
        if (remote.branchExists(remoteBranchName)) {
            var remoteCommit = remote.getRemoteCommit(remoteBranchName);
            var remoteCommitId = remoteCommit.getId();
            for (var currentCommit : currentBranchCommits) {
                var id = currentCommit.getId();
                commitIds.add(id);
                if (id.equals(remoteCommitId)) {
                    break;
                }
            }
            // If the remote branch’s head is not
            // in the history of the current local head,
            if (commitIds.size() == currentBranchCommits.size()) {
                message("Please pull down remote changes before pushing.");
                System.exit(0);
            } else {
                var files = getFilesMap(currentBranch, remoteCommit);
                blobIds = new ArrayList<>(files.values());
                commitIds.remove(commitIds.size() - 1);
            }
        } else {
            // If the Gitlet system on the remote machine exists
            // but does not have the input branch,
            // then simply add the branch to the remote Gitlet.
            blobIds = new ArrayList<>(getFilesMap(currentBranch).values());
            for (var currentCommit : currentBranchCommits) {
                var id = currentCommit.getId();
                commitIds.add(id);
            }
            commitIds.remove(commitIds.size() - 1);

        }
        FileUtils.copyAll(GITLET_DIR, remote.getGitletDir(), blobIds);
        FileUtils.copyAll(COMMIT, remote.getCommitDir(), commitIds);
        remote.writeBranchHead(remoteBranchName, currentBranch.getId());
    }

    public static void fetch(String... args) {
        var remoteName = args[0];
        var remoteBranchName = args[1];
        var remote = remotes.get(remoteName);
        if (!new File(remote.getUrl()).exists()) {
            message("Remote directory not found.");
            System.exit(0);
        }
        if (remote.branchExists(remoteBranchName)) {
            message("That remote does not have that branch.");
            System.exit(0);
        }
        // something to do
    }
     */

    private static Commit getCommonAncestor(String givenBranchName) {
        var givenBranchCommits = BRANCHES.get(givenBranchName);
        var currentBranchCommits = BRANCHES.get(currentBranchName);
        for (var commit : currentBranchCommits) {
            for (var givenCommit : givenBranchCommits) {
                if (commit.getId().equals(givenCommit.getId())) {
                    return commit;
                }
            }
        }
        return null;
    }

    // print utils
    private static void printCommit(Commit commit) {
        message("===");
        message("commit %s", commit.getId());
        if (commit.getMergedParentId().isEmpty()) {
            message("Date: %s", commit.getTimestamp());
            message(commit.getMessage());
        } else {
            message("Merge: %s %s", commit.getParentId().substring(0, 7),
                    commit.getMergedParentId().substring(0, 7));
            message("Date: %s", commit.getTimestamp());
            message(commit.getMessage());
        }
        message("");
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

    /*
    public static TreeMap<String, Remote> putAllRemotes(File folder) {
        var target = new TreeMap<String, Remote>();
        var files = Utils.plainFilenamesIn(folder);
        if (files != null) {
            for (var f : files) {
                var remote = Utils.readObject(Utils.join(folder, f), Remote.class);
                target.put(remote.getName(), remote);
            }
        }
        return target;
    }
     */

    // commit utils
    public static Commit getCommit(String id) {
        if (id.isEmpty()) {
            return null;
        }
        return Utils.readObject(Utils.join(COMMIT, id), Commit.class);
    }

    private static Commit getBranchCommit(String branchName) {
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
            message("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(targetFile, Commit.class);
    }

    // branch utils
    private static String getBranchNameByCommitId(String commitId) {
        for (var entry : BRANCHES.entrySet()) {
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

    private static ArrayList<Commit> getBranch(Commit commit) {
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

    private static void getAllBranches() {
        var files = Utils.plainFilenamesIn(BRANCH);
        if (files != null) {
            for (var file : files) {
                var id = Utils.readContentsAsString(Utils.join(BRANCH, file));
                var commit = getCommit(id);
                if (commit != null) {
                    var branch = getBranch(commit);
                    BRANCHES.put(file, branch);
                }
            }
        }
    }

    // file utils
    // TreeMap file => filename, blobId
    private static TreeMap<String, String> getFilesMap(Commit commit) {
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

    // file utils
    // TreeMap file => filename, blobId
    private static TreeMap<String, String> getFilesMap(Commit currentCommit, Commit endCommit) {
        var map = new TreeMap<String, String>();
        var current = currentCommit;
        var endId = endCommit.getId();
        while (!current.getId().equals(endId)) {
            for (var entry : current.getFiles().entrySet()) {
                if (!map.containsKey(entry.getKey()) /* && entry.getValue() != null */) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            current = getCommit(current.getParentId());
        }
        return map;
    }

    private static TreeMap<String, String> getMergedFilesMap(Commit currentCommit,
                                                             Commit endCommit) {
        var map = new TreeMap<String, String>();
        var path = findPath(currentCommit, endCommit.getId());
        if (path != null) {
            path.remove(path.size() - 1);
            for (var commit : path) {
                for (var entry : commit.getFiles().entrySet()) {
                    if (!map.containsKey(commit.getId())) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return map;
    }

    // TreeMap file => filename, blobId
    private static TreeMap<String, String> getWorkspaceFiles() {
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
    private static HashSet<String> getUntrackedFiles(TreeMap<String, String> workspaceFiles,
                                                     TreeMap<String, String> currentFilesMap) {
        var untrackedFiles = new HashSet<String>();
        for (var filename : workspaceFiles.keySet()) {
            if (!currentFilesMap.containsKey(filename) && !addition.containsKey(filename)) {
                untrackedFiles.add(filename);
            }
        }
        return untrackedFiles;
    }

    private static void checkUntrackedFile(TreeMap<String, String> workspaceFiles,
                                           TreeMap<String, String> filesMap) {
        for (var filename : workspaceFiles.keySet()) {
            if (!filesMap.containsKey(filename)
                    || !workspaceFiles.get(filename).equals(filesMap.get(filename))) {
                message("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static void checkUntrackedFile() {
        var workspaceFiles = getWorkspaceFiles();
        var currentFiles = getFilesMap(currentBranch);
        for (var filename : workspaceFiles.keySet()) {
            if (!currentFiles.containsKey(filename)
                    || !workspaceFiles.get(filename).equals(currentFiles.get(filename))) {
                message("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static TreeMap<String, Integer> travelTree(Commit commit,
                                                       int depth,
                                                       TreeMap<String, Integer> depthMap) {
        if (depthMap == null) {
            depthMap = new TreeMap<>();
        }
        if (commit != null) {
            depthMap.put(commit.getId(), depth);
            var parentId = commit.getParentId();
            var mergedId = commit.getMergedParentId();
            var set = new TreeSet<String>();
            if (parentId != null) {
                set.add(parentId);
            }
            if (mergedId != null) {
                set.add(mergedId);
            }
            for (var id : set) {
                travelTree(getCommit(id), depth + 1, depthMap);
            }
        }
        return depthMap;
    }

    private static Commit getCommonAncestor(Commit givenCommit) {
        var currentTree = travelTree(currentBranch, 0, null);
        var givenTree = travelTree(givenCommit, 0, null);

        var commonCommit = new TreeSet<>(currentTree.keySet());
        commonCommit.retainAll(new TreeSet<>(givenTree.keySet()));

        var min = Integer.MAX_VALUE;
        String minId = null;

        for (var id : commonCommit) {
            var depth = Math.max(currentTree.get(id), givenTree.get(id));
            if (depth < min) {
                min = depth;
                minId = id;
            }
        }
        if (minId == null) {
            return null;
        }
        return getCommit(minId);
    }

    public static ArrayList<Commit> findPath(Commit commit, String targetId) {
        if (commit == null) {
            return null;
        }

        var stack = new Stack<Commit>();
        var path = new Stack<ArrayList<Commit>>();

        stack.push(commit);
        path.push(new ArrayList<>(List.of(commit)));

        while (!stack.isEmpty()) {
            var current = stack.pop();
            var currentPath = path.pop();
            if (current.getId().equals(targetId)) {
                return currentPath;
            }
            if (!current.getParentId().isEmpty()) {
                stack.push(getCommit(current.getParentId()));
                var parentPath = new ArrayList<>(currentPath);
                parentPath.add(getCommit(current.getParentId()));
                path.push(parentPath);
            }
            if (!current.getMergedParentId().isEmpty()) {
                stack.push(getCommit(current.getMergedParentId()));
                var mergedPath = new ArrayList<>(currentPath);
                mergedPath.add(getCommit(current.getMergedParentId()));
                path.push(mergedPath);
            }
        }
        return null;
    }

}
