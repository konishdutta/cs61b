package gitlet;

// TODO: any imports you need here

import java.io.*;
import java.time.*;
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
    private ZonedDateTime timestamp;
    private BlobTree blobs;
    private Commit parent;
    private String branch;

    public Commit(String m, ZonedDateTime ts, BlobTree b, String branch) {
        this.message = m;
        this.timestamp = ts;
        this.parent = Repository.head;
        this.blobs = b;
        generateUID();

        /* change the head marker */
        Repository.head = this;

        /* change the branch */
        this.branch = branch;
        parent.branch = null;

        saveCommit(Repository.COMMITS_DIR);
    }
    private void generateUID() {
        this.UID = Utils.sha1(message, timestamp, blobs, parent);
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
