package gitlet;

import java.time.Instant;
import java.util.*;


public class Stage extends Commit {
    private TreeSet<String> addFiles;
    private TreeSet<String> removeFiles;
    public Stage(Commit head) {
        super("stage", Instant.now(), head.getBlobs());
        addFiles = new TreeSet<String>();
        removeFiles = new TreeSet<String>();
        saveCommit(Repository.STAGING_DIR);
    }
    public void add(String filename) {
        addFiles.add(filename);
    }
    public void unstage(String filename) {
        addFiles.remove(filename);
    }
    public void remove(String filename) {
        removeFiles.add(filename);
    }
    public TreeSet<String> getAddFiles() {
        return addFiles;
    }
    public TreeSet<String> getRemoveFiles() {
        return removeFiles;
    }

}
