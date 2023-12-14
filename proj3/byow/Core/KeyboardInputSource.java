package byow.Core;
import edu.princeton.cs.introcs.StdDraw;

public class KeyboardInputSource implements InputSource {
    public char getNextKey() {
        return Character.toUpperCase(StdDraw.nextKeyTyped());
    }

    public boolean possibleNextInput() {
        return StdDraw.hasNextKeyTyped();
    }
}
