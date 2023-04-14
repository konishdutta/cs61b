package deque;

public class ArrayDeque <T> {
    private T[] lst;
    private int size;
    public ArrayDeque() {
        size = 0;
        lst = (T[]) new Object[8]; //casting object as an array with T elems
    }
    public void addFirst(T item) {
        /* copy the first elements into position 1 */
        System.arraycopy(lst, 0, lst, 1, size);
        lst[0] = item; // add the new item in position 0
        size += 1;
    }
    public void addLast(T item) {
        lst[size] = item;
        size += 1;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public int size() {
        return size;
    }
    public void printDeque() {
        int p = 0;
        while (p != size) {
            System.out.print(lst[p].toString() + " ");
            p += 1;
        }
        System.out.println();
    }
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T val = lst[0];
        System.arraycopy(lst, 1, lst, 0, size - 1);
        size -= 1;
        return val;
    }
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T val = lst[size - 1];
        size -= 1;
        return val;
    }
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return lst[index];
    }

    public boolean equals(Object o) {
        if (o instanceof ArrayDeque && ((ArrayDeque) o).size() == size && equal_helper(this, (ArrayDeque) o)) {
            return true;
        }
        return false;
    }
    private boolean equal_helper(ArrayDeque a, ArrayDeque b) {
        int p = 0;
        while (a.lst[p] == b.lst[p] && p < size) {
            p += 1;
        }
        if (p == size) {
            return true;
        }
        else return false;
    }
}
