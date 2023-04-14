package deque;

public class LinkedListDeque <type> {
    public int size;
    public type item;
    public Node sentinel;
    /* create an empty LL Deque */
    public LinkedListDeque(){
        sentinel = new Node(null, null);
        sentinel.rest = sentinel;
        sentinel.last = sentinel;
        size = 0;
    }
    public int size() {
        return size;
    }

    public void addFirst (type item) {
        sentinel.rest = new Node(item, sentinel.rest);
        sentinel.rest.rest.last = sentinel.rest;
        size += 1;
    }

    public void addLast (type item) {
        Node tmp = sentinel.last;
        sentinel.last = new Node(item, sentinel);
        sentinel.last.last = tmp;
        sentinel.last.last.rest = sentinel.last;
        size += 1;
    }
    public boolean isEmpty() {
        if (sentinel.rest == sentinel && sentinel.last == sentinel) {
            return true;
        }
        return false;
    }
    public void printDeque() {
        Node p = sentinel.rest;
        String res = "";
        while (p != sentinel) {
            if (res == "") {
                res = p.first.toString();
            }
            else {
            res = res + " " + p.first.toString();}
            p = p.rest;
        }
        System.out.println(res);
    }

    public type removeFirst() {
        Object val = sentinel.rest.first;
        sentinel.rest = sentinel.rest.rest;
        sentinel.rest.last = sentinel;
        size -= 1;
        return (type) val;
    }

    public type removeLast() {
        Object val = sentinel.last.first;
        sentinel.last = sentinel.last.last;
        sentinel.last.rest = sentinel;
        size -= 1;
        return (type) val;
    }

    public type get(int index) {
        Node p = sentinel.rest;
        while (index > 0 && p != sentinel) {
            index -= 1;
            p = p.rest;
        }

        return (type) p.first;
    }
    public boolean equals(Object o) {
        if (o instanceof LinkedListDeque && equal_helper(this, (LinkedListDeque) o)) {
            return true;
        }
        return false;
    }

    private boolean equal_helper(LinkedListDeque a, LinkedListDeque b) {
        Node p1 = a.sentinel.rest;
        Node p2 = b.sentinel.rest;
        while (p1.first == p2.first && p1 != a.sentinel && p2 != b.sentinel) {
            p1 = p1.rest;
            p2 = p2.rest;
        }
        if (p1 == a.sentinel && p2 == b.sentinel) {
            return true;
        }
        else return false;
    }

    public type getRecursive(int index) {
        return (type) sentinel.rest.getNode(index);
    }

    public class Node<type> {
        public type first;
        public Node rest;

        public Node last;

        public Node(type x, Node r){
            first = x;
            rest = r;
        }

        public type getNode(int index) {
            if (index == 0) {
                return first;
            }
            else if (this == null) {
                return null;
            }
            else {
                return (type) rest.getNode(index - 1);
            }
        }

    }

}
