package gitlet;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.plainFilenamesIn;

public class BlobList implements Serializable {
    private HashMap<String, Blob> blobs = new HashMap<String, Blob>();
    private HashMap<String, String> fileMap = new HashMap<String, String>();
    private String UID;
    public BlobList(){
        UID = "";
    }
    public String getUID(){
        generateUID();
        return UID;
    }
    public HashMap<String, Blob> getSet(){
        return blobs;
    }
    public HashMap<String, String> getFileSet(){
        return fileMap;
    }
    public Set<String> getFileKeys(){
        return fileMap.keySet();
    }
    private void generateUID(){
        UID = Utils.sha1(blobs.keySet().toArray());
    }
    public void addBlob(Blob b) {
        blobs.put(b.getUID(), b);
        fileMap.put(b.getName(), b.getUID());
    }

    public void removeBlob(Blob b) {
        blobs.remove(b.getUID());
        fileMap.remove(b.getName());
    }
    public void removeBlobByFile(Blob b) {
        String name = b.getName();
        String UID = fileMap.get(name);
        blobs.remove(UID);
        fileMap.remove(name);
    }

    public boolean contains(Blob b) {
        return blobs.containsKey(b.getUID());
    }

    public boolean containsFile(Blob b) {
        return fileMap.containsKey(b.getName());
    }

    public String returnFileUID(Blob b) {
        return fileMap.get(b.getName());
    }

    public Blob returnBlob(String b) {
        return blobs.get(b);
    }

}
