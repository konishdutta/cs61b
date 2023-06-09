package tester;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.lang.reflect.Method;
import java.util.HashMap;
public class TestArrayDequeEC<Item> {
    @Test
    public void TestOne() {
        /*
        addFirst 0
        addLast 1
        removeFirst 2
        removeLast 3
        printDeque 4
        isEmpty 5
        size 6
        get 7
         */

        ArrayDequeSolution<Integer> a = new ArrayDequeSolution();
        StudentArrayDeque<Integer> b = new StudentArrayDeque();


        assertEquals("isEmpty()", a.isEmpty(), b.isEmpty());
        assertEquals("size()", a.size(), b.size());


        for (int j = 0; j < 10000; j += 1) {
            Integer func = StdRandom.uniform(8);
            if (func == 0) {
                Integer randVal = StdRandom.uniform(1000);
                a.addFirst(randVal);
                b.addFirst(randVal);
                assertEquals("addFirst(" + randVal + ")\n", a.get(0), b.get(0));
                assertEquals("size()\n", a.size(), b.size());
                for (int i = 0; i < a.size(); i += 1) {
                    assertEquals("get(" + i + ")\n", a.get(i), b.get(i));
                }
            } else if (func == 1) {
                Integer randVal = StdRandom.uniform(1000);
                a.addLast(randVal);
                b.addLast(randVal);
                assertEquals("addLast(" + randVal + ")\n", a.get(a.size() - 1), b.get(b.size() - 1));
                assertEquals("size()\n", a.size(), b.size());
                for (int i = 0; i < a.size(); i += 1) {
                    assertEquals("get(" + i + ")\n", a.get(i), b.get(i));
                }
            } else if (func == 2 && a.size() > 0 && b.size() > 0) {
                Integer x = a.removeFirst();
                Integer y = b.removeFirst();
                assertEquals("removeFirst()\n", x, y);
                assertEquals("size()\n", a.size(), b.size());
                if (a.size() > 0) {
                    //assertEquals("removeFirst()\n", a.get(0), b.get(0));
                    for (int i = 0; i < a.size(); i += 1) {
                        assertEquals("get(" + i + ")\n", a.get(i), b.get(i));
                    }
                }
            } else if (func == 3 && a.size() > 0 && b.size() > 0) {
                Integer x = a.removeLast();
                Integer y = b.removeLast();
                assertEquals("removeLast()\n", x, y);
                assertEquals("size()\n", a.size(), b.size());
                if (a.size() > 0) {
                    //assertEquals("removeLast()\n", a.get(a.size() - 1), b.get(b.size() - 1));
                    for (int i = 0; i < a.size(); i += 1) {
                        assertEquals("get(" + i + ")\n", a.get(i), b.get(i));
                    }
                }
            } else if (func == 5) {
                assertEquals("isEmpty()\n", a.isEmpty(), b.isEmpty());
            } else if (func == 6) {
                assertEquals("size()\n", a.size(), b.size());
            } else if (func == 7 && a.size() > 0 && b.size() > 0) {
                Integer randVal = StdRandom.uniform(a.size());
                assertEquals("get(" + randVal + ")\n", a.get(randVal), b.get(randVal));
                assertEquals("size()\n", a.size(), b.size());
                for (int i = 0; i < a.size(); i += 1) {
                    assertEquals("get(" + i + ")\n", a.get(i), b.get(i));
                }
            }

        }
    }
}