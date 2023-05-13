package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "add":
                validateNumArgs(args, 2);
                if (!Repository.checkFileExists(args[1])) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                validateNumArgs(args, 2);
                Repository.commit(args[1], "Commit");
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.remove(args[1]);
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "debug":
                Repository.loadRepo();
                Repository.loadStage();
                System.out.println("Head: " + Repository.head.getBlobs().getSet());
                System.out.println("Stage: " + Repository.stage.getBlobs().getSet());
                System.out.println("Branches: " + Repository.branchMapKV);
                break;
            case "checkout":
                Repository.loadRepo();
                if (args.length == 2) {
                    Repository.branchCheck(args[1]);
                } else if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.commitFileCheck(Repository.head.getUID(), args[2]);
                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.commitFileCheck(args[1], args[3]);
                } else {
                    validateNumArgs(args, 4);
                }
                break;
            case "branch":
                validateNumArgs(args, 2);
                Repository.addBranch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                validateNumArgs(args, 2);
                Repository.mergeOrchestrator(args[1]);
                break;
            default:
                System.out.print("No command with that name exists.");
                System.exit(0);
        }
    }
    public static void validateNumArgs(String[] args, Integer n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
