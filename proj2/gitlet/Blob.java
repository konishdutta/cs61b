package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable, Comparable<Blob> {
    private String UID;
    private byte[] contents;
    private String name;
    private File file;

    public Blob(File f) {
        this.name = f.getName();
        this.contents = Utils.readContents(f);
        this.file = f;
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

    public File getFile() {
        return this.file;
    }

    @Override
    public int compareTo(Blob o) {
        return this.getName().compareTo(o.getName());
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Blob)) {
            return false;
        }
        Blob c = (Blob) obj;
        return this.UID.equals(c.UID);
    }
    @Override
    public String toString() {
        if (UID == null) {
            return "";
        } else {
            return UID;
        }
    }
}

