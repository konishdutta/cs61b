package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.*;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REPOSITORY_FILE = join(GITLET_DIR, "repository");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File STAGING_BLOBS_DIR = join(STAGING_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File COMMITS_BLOBS_DIR = join(COMMITS_DIR, "blobs");
    public static Commit head = null;
    public static HashMap<String, Commit> branchMapKV = new HashMap<String, Commit>();
    public static HashMap<Commit, String> branchMapVK = new HashMap<Commit, String>();

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.print("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        try {
            REPOSITORY_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        STAGING_DIR.mkdir();
        STAGING_BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        COMMITS_BLOBS_DIR.mkdir();
        Commit initialCommit = new Commit("initial commit", Instant.EPOCH, null, "master");
        updateBranchMap("master", initialCommit);
        saveRepo();
    }

    public static void updateBranchMap(String branch, Commit commit){
        Commit tmp = branchMapKV.get(branch);
        if (tmp != null) {
            branchMapVK.remove(tmp);
        }
        branchMapKV.put(branch, commit);
        branchMapVK.put(commit, branch);
    }

    public static void saveRepo(){
        writeObject(REPOSITORY_FILE, Repository.class);
    }

}
