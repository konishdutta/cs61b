package gitlet;

public class BlobTree {
    private Blob root;
    public BlobTree branches;

    public BlobTree(Blob r, BlobTree b) {
        this.root = r;
        this.branches = b;
    }
}
