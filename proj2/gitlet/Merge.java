package gitlet;

import java.time.Instant;
public class Merge extends Commit {
    private Commit givenParent;
    public Merge(String givenBranch) {
        super(getMessage(givenBranch), Instant.now(), Repository.getStage().getBlobs());
    }
    private static String getMessage(String givenBranch) {
        String passMessage = "Merged ";
        passMessage += givenBranch;
        passMessage += " into ";
        passMessage += Repository.getCurrentBranch();
        passMessage += ".";
        return passMessage;
    }
    public void setGivenParent(Commit p) {
        givenParent = p;
    }
    @Override
    public void prettyPrint() {
        System.out.println("===");
        String firstMessage = "Merge: ";
        firstMessage += getParent().getUID().substring(0, 7);
        firstMessage += "";
        firstMessage += givenParent.getUID().substring(0, 7);
        System.out.println("commit " + this.getUID());
        System.out.println(firstMessage);
        System.out.println("Date: " + this.getTimestamp());
        System.out.println(this.getMessage());
        System.out.println();
    }
}
