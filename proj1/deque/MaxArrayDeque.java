package deque;
import java.util.Comparator;

public class MaxArrayDeque <T> extends ArrayDeque <T> {
    public Comparator<T> comp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comp = c;
    }

    public T max() {
        return max(comp);
    }

    public T max(Comparator<T> c) {
        T maxVal = this.get(0);
        for (int i = 1; i < this.size(); i += 1) {
            if (c.compare(this.get(i), maxVal) > 0) {
                maxVal = this.get(i);
            }
        }
        return maxVal;
    }
}
