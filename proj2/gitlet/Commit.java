package gitlet;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author kdutta
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
        this.parent = Repository.getHead();
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
        if (this.parent != null) {
            arg4 = this.parent.UID;
        }
        this.UID = Utils.sha1(message, timestamp.toString(), arg3, arg4);
    }

    public void saveCommit(File dir) {
        File destination = join(dir, this.UID);
        if (!destination.exists()) {
            try {
                destination.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeObject(destination, this);
    }
    public BlobList getBlobs() {
        if (this.blobs == null) {
            return new BlobList();
        }
        return this.blobs;
    }
    public String getUID() {
        return this.UID;
    }
    public String getTimestamp() {
        String pattern = "EEE MMM d HH:mm:ss yyyy Z";
        ZoneId zone = ZoneId.systemDefault();
        DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern(pattern).withZone(zone);
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

    public int size() {
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
        return this.UID.equals(c.UID);
    }

    @Override
    public int hashCode() {
        return UID.hashCode();
    }

    @Override
    public int compareTo(Commit o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
    @Override
    public String toString() {
        return "UID: " + getUID() + "\nBlobs: " + getBlobs().toString();
    }

}
