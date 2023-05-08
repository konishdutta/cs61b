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
                Repository.init();
                break;
            case "log":
                Repository.log();
                break;
            case "add":
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
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.remove(args[1]);
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "debug":
                Repository.loadRepo();
                Repository.loadStage();
                System.out.println(Repository.head.getBlobs().getSet());
                System.out.println(Repository.stage.getBlobs().getSet());
                break;
            case "checkout":
                Repository.loadRepo();
                if (args.length == 2) {
                    Repository.branchCheck(args[1]);
                } else if (args.length == 3) {
                    Repository.commitFileCheck(Repository.head.getUID(), args[2]);
                } else if (args.length == 4) {
                    Repository.commitFileCheck(args[1], args[3]);
                }
                break;
            default:
                System.out.print("No command with that name exists.");
                System.exit(0);
        }
    }
}
