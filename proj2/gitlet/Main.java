package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author hashjenny
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.setupGitletFolder();
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.loadGitletFolder();
                if (args.length < 2) {
                    System.exit(0);
                }
                var filename = args[1];
                Repository.add(filename);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }



}
