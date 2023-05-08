package gitlet;

// TODO: any imports you need here

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable, Comparable<Commit> {
    private String UID;
    private String message;
    private Instant timestamp;
    private BlobList blobs;
    private Commit parent;

    public Commit(String m, Instant ts, BlobList blobs) {
        this.message = m;
        this.timestamp = ts;
        this.parent = Repository.head;
        this.blobs = blobs;
        generateUID();
    }
    private void generateUID() {
        if (this instanceof Stage) {
            UID = "staging";
            return;
        }
        String arg3 = "";
        String arg4 = "";
        if (this.blobs != null) {
            arg3 = this.blobs.getUID();
        }
        if(this.parent != null) {
            arg4 = this.parent.UID;
        }
        this.UID = Utils.sha1(message, timestamp.toString(), arg3, arg4);
    }

    public void saveCommit(File dir) {
        File destination = join(dir, this.UID);
        if(!destination.exists()) {
            try {
                destination.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeObject(destination, this);
    }
    public BlobList getBlobs(){
        if (this.blobs == null) {
            return new BlobList();
        }
        return this.blobs;
    }
    public String getUID() {
        return this.UID;
    }
    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy Z").withZone(ZoneId.systemDefault());
        return formatter.format(this.timestamp);
    }
    public String getMessage() {
        return this.message;
    }
    public Commit getParent() {
        return this.parent;
    }
    public void prettyPrint() {
        System.out.println("===");
        System.out.println("commit " + this.getUID());
        System.out.println("Date: " + this.getTimestamp());
        System.out.println(this.getMessage());
        System.out.println();
        //TODO Merge
    }
    public void printCommit() {
        prettyPrint();
        if (this.parent == null) {
            return;
        }
        this.parent.printCommit();
    }

    public void updateParent(Commit p) {
        this.parent = p;
    }

    public int size(){
        return blobs.getSet().size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Commit)) {
            return false;
        }
        Commit c = (Commit) obj;
        return this.getBlobs().getSet().keySet().equals(c.getBlobs().getSet().keySet());
    }

    @Override
    public int compareTo(Commit o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
