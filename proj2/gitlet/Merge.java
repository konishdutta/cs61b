package gitlet;

import java.time.Instant;
import java.util.*;
public class Merge extends Commit{
    private Commit givenParent;
    public Merge(String givenBranch) {
        super("Merged " + givenBranch + " into " + Repository.currentBranch.getName() + ".", Instant.now(), Repository.stage.getBlobs());
    }
    public void setGivenParent(Commit p) {
        givenParent = p;
    }
    @Override
    public void prettyPrint() {
        System.out.println("===");
        System.out.println("Merge: " + getParent().getUID().substring(0,6) + " " + givenParent.getUID().substring(0,6));
        System.out.println("commit " + this.getUID());
        System.out.println("Date: " + this.getTimestamp());
        System.out.println(this.getMessage());
        System.out.println();
    }
}