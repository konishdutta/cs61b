package gitlet;
import edu.princeton.cs.algs4.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class GitletUnitTest {
    @Test
    public void mergeSortTest() {
        ArrayList<String> TestList1 = new ArrayList<String>();
        ArrayList<String> TestList2 = new ArrayList<String>();
        Integer numTries = StdRandom.uniform(1000);
        for (int i = 0; i < numTries; i += 1) {
            String randVal = Integer.toString(StdRandom.uniform(1000));
            TestList1.add(randVal);
            TestList2.add(randVal);
        }
        KonishAlgos.mergeSort(TestList1);
        Collections.sort(TestList2);
        assertEquals(TestList2, TestList1);
    }
    @Test
    public void binSearchTest() {
        ArrayList<String> TestList1 = new ArrayList<String>();
        Integer numTries = StdRandom.uniform(1000);
        String randVal = "";
        for (int i = 0; i < numTries; i += 1) {
            randVal = Integer.toString(StdRandom.uniform(1000));
            TestList1.add(randVal);
        }
        KonishAlgos.mergeSort(TestList1);
        assertTrue(KonishAlgos.binSearch(randVal, TestList1));
        assertFalse(KonishAlgos.binSearch("hello", TestList1));
    }

    @Test
    public void binSearchSmallTest() {
        ArrayList<String> TestList = new ArrayList<String>();
        TestList.add("hello.txt");
        TestList.add("hello2.txt");
        KonishAlgos.mergeSort(TestList);
        assertTrue(KonishAlgos.binSearch("hello.txt", TestList));
    }
}
