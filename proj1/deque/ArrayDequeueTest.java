package deque;

import org.junit.Test;

import java.sql.Array;

import static org.junit.Assert.*;

public class ArrayDequeueTest {
    @Test
    public void ArrayListBasicTests() {
        ArrayDeque a = new ArrayDeque();
        assertEquals(a.isEmpty(), true);
        a.addFirst("hello");
        a.addLast(42);
        assertEquals(a.isEmpty(), false);
        assertEquals(a.size(), 2);
        a.printDeque();
        assertEquals(a.get(0), "hello");
        a.removeFirst();
        assertEquals(a.get(0), 42);
        assertEquals(a.get(1), null);
        a.addFirst("hello");
        a.removeLast();
        assertEquals(a.get(0), "hello");
        assertEquals(a.get(1), null);
        ArrayDeque b = new ArrayDeque();
        b.addLast("hello");
        b.printDeque();
        assertEquals(a.equals(b), true);
        ArrayDeque c = new ArrayDeque();
        c.addFirst(42);
        assertEquals(a.equals(c), false);

    }
}
