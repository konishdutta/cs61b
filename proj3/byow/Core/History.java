package byow.Core;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class History implements Serializable {
    private String commands = "N";
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File SAVEFILE = Paths.get(CWD.getPath(), "savefile.txt").toFile();

    public void addCommand(char c) {
        commands += c;
    }

    public void addCommands(String s) {
        commands  = s;
    }

    public String getCommands() {
        return commands;
    }

    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    public void save() {
        writeContents(SAVEFILE, commands);
    }
}
