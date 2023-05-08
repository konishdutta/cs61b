package gitlet;

import java.time.Instant;
import java.util.*;


public class Stage extends Commit {
    private HashSet<String> addFiles;
    private HashSet<String> removeFiles;
    public Stage(Commit head) {
        super("stage", Instant.now(), head.getBlobs());
        addFiles = new HashSet<String>();
        removeFiles = new HashSet<String>();
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
    public HashSet<String> getAddFiles(){
        return addFiles;
    }
    public HashSet<String> getRemoveFiles(){
        return removeFiles;
    }

}