package gitlet;

// TODO: any imports you need here

import java.io.*;
import java.time.*;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    private String UID;
    private String message;
    private Instant timestamp;
    private BlobTree blobs;
    private Commit parent;
    private String branch;

    public Commit(String m, Instant ts, BlobTree b, String branch) {
        this.message = m;
        this.timestamp = ts;
        this.parent = Repository.head;
        this.blobs = b;
        generateUID();

        /* change the head marker */
        Repository.head = this;

        /* change the branch */
        this.branch = branch;
        if(parent != null) {
            parent.branch = null;
        }

        saveCommit(Repository.COMMITS_DIR);
    }
    private void generateUID() {
        String arg3 = "null";
        String arg4 = "null";
        if (this.blobs != null) {
            arg3 = this.blobs.UID;
        }
        if(this.parent != null) {
            arg4 = this.parent.UID;
        }
        this.UID = Utils.sha1(message, timestamp.toString(), arg3, arg4);
    }

    private void saveCommit(File dir) {
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


}
