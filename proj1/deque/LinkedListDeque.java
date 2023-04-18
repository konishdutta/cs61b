package deque;
import java.util.Iterator;
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T first;
        private Node rest;
        private Node last;
        private Node(T x, Node r, Node l) {
            first = x;
            rest = r;
            last = l;
        }
        private T getNode(int index) {
            if (index == 0) {
                return first;
            } else if (this == null) {
                return null;
            } else {
                return rest.getNode(index - 1);
            }
        }
    }
    private int size;
    private Node sentinel;
    /* create an empty LL Deque */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.rest = sentinel;
        sentinel.last = sentinel;
        size = 0;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void addFirst(T item) {
        sentinel.rest = new Node(item, sentinel.rest, sentinel);
        sentinel.rest.rest.last = sentinel.rest;
        size += 1;
    }
    @Override
    public void addLast(T item) {
        sentinel.last = new Node(item, sentinel, sentinel.last);
        sentinel.last.last.rest = sentinel.last;
        size += 1;
    }
    @Override
    public void printDeque() {
        Node p = sentinel.rest;
        while (p != sentinel) {
            System.out.print(p.first.toString() + " ");
            p = p.rest;
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T val = sentinel.rest.first;
        sentinel.rest = sentinel.rest.rest;
        sentinel.rest.last = sentinel;
        size -= 1;
        return val;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T val = sentinel.last.first;
        sentinel.last = sentinel.last.last;
        sentinel.last.rest = sentinel;
        size -= 1;
        return val;
    }
    @Override
    public T get(int index) {
        Node p = sentinel.rest;
        while (index > 0 && p != sentinel) {
            index -= 1;
            p = p.rest;
        }

        return p.first;
    }
    public boolean equals(Object o) {
        if (o instanceof Deque) {
            Deque other = (Deque) o;
            int h = ((Deque) o).size();
            if (h == this.size()) {
                for (int i = 0; i < h; i += 1) {
                    if (!other.get(i).equals(this.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public T getRecursive(int index) {
        return sentinel.rest.getNode(index);
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
        return new DequeIterator();
    }



}
