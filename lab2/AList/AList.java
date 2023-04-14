package AList;

public class AList {
    private int size;
    private int[] items;
    public AList() {
        items = new int[0];
        size = 0;
    }

    public void addLast(int x) {
        int[] temp = new int[size + 1];
        System.arraycopy(items, 0, temp, 0, size);
        temp[size] = x;
        size += 1;
        items = temp;
    }
    public int getLast() {
        if (items != null){
            return items[size - 1];
        }
        else return -1;
    }
    public int get(int x) {
        return items[x];
    }
    public int size() {
        return size;
    }
    public int removeLast() {
        int[] temp = new int[size - 1];
        System.arraycopy(items, 0, temp, 0, size - 1);
        size -= 1;
        int last_val = items[size];
        items = temp;
        return last_val;
    }
    public static void main (String[] args) {
        AList a = new AList();
        a.addLast(1);
        a.addLast(2);
        a.addLast(3);
        System.out.println(a.size());
        System.out.println(a.get(1));
        System.out.println(a.removeLast());
        System.out.println(a.size());
        System.out.println(a.getLast());


    }
}
