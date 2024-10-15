package gitlet;

import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author hashjenny
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
        switch (args[0]) {
            case "init":
                Repository.setupGitlet();
                Repository.init();
                break;
            case "add":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                var filename = args[1];
                Repository.add(filename);
                break;
            case "commit":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.commit(args[1]);
                break;
            case "rm":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.loadGitlet();
                Repository.log();
                break;
            case "global-log":
                Repository.loadGitlet();
                Repository.globalLog();
                break;
            case "find":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.find(args[1]);
                break;
            case "status":
                Repository.loadGitlet();
                Repository.status();
                break;
            case "checkout":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.checkout(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "branch":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.reset(args[1]);
                break;
            case "merge":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.merge(args[1]);
                break;
            case "add-remote":
                checkArgsLength(args, 3);
                Repository.loadGitlet();
                Repository.addRemote(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "rm-remote":
                checkArgsLength(args, 2);
                Repository.loadGitlet();
                Repository.rmRemote(args[1]);
            case "push":
                checkArgsLength(args, 3);
                Repository.loadGitlet();
                Repository.push(Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
        }
        Repository.storeGitlet();
    }

    private static void checkArgsLength(String[] args, int atLeast) {
        if (args.length < atLeast) {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }

}
