package byow.Core;

public class StringInputSource implements InputSource {
    private int index;
    private String input;
    public StringInputSource(String s) {
        index = 0;
        input = s;
    }
    public char getNextKey() {
        char res = input.charAt(index);
        index += 1;
        return res;
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }
}
