package randomizedtest;

public class IntList {
    public int first;
    public IntList rest;
    public IntList (int f, IntList r) {
        this.first = f;
        this.rest = r;
    }
    public static IntList reverse(IntList lst) {
        IntList prev = null;
        IntList current = lst;

        while (current != null) {
            IntList next = current.rest;
            current.rest = prev;
            prev = current;
            current = next;
        }
        return prev;
    }

    public static IntList[] partition(IntList lst, int k) {
        IntList[] array = new IntList[k];
        int index = 0;
        IntList L = reverse(lst);
        while (L != null) {
            int i = index % k;
            array[i] = new IntList(L.first, array[i]);
            L = L.rest;
            index += 1;
        }
        return array;
    }

    public static void main(String[] args) {
        IntList a = new IntList(4, null);
        a = new IntList (5, a);
        a = new IntList (6, a);
        a = new IntList (7, a);
        a = new IntList (8, a);
        a = new IntList (9, a);
        a = new IntList (10, a);
        a = new IntList (11, a);
        a = new IntList (12, a);
        a = new IntList (13, a);
        a = new IntList (14, a);
        a = new IntList (15, a);
        a = new IntList (16, a);
        IntList[] b = partition(a, 3);
        System.out.println(b);
    }
}
