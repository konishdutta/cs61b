package deque;

public class ArrayDeque <T> {
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
    public void addFirst(T item) {
        resize(); // method that checks resizing
        lst[first] = item;
        first -= 1;
        size += 1;
        checkBounds(); // method that loops the array around when needed

    }

    public void checkBounds() {
        if (last >= lst.length) {
            last -= lst.length;
        }
        else if (first < 0) {
            first += lst.length;
        }
    }
    public void resize() {
        if (size == lst.length) {
            upsize();
        }
    }
    public void addLast(T item) {
        resize(); // method that checks resizing
        lst[last] = item;
        last += 1;
        size += 1;
        checkBounds();
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public int size() {
        return size;
    }
    public void printDeque() {
        int p = 0;
        while (p < size) {
            System.out.print(this.get(p).toString() + " ");
            p += 1;
        }
        System.out.println();
    }
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T val = lst[first + 1];
        first += 1;
        size -= 1;
        return val;
    }
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T val = lst[last - 1];
        last -= 1;
        size -= 1;
        return val;
    }

    private void upsize(){
        /* create int vars to help with placement */
        int oldLen = lst.length;
        int newLen = oldLen * 2;

        T[] tmp = ((T[]) new Object[newLen]); //create a new Array with double the length
        /* copy the stuff in the front */
        System.arraycopy(lst, 0, tmp, 0, last);
        /* copy the stuff to the back */
        System.arraycopy(lst, last, tmp, last + oldLen, oldLen - last);
        first = last + oldLen - 1;
        lst = tmp;
    }

    private void downsize(){
        if (size > 0 && size * 4 <= lst.length) {
            /* create int vars to help with placement */
            int oldLen = lst.length;
            int newLen = oldLen * 2;
            int newMid = newLen / 2;
            int sizeMid = size / 2;
            int sizeMod = size % 2;

            T[] tmp = ((T[]) new Object[newLen]); //create a new Array with double the length
            System.arraycopy(lst, first + 1, tmp, newMid - sizeMid, size);
            first = newMid - sizeMid - 1;
            last = newMid + sizeMid + sizeMod;
            lst = tmp;
        }
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int pos = first + 1 + index;
        if (pos > lst.length) {
            pos -= lst.length;
        }
        return lst[pos];
    }

    public boolean equals(Object o) {
        if (o instanceof ArrayDeque && ((ArrayDeque) o).size() == size && equal_helper(this, (ArrayDeque) o)) {
            return true;
        }
        return false;
    }
    private boolean equal_helper(ArrayDeque a, ArrayDeque b) {
        int p = 0;
        while (a.get(p) == b.get(p) && p < size) {
            p += 1;
        }
        if (p == size) {
            return true;
        }
        else return false;
    }
}
