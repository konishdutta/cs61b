package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class Blob implements Serializable, Comparable<Blob> {
    private String UID;
    private byte[] contents;
    private String name;

    public Blob(File f) {
        name = f.getName();
        contents = Utils.readContents(f);
        generateUID();
    }
    public String getUID() {
        return UID;
    }

    protected void setUID(String id) {
        this.UID = id;
    }

    public String getName(){
        return this.name;
    }

    public void generateUID() {
        UID = Utils.sha1(name, contents);
    }
    public void saveBlob(File dir) {
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

    public byte[] getContents() {
        return contents;
    }

    @Override
    public int compareTo(Blob o) {
        return this.getName().compareTo(o.getName());
    }
}

