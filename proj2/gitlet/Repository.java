package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.*;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.restrictedDelete;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author kdutta
 */
public class Repository implements Serializable {
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File MAP_STRING_COMMIT = join(GITLET_DIR, "MAP_KV");
    public static final File MAP_COMMIT_STRING = join(GITLET_DIR, "MAP_VK");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File STAGING_BLOBS_DIR = join(STAGING_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File COMMITS_BLOBS_DIR = join(COMMITS_DIR, "blobs");
    public static final File CURRENT_BRANCH = join(GITLET_DIR, "CURRENT_BRANCH");
    public static final File COMMIT_ABBREV = join(GITLET_DIR, "COMMIT_ABBREV");
    public static Commit head = null;
    public static TreeMap<String, Branch> branchMapKV = new TreeMap<String, Branch>();
    public static TreeMap<String, String> commitAbbrev = new TreeMap<String, String>();
    public static Stage stage = null;
    public static Branch currentBranch;

    public static void init() {
        /* make all the files */
        if (GITLET_DIR.exists()) {
            System.out.print("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdirs();
        try {
            HEAD_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            MAP_STRING_COMMIT.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            MAP_COMMIT_STRING.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        STAGING_DIR.mkdir();
        STAGING_BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        COMMITS_BLOBS_DIR.mkdir();

        Commit initialCommit = new Commit("initial commit", Instant.EPOCH, new BlobList());
        initialCommit.saveCommit(Repository.COMMITS_DIR);
        abbreviateCommit(initialCommit);
        updateMarker(initialCommit);
        Branch masterBranch = new Branch(initialCommit, "master");
        currentBranch = masterBranch;
        saveRepo();
    }
    public static void abbreviateCommit(Commit c) {
        String UID = c.getUID();
        String abbrev = UID.substring(0, 6);
        if (commitAbbrev.containsKey(abbrev)) {
            commitAbbrev.put(abbrev, "collision");
        } else {
            commitAbbrev.put(abbrev, UID);
        }
    }
    public static void addBranch(String label){
        loadRepo();
        if (branchMapKV.containsKey(label)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Branch newBranch = new Branch(head, label);
        saveRepo();
    }

    public static void removeBranch(String label){
        loadRepo();
        if (label.equals(currentBranch.getName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        if (!branchMapKV.containsKey(label)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        branchMapKV.remove(label);
        saveRepo();
    }

    public static void updateMarker(Commit c) {
        head = c;
    }
    public static void commit(String m, String type) {
        /* takes everything in the staging area and commits it */
        loadRepo();
        loadStage();
        if (stage.getBlobs().equals(head.getBlobs())) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit newCommit = null;
        if (type.equals("Commit")) {
            newCommit = createCommit(m);
        } else if (type.equals("Merge")) {
            newCommit = createMerge(m);
        }
        updateMarker(newCommit);
        newCommit.saveCommit(Repository.COMMITS_DIR);
        abbreviateCommit(newCommit);
        currentBranch.push(newCommit);
        commitStagedBlobs();
        saveRepo();
    }

    public static Commit createCommit(String m) {
        Commit newCommit = new Commit(m, Instant.now(), stage.getBlobs());
        return newCommit;
    }

    public static Merge createMerge(String m) {
        Merge newCommit = new Merge(m);
        return newCommit;
    }

    public static void remove(String f) {
        loadRepo();
        loadStage();
        File file = Utils.join(CWD, f);
        if (file.exists()) {
            Blob b = new Blob(file);
            Utils.restrictedDelete(file);
            deleteStagedBlob(b);
        }
        /* check if file is staged and remove */
        if (stage.getBlobs().containsFileByName(f)) {
            stage.getBlobs().removeBlobByFilename(f);
            stage.unstage(f);
            if (head.getBlobs().containsFileByName(f)) {
                stage.remove(f);
            }
            stage.saveCommit(STAGING_DIR);
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public static void deleteStagedBlob(Blob b) {
        File blobLocation = Utils.join(STAGING_BLOBS_DIR, b.getUID());
        if (blobLocation.exists()) {
            blobLocation.delete();
        }
    }

    public static void loadStage() {
        List<String> stageList = plainFilenamesIn(STAGING_DIR);
        if (stageList.size() == 0) {
            stage = new Stage(head);
        } else {
            stage = Utils.readObject(Utils.join(STAGING_DIR, stageList.get(0)), Stage.class);
        }
    }

    public static void updateBranchMap(String label, Branch branch) {
        branchMapKV.put(label, branch);
    }

    public static void log() {
        loadRepo();
        head.printCommit();
    }

    public static void add(String f) {
        loadRepo();
        loadStage();
        /* create the blob */
        File file = join(CWD, f);
        Blob b = new Blob(file);

        BlobList stagedBlobs = stage.getBlobs();
        if (stagedBlobs.contains(b) && stagedBlobs.containsFile(b)) {
            return;
        }
        if (stagedBlobs.containsFile(b)) {
            String duplicateBlobName = stagedBlobs.returnFileUID(b);
            Blob duplicateBlob = stagedBlobs.returnBlob(duplicateBlobName);
            stagedBlobs.removeBlob(duplicateBlob);
            deleteStagedBlob(duplicateBlob);
        }
        if (head.getBlobs().contains(b)) {
            stagedBlobs.addBlob(b);
            stage.saveCommit(STAGING_DIR);
            return;
        }
        stagedBlobs.addBlob(b);
        stage.add(b.getName());
        stage.saveCommit(STAGING_DIR);
        b.saveBlob(STAGING_BLOBS_DIR);
    }

    public static void saveRepo() {
        writeObject(HEAD_FILE, head);
        writeObject(MAP_STRING_COMMIT, branchMapKV);
        writeObject(CURRENT_BRANCH, currentBranch);
        writeObject(COMMIT_ABBREV, commitAbbrev);
    }

    public static void loadRepo() {
        head = readObject(HEAD_FILE, Commit.class);
        branchMapKV = readObject(MAP_STRING_COMMIT, TreeMap.class);
        currentBranch = readObject(CURRENT_BRANCH, Branch.class);
        commitAbbrev = readObject(COMMIT_ABBREV, TreeMap.class);
    }

    public static boolean checkFileExists(String f) {
        File file = join(CWD, f);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    public static void commitStagedBlobs() {
        List<String> newBlobs = plainFilenamesIn(STAGING_BLOBS_DIR);
        for (String b : newBlobs) {
            Blob newBlob = stage.getBlobs().returnBlob(b);
            File stagedBlob = join(STAGING_BLOBS_DIR, b);
            File destination = join(COMMITS_BLOBS_DIR, b);
            stagedBlob.renameTo(destination);
        }
        stage = new Stage(head);
    }
    /* clear staging without saving blobs */
    public static void clearStaging() {
        List<String> newBlobs = plainFilenamesIn(STAGING_BLOBS_DIR);
        for (String b : newBlobs) {
            File stagedFile = join(STAGING_BLOBS_DIR, b);
            stagedFile.delete();
        }
        stage = new Stage(head);
    }

    public static void globalLog() {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String c : commits) {
            File cFile = Utils.join(COMMITS_DIR, c);
            Commit readCommit = Utils.readObject(cFile, Commit.class);
            readCommit.prettyPrint();
        }
    }

    public static void find(String m) {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        boolean printed = false;
        for (String c : commits) {
            File cFile = Utils.join(COMMITS_DIR, c);
            Commit readCommit = Utils.readObject(cFile, Commit.class);
            if (readCommit.getMessage().equals(m)) {
                System.out.println(readCommit.getUID());
                printed = true;
            }
        }
        if (printed == false) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        loadRepo();
        loadStage();
        System.out.println("=== Branches ===");
        for (String branch : branchMapKV.keySet()) {
            String marker = "";
            if (currentBranch.getName().equals(branch)) {
                marker = "*";
            }
            System.out.println(marker + branch);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        Set add = stage.getAddFiles();
        Set remove = stage.getRemoveFiles();
        TreeMap<String, String> blobs = head.getBlobs().getFileSet();
        TreeMap<String, String> stageBlobs = stage.getBlobs().getFileSet();
        for (Object f : add) {
            System.out.println(f);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (Object f : remove) {
            System.out.println(f);
        }
        System.out.println();
        List<String> files = plainFilenamesIn(CWD);
        if (files.size() > 0) {
            KonishAlgos.mergeSort(files);
        }
        String modString = "";
        String untrackedString = "";
        for (String f : files) {
            if (stageBlobs.containsKey(f)) { // blob does contain the filename
                File workFile = Utils.join(CWD, f);
                Blob workBlob = new Blob(workFile);
                if (!workBlob.getUID().equals(stageBlobs.get(f))) {
                    modString += f + " (modified)\n";
                }
            } else if (!stageBlobs.containsKey(f)) {
                untrackedString += f + "\n";
            }
        }
        for (String f : head.getBlobs().getFileKeys()) {
            if(!KonishAlgos.binSearch(f, files) && !remove.contains(f)) {
                // TO DO: make sure it's not in deleted
                modString += f + " (deleted)\n";
            }
        }
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(modString);
        System.out.println("=== Untracked Files ===");
        System.out.println(untrackedString);
    }
    public static void clearCWD() {
        List<String> files = plainFilenamesIn(CWD);
        for (String f : files) {
            File deleteTarget = Utils.join(CWD, f);
            restrictedDelete(deleteTarget);
        }
    }

    public static String checkShortenedCommit(String c) {
        if (c.length() != 6) {
            return c;
        }
        if (!commitAbbrev.containsKey(c)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else if (commitAbbrev.get(c).equals("collision")) {
            System.out.println("Abbreviated commit has duplicates. Please use full commit UID");
            System.exit(0);
        }
        return commitAbbrev.get(c);
    }

    public static void branchCheck(String b) {
        if(!branchMapKV.containsKey(b)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        // make the checkout branch the current branch
        currentBranch = branchMapKV.get(b);
        // make the head the last branch in the current branch
        head = currentBranch.peek();
        saveRepo();
        // clear the staging area and CWD
        reset(head.getUID());
    }
    public static Commit loadCommit(String c) {
        File commitFile = Utils.join(COMMITS_DIR, c);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = readObject(commitFile, Commit.class);
        return commit;
    }
    public static void reset(String c) {
        loadRepo();
        c = checkShortenedCommit(c);
        Commit commit = loadCommit(c);
        clearStaging();
        clearCWD();
        // replace all the files
        TreeMap<String, Blob> blobMap = commit.getBlobs().getSet();
        for (String UID : blobMap.keySet()) {
            Blob newBlob = blobMap.get(UID);
            File fileLocation = Utils.join(CWD, newBlob.getName());
            writeContents(fileLocation, newBlob.getContents());
        }
        saveRepo();
    }

    public static void commitFileCheck(String c, String f) {
        c = checkShortenedCommit(c);
        Commit commit = loadCommit(c);
        BlobList headBlobs = commit.getBlobs();
        if (!headBlobs.containsFileByName(f)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File fileLocation = Utils.join(CWD, f);
        String replaceBlobName = headBlobs.returnFileUIDByName(f);
        Blob replaceBlob = headBlobs.returnBlob(replaceBlobName);
        writeContents(fileLocation, replaceBlob.getContents());
    }
    public static void mergeOrchestrator(String b) {
        loadRepo();
        Branch branchB = branchMapKV.get(b);
        Commit commitA = head;
        Commit commitB = branchB.peek();
        Commit ancestor = findAncestor(commitA, commitB);
        recon(commitA, commitB, ancestor);
        commit(b, "Merge");
        Merge newHead = (Merge) head;
        newHead.setGivenParent(commitB);
        System.out.println("Merged " + b + " into " + currentBranch.getName());
        saveRepo();
    }

    public static Commit findAncestor(Commit a, Commit b) {
        HashSet<String> givenHistory = new HashSet<String>();
        Commit curr = a;
        Commit given = b;
        // get all parents of the given branch
        while (given != null) {
            givenHistory.add(given.getUID());
            given = given.getParent();
        }
        // if current is already in the given parents, fast-forward.
        if (givenHistory.contains(curr.getUID())) {
            reset(b.getUID());
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        while (!givenHistory.contains(curr.getUID())) {
            curr = curr.getParent();
        }

        // if the current branch equals the initial given, it means that given was an ancestor of the head
        if (curr.equals(b)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        return curr;
    }

    public static void recon(Commit curr, Commit given, Commit split) {
        BlobList splitBlobs = split.getBlobs();
        BlobList givenBlobs = given.getBlobs();
        BlobList currBlobs = curr.getBlobs();
        Set<String> masterSet = new TreeSet<>(splitBlobs.getFileKeys());
        masterSet.addAll(givenBlobs.getFileKeys());
        masterSet.addAll(currBlobs.getFileKeys());

        for (String f: masterSet) {
            Blob splitBlob = splitBlobs.returnBlobByName(f);
            Blob givenBlob = givenBlobs.returnBlobByName(f);
            Blob currBlob = currBlobs.returnBlobByName(f);

            if (currBlob != null && splitBlob != null && givenBlob != null) {
                if (currBlob.equals(splitBlob) && !currBlob.equals(givenBlob)) {
                    commitFileCheck(given.getUID(), f);
                    add(f);
                } else if (givenBlob.equals(splitBlob) && !currBlob.equals(givenBlob)) {
                    continue;
                } else if (currBlob.equals(splitBlob)) {
                    System.out.println("case3");
                    continue;
                }

            }
            if (splitBlob == null && givenBlob == null) {
                continue;
            }  else if (splitBlob == null && currBlob == null) {
                commitFileCheck(given.getUID(), f);
                add(f);
            }  else if (currBlob.equals(splitBlob) && givenBlob == null) {
                System.out.println("case6");
                remove(f);
            } else if (givenBlob.equals(splitBlob) && currBlob == null) {
                System.out.println("case7");
                continue;
            } else if(!currBlob.equals(splitBlob) && !currBlob.equals(givenBlob)) {
                File fileLocation = Utils.join(CWD, f);
                writeContents(fileLocation, "<<<<<<< HEAD\n", currBlob.getContents(), "=======\n", givenBlob.getContents(), ">>>>>>>");
                add(f);
            }
        }
    }
}
