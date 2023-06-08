package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode node;
    private int size;
    public BSTMap() {
        this.node = null;
        this.size = 0;

    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        this.node = null;
        this.size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return containsKey(this.node, key);
    }

    private boolean containsKey(BSTNode n, K k) {
        if (n == null) {
            return false;
        } else if (n.key.equals(k)) {
            return true;
        } else if (n.key.compareTo(k) > 0) {
            return containsKey(n.left, k);
        } else {
            return containsKey(n.right, k);
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return get(node, key);
    }

    private V get(BSTNode n, K k) {
        if (n == null) {
            return null;
        } else if (n.key.equals(k)) {
            return n.value;
        } else if (n.key.compareTo(k) > 0) {
            return get(n.left, k);
        } else {
            return get(n.right, k);
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        this.node = put(node, key, value);
    }

    private BSTNode put(BSTNode n, K k, V v) {
        if (n == null) {
            size += 1;
            return new BSTNode(k, v);
        } else if (n.key.compareTo(k) > 0) {
            n.left = put(n.left, k, v);
        } else if (n.key.compareTo(k) < 0) {
            n.right = put(n.right, k, v);
        }
        return n;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        return createSet(node, new TreeSet<K>());
    }

    private TreeSet<K> createSet(BSTNode n, TreeSet<K> t) {
        if (n == null) {
            return t;
        }

        TreeSet res = createSet(n.left, t);
        res.add(n.key);
        return createSet(n.right, t);
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        V res = this.get(key);
        node = remove(node, key);
        return res;
    }

    private BSTNode remove(BSTNode n, K k) {
        if (n == null) {
            return null;
        } else if (n.key.compareTo(k) > 0) {
            n.left = remove(n.left, k);
        } else if(n.key.compareTo(k) < 0) {
            n.right = remove(n.right, k);
        } else {
            BSTNode res = n;
            size -= 1;
            if (n.left == null) {
                return n.right;
            } else if (n.right == null) {
                return n.left;
            } else {
                BSTNode successor = findSuccessor(n.right);
                n.key = successor.key;
                n.value = successor.value;
                n.right = remove(n.right, successor.key);
            }
        }
        return n;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printInOrder(node);
    }

    private void printInOrder(BSTNode n) {
        if (n == null) {
            return;
        } else if (n.left != null) {
            printInOrder(n.left);
        }
        System.out.println(n.key);
        printInOrder(n.right);
    }

    private BSTNode findSuccessor(BSTNode n) {
        if (n == null) {
            return null;
        } else if (n.left == null) {
            return n;
        }
        return findSuccessor(n.left);
    }


    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private class BSTIterator implements Iterator<K> {
        private BSTNode curr = node;
        @Override
        public boolean hasNext() {
            return curr == null;
        }

        @Override
        public K next() {
            Stack<BSTNode> stack = new Stack();
            while (curr != null || !stack.isEmpty()) {
                if (curr != null) {
                    stack.push(curr);
                    curr = curr.left;
                } else {
                    curr = stack.pop();
                    K res = curr.key;
                    curr = curr.right;
                    return res;
                }
            }
            throw new NoSuchElementException();
        }
    }

    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }

    }

}
