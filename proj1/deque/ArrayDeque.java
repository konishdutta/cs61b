package deque;
import java.util.Iterator;
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] lst;
    private int size;
    private int first;
    private int last;
    public ArrayDeque() {
        size = 0;
        lst = (T[]) new Object[8]; //casting object as an array with T elems
        first = 3;
        last = 4;
    }
    @Override
    public void addFirst(T item) {
        resize(); // method that checks resizing
        lst[first] = item;
        first -= 1;
        size += 1;
        first = checkBounds(first); // method that loops the array around when needed

    }

    private int checkBounds(int val) {
        if (val >= lst.length) {
            val -= lst.length;
        } else if (val < 0) {
            val += lst.length;
        }
        return val;
    }
    private void resize() {
        /* create int vars to help with placement */
        int oldLen = lst.length;
        int newLen = oldLen;

        if (size == lst.length) {
            newLen = oldLen * 2;
        } else if (size > 15 && size * 4 <= lst.length) {
            newLen = oldLen / 2;
        } else if (oldLen == newLen) {
            return;
        }

        T[] tmp = ((T[]) new Object[newLen]); //create a new Array with newLen
        for (int i = 0; i < size; i += 1) {
            tmp[i] = this.get(i);
        }
        first = newLen - 1;
        last = size;
        lst = tmp;
    }
    @Override
    public void addLast(T item) {
        resize(); // method that checks resizing
        lst[last] = item;
        last += 1;
        size += 1;
        last = checkBounds(last);
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        int p = 0;
        while (p < size) {
            System.out.print(this.get(p).toString() + " ");
            p += 1;
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T val = this.get(0);
        first += 1;
        size -= 1;
        first = checkBounds(first);
        resize();
        return val;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T val = this.get(size - 1);
        last -= 1;
        size -= 1;
        last = checkBounds(last);
        resize();
        return val;
    }
    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int pos = checkBounds(first + 1 + index);
        return lst[pos];
    }
    public boolean equals(Object o) {
        if (o instanceof Deque) {
            Deque other = (Deque) o;
            int h = ((Deque) o).size();
            if (h == this.size()) {
                for (int i = 0; i < h; i += 1) {
                    if (other.get(i) != this.get(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private class DequeIterator implements Iterator<T> {
        private int p;
        DequeIterator() {
            p = 0;
        }
        public boolean hasNext() {
            return p < size;
        }

        public T next() {
            T val = get(p);
            p += 1;
            return val;
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new ArrayDeque.DequeIterator();
    }

}
