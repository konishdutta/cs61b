package deque;
import java.util.Comparator;
import org.junit.Test;
import static org.junit.Assert.*;

class Dog implements Comparable<Dog> {

    private static class NameComparator implements Comparator<Dog> {
        public int compare(Dog a, Dog b) {
            return a.name.compareTo(b.name);
        }
    }

    private static class LenComparator implements Comparator<Dog> {
        public int compare(Dog a, Dog b) {
            int val1 = a.name.length();
            int val2 = b.name.length();
            return val1 - val2;
        }
    }

    private static class SizeComparator implements Comparator<Dog> {
        public int compare(Dog a, Dog b) {
            return a.compareTo(b);
        }
    }
    public static Comparator<Dog> getNameComparator() {
        return new NameComparator();
    }

    public static Comparator<Dog> getSizeComparator() {
        return new SizeComparator();
    }

    public static Comparator<Dog> getLenComparator() {
        return new LenComparator();
    }

    public int size;
    public String name;

    public Dog(String n, int s) {
        name = n;
        size = s;
    }

    @Override
    public int compareTo(Dog otherDog) {
        return size - otherDog.size;
    }
}


public class MaxArrayDequeTest {
    @Test
    public void intComparatorTest() {
        MaxArrayDeque<Dog> dogArray = new MaxArrayDeque(Dog.getNameComparator());
        dogArray.addFirst(new Dog("Kira", 50));
        dogArray.addFirst(new Dog("Harrison", 80));
        dogArray.addFirst(new Dog("Autumn", 10));
        Dog maxDog = dogArray.max();
        assertEquals("Kira", maxDog.name);
        Dog maxDog2 = dogArray.max(Dog.getSizeComparator());
        assertEquals("Harrison", maxDog2.name);
        Dog maxDog3 = dogArray.max(Dog.getLenComparator());
        assertEquals("Harrison", maxDog3.name);
    }
}
