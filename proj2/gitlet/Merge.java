package gitlet;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class Merge extends Commit {
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
    @Override
    public void prettyPrint() {
        System.out.println("===");
        String firstMessage = "Merge: ";
        firstMessage += getParent().getUID().substring(0, 7);
        firstMessage += " ";
        firstMessage += getGivenParent().getUID().substring(0, 7);
        System.out.println("commit " + this.getUID());
        System.out.println(firstMessage);
        System.out.println("Date: " + this.getTimestamp());
        System.out.println(this.getMessage());
        System.out.println();
    }

}
