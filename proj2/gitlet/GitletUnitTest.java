package gitlet;
import edu.princeton.cs.algs4.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class GitletUnitTest {
    @Test
    public void mergeSortTest() {
        ArrayList<String> testList1 = new ArrayList<String>();
        ArrayList<String> testList2 = new ArrayList<String>();
        Integer numTries = StdRandom.uniform(1000);
        for (int i = 0; i < numTries; i += 1) {
            String randVal = Integer.toString(StdRandom.uniform(1000));
            testList1.add(randVal);
            testList2.add(randVal);
        }
        KonishAlgos.mergeSort(testList1);
        Collections.sort(testList2);
        assertEquals(testList2, testList1);
    }
    @Test
    public void binSearchTest() {
        ArrayList<String> testList1 = new ArrayList<String>();
        Integer numTries = StdRandom.uniform(1000);
        String randVal = "";
        for (int i = 0; i < numTries; i += 1) {
            randVal = Integer.toString(StdRandom.uniform(1000));
            testList1.add(randVal);
        }
        KonishAlgos.mergeSort(testList1);
        assertTrue(KonishAlgos.binSearch(randVal, testList1));
        assertFalse(KonishAlgos.binSearch("hello", testList1));
    }

    @Test
    public void binSearchSmallTest() {
        ArrayList<String> testList = new ArrayList<String>();
        testList.add("hello.txt");
        testList.add("hello2.txt");
        KonishAlgos.mergeSort(testList);
        assertTrue(KonishAlgos.binSearch("hello.txt", testList));
    }
}
