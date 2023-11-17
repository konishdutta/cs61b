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
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File STAGING_BLOBS_DIR = join(STAGING_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File COMMITS_BLOBS_DIR = join(COMMITS_DIR, "blobs");
    public static final File CURRENT_BRANCH = join(GITLET_DIR, "CURRENT_BRANCH");
    public static final File COMMIT_ABBREV = join(GITLET_DIR, "COMMIT_ABBREV");
    private static Commit head = null;
    private static TreeMap<String, Commit> branchMapKV = new TreeMap<>();
    private static TreeMap<String, LinkedList<String>> commitAbbrev = new TreeMap<>();
    private static Stage stage = null;
    private static String currentBranch;

    public static void init() {
        /* make all the files */
        if (GITLET_DIR.exists()) {
            System.out.print("A Gitlet version-control system already exists "
                    + "in the current directory.");
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
        STAGING_DIR.mkdir();
        STAGING_BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        COMMITS_BLOBS_DIR.mkdir();

        Commit initialCommit = new Commit("initial commit", Instant.EPOCH, new BlobList());
        initialCommit.saveCommit(Repository.COMMITS_DIR);
        abbreviateCommit(initialCommit);
        updateMarker(initialCommit);
        head = initialCommit;
        currentBranch = "master";
        branchMapKV.put(currentBranch, head);
        saveRepo();
    }
    public static void abbreviateCommit(Commit c) {
        String fullID = c.getUID();
        String abbrev = fullID.substring(0, 2);
        if (!commitAbbrev.containsKey(abbrev)) {
            commitAbbrev.put(abbrev, new LinkedList<String>());
        }
        commitAbbrev.get(abbrev).add(fullID);
    }
    public static void addBranch(String label) {
        loadRepo();
        if (branchMapKV.containsKey(label)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branchMapKV.put(label, head);
        saveRepo();
    }

    public static void removeBranch(String label) {
        loadRepo();
        if (label.equals(currentBranch)) {
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
        branchMapKV.put(currentBranch, newCommit);
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
        /* delete the file if and only if it exists in the head */
        if (file.exists() && head.getBlobs().containsFileByName(f)) {
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

    public static void log() {
        loadRepo();
        head.printCommit();
    }

    public static void add(String f) {
        loadRepo();
        loadStage();
        BlobList stagedBlobs = stage.getBlobs();

        /*
        if the file is removed, add it back in
         */
        if (stage.getRemoveFiles().contains(f)) {
            stage.getRemoveFiles().remove(f);
            Blob removedBlob = head.getBlobs().returnBlobByName(f);
            File fileLocation = Utils.join(CWD, f);
            writeContents(fileLocation, removedBlob.getContents());
            stagedBlobs.addBlob(removedBlob);
            stage.saveCommit(STAGING_DIR);
            System.exit(0);
        }
        if (!Repository.checkFileExists(f)) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        /* create the blob */
        File file = join(CWD, f);
        Blob b = new Blob(file);


        /*
        * if the filename is in staged
        * and the blob is the same
        * exit
         */
        if (stagedBlobs.contains(b) && stagedBlobs.containsFile(b)) {
            return;
        }
        /*
         * if the filename is in staged
         * but the blob is different
         * do nothing
         */
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
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        head = readObject(HEAD_FILE, Commit.class);
        branchMapKV = readObject(MAP_STRING_COMMIT, TreeMap.class);
        currentBranch = readObject(CURRENT_BRANCH, String.class);
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
        if (!printed) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        loadRepo();
        loadStage();
        System.out.println("=== Branches ===");
        for (String branch : branchMapKV.keySet()) {
            String marker = "";
            if (currentBranch.equals(branch)) {
                marker = "*";
            }
            System.out.println(marker + branch);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        TreeSet add = stage.getAddFiles();
        TreeSet remove = stage.getRemoveFiles();
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
            if (stageBlobs.containsKey(f)) { // blob contains filename
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
            if (!KonishAlgos.binSearch(f, files) && !remove.contains(f)) {
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
        if (c.length() == 40) {
            return c;
        }

        String candidate = searchAbbrev(c);
        if (candidate.equals("error")) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return candidate;
    }
    public static void checkUntrackedFiles() {
        List<String> cwdFiles = plainFilenamesIn(CWD);
        TreeMap<String, String> stageBlobs = stage.getBlobs().getFileSet();
        /*
        check for untracked files
         */
        for (String f: cwdFiles) {
            String untrackedMsg = "There is an untracked file in the way; "
                    + "delete it, or add and commit it first.";
            if (!stageBlobs.containsKey(f)) {
                System.out.println(untrackedMsg);
                System.exit(0);
            } else {
                File workFile = Utils.join(CWD, f);
                Blob workBlob = new Blob(workFile);
                if (!workBlob.getUID().equals(stageBlobs.get(f))) {
                    System.out.println(untrackedMsg);
                    System.exit(0);
                }
            }
        }
    }

    public static void branchCheck(String b) {
        loadRepo();
        loadStage();
        checkUntrackedFiles();

        if (!branchMapKV.containsKey(b)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (currentBranch.equals(b)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        // make the checkout branch the current branch
        currentBranch = b;
        // make the head the last branch in the current branch
        head = branchMapKV.get(currentBranch);
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
        loadStage();
        checkUntrackedFiles();
        c = checkShortenedCommit(c);
        Commit commit = loadCommit(c);
        head = commit;
        stage = new Stage(head);

        /* move current branch pointer */
        branchMapKV.put(currentBranch, head);
        clearCWD();

        // replace all the files
        TreeMap<String, Blob> blobMap = commit.getBlobs().getSet();
        for (String id : blobMap.keySet()) {
            Blob newBlob = blobMap.get(id);
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
        if (currentBranch.equals(b)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (!branchMapKV.containsKey(b)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        loadStage();
        checkUntrackedFiles();
        if (stage.getAddFiles().size() > 0 || stage.getRemoveFiles().size() > 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        Commit commitA = head;
        Commit commitB = branchMapKV.get(b);
        Commit ancestor = findAncestor(commitA, commitB);
        boolean mergeConflict = recon(commitA, commitB, ancestor);

        commit(b, "Merge");
        Merge newHead = (Merge) head;
        newHead.setGivenParent(commitB);

        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        saveRepo();
    }

    public static Commit findAncestor(Commit a, Commit b) {
        HashSet<String> givenHistory = new HashSet<>();
        // get all parents of the given branch
        generateHistory(b, givenHistory);
        // if current is already in the given parents, fast-forward.
        if (givenHistory.contains(a.getUID())) {
            reset(b.getUID());
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        Commit res = traverseAncestor(a, givenHistory);
        /*
        * if the current branch equals the initial given,
        * it means that given was an ancestor of the head
         */
        if (res.equals(b)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        return res;
    }
    public static void generateHistory(Commit c, HashSet<String> res) {
        if (c == null) {
            return;
        }
        res.add(c.getUID());
        generateHistory(c.getParent(), res);
        generateHistory(c.getGivenParent(), res);
    }

    public static Commit traverseAncestor(Commit c, HashSet<String> history) {
        if (c == null) {
            return null;
        }
        if (history.contains(c.getUID())) {
            return c;
        }
        Commit parent1 = traverseAncestor(c.getParent(), history);
        Commit parent2 = traverseAncestor(c.getGivenParent(), history);
        if (parent1 != null && parent2 != null) {
            if (parent1.getTimestamp().compareTo(parent2.getTimestamp()) < 0) {
                return parent1;
            } else {
                return parent2;
            }
        } else if (parent2 == null) {
            return parent1;
        } else {
            return parent2;
        }
    }

    public static boolean recon(Commit curr, Commit given, Commit split) {
        boolean mergeConflict = false;
        BlobList splitBlobs = split.getBlobs();
        BlobList givenBlobs = given.getBlobs();
        BlobList currBlobs = curr.getBlobs();
        Set<String> masterSet = new TreeSet<>(splitBlobs.getFileKeys());
        masterSet.addAll(givenBlobs.getFileKeys());
        masterSet.addAll(currBlobs.getFileKeys());

        List<String> cwdFiles = plainFilenamesIn(CWD);

        for (String f: masterSet) {
            String splitBlob = "";
            if (splitBlobs.returnBlobByName(f) != null) {
                splitBlob = splitBlobs.returnBlobByName(f).toString();
            }
            String givenBlob = "";
            if (givenBlobs.returnBlobByName(f) != null) {
                givenBlob = givenBlobs.returnBlobByName(f).toString();
            }
            String currBlob = "";
            if (currBlobs.returnBlobByName(f) != null) {
                currBlob = currBlobs.returnBlobByName(f).toString();
            }

            if ((currBlob.equals(givenBlob)) || (splitBlob.equals(givenBlob))) {
                continue;
            } else if (currBlob.equals(splitBlob) && !givenBlob.equals("")) {
                commitFileCheck(given.getUID(), f);
                add(f);
            } else if (currBlob.equals(splitBlob) && givenBlob.equals("")) {
                remove(f);
            } else {
                File fileLocation = Utils.join(CWD, f);
                Object arg1 = "<<<<<<< HEAD\n";
                Object arg2 = "";
                if (!currBlob.equals("")) {
                    arg2 = currBlobs.returnBlobByName(f).getContents();
                }
                Object arg3 = "\n=======\n";
                Object arg4 = "";
                if (!givenBlob.equals("")) {
                    arg4 = givenBlobs.returnBlobByName(f).getContents();
                }
                Object arg5 = "\n>>>>>>>";
                writeContents(fileLocation, arg1, arg2, arg3, arg4, arg5);
                add(f);
                mergeConflict = true;
            }
        }
        return mergeConflict;
    }
    public static Commit getHead() {
        return head;
    }

    public static Commit getStage() {
        return stage;
    }

    public static TreeMap getBranches() {
        return branchMapKV;
    }
    public static String getCurrentBranch() {
        return currentBranch;
    }
    private static String searchAbbrev(String c) {
        String subC = c.substring(0, 2);
        if (!commitAbbrev.containsKey(subC)) {
            return "error";
        }
        for (String cnd : commitAbbrev.get(subC)) {
            if (cnd.substring(0, c.length()).equals(c)) {
                return cnd;
            }
        }
        return "error";
    }
}
