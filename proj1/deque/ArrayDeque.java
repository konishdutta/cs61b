package deque;

public class ArrayDeque <T> implements Deque <T> {
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

    public int checkBounds(int val) {
        if (val >= lst.length) {
            val -= lst.length;
        }
        else if (val < 0) {
            val += lst.length;
        }
        return val;
    }
    public void resize() {
        /* create int vars to help with placement */
        int oldLen = lst.length;
        int newLen = oldLen;

        if (size == lst.length) {
            newLen = oldLen * 2;
        }

        else if (size > 15 && size * 4 <= lst.length) {
            newLen = oldLen / 2;
        }

        else if (oldLen == newLen) {
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
    @Override
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
