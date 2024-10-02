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
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.setupGitlet();
                Repository.init();
                break;
            case "add":
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                var filename = args[1];
                Repository.add(filename);
                break;
            case "commit":
                Repository.loadGitlet();
                if (args.length < 2) {
                    throw Utils.error("Please enter a commit message.");
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
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
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                Repository.find(args[1]);
                break;
            case "status":
                Repository.loadGitlet();
                Repository.status();
                break;
            case "checkout":
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }

                Repository.checkout(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "branch":
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.loadGitlet();
                if (args.length < 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                Repository.merge(args[1]);
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
        }
        Repository.storeGitlet();
    }



}
