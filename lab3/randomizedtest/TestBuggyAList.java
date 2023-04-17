package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  @Test
    public void testThreeAddThreeRemove() {
      AListNoResizing a = new AListNoResizing();
      BuggyAList b = new BuggyAList();

      for (int i = 0; i < 3; i += 1) {
          a.addLast(i);
          b.addLast(i);
      }
      assertEquals(a.size(), b.size());
      assertEquals(a.removeLast(), b.removeLast());
      assertEquals(a.removeLast(), b.removeLast());
      assertEquals(a.removeLast(), b.removeLast());

  }
  @Test
    public void randomizedTest() {
      AListNoResizing<Integer> L = new AListNoResizing<>();
      BuggyAList<Integer> B = new BuggyAList<>();

      int N = 5000;
      for (int i = 0; i < N; i += 1) {
          int operationNumber = StdRandom.uniform(0, 4);
          if (operationNumber == 0) {
              // addLast
              int randVal = StdRandom.uniform(0, 100);
              L.addLast(randVal);
              B.addLast(randVal);
              assertEquals(L.getLast(), B.getLast());
              assertEquals(L.size(), B.size());
          }
          else if (operationNumber == 1) {
              // size
              assertEquals(L.size(), B.size());

          }
          else if (operationNumber == 2 && L.size() > 0) {
              // getLast
              assertEquals(L.getLast(), B.getLast());

          }
          else if (operationNumber == 3 && L.size() > 0) {
              // removeLast
              assertEquals(L.removeLast(), B.removeLast());
              assertEquals(L.size(), B.size());

          }
      }
  }
}
