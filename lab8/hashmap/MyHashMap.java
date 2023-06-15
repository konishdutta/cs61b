package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private HashSet<K> keys;
    private int size;
    private double load;
    private int mapSize;

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }
    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        load = maxLoad;
        size = 0;
        mapSize = initialSize;
        keys = new HashSet<K>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    public void put(K key) {
        put(key, null);
    }
    public V get(K key) {
        int tableNum = tableNum(key);
        Collection<Node> b = buckets[tableNum];
        if (b != null) {
            Iterator<Node> itr = b.iterator();
            while (itr.hasNext()) {
                Node elem = itr.next();
                if (elem.key.equals(key)) {
                    return elem.value;
                }
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        if(get(key) != null) {
            return true;
        }
        return false;
    }
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }
    public void clear() {
        buckets = createTable(16);
        size = 0;
        mapSize = 16;
    }

    public int size() {
        return size;
    }
    public void put(K key, V value) {
        int tableNum = tableNum(key);
        if (buckets[tableNum] == null) {
            buckets[tableNum] = createBucket();
        }

        Iterator<Node> itr = buckets[tableNum].iterator();

        while (itr.hasNext()) {
            Node elem = itr.next();
            if (elem.key.equals(key)) {
                elem.value = value;
                return;
            }
        }

        buckets[tableNum].add(createNode(key, value));
        size += 1;
        keys.add(key);
    }

    public Set<K> keySet() {
        return keys;
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }

    private void resize() {

    }

    private int tableNum(K key) {
        int hashcode = key.hashCode();
        int mod = Math.floorMod(hashcode, mapSize);
        return mod;
    }
}
