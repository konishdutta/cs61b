package gitlet;
import java.util.*;

public class KonishAlgos {
    public static Boolean binSearch(String o, List<String> a) {
        int p2 = a.size() - 1;
        int p1 = 0;

        while (p1 <= p2) {
            int mid = (p1 + p2) / 2;
            if (a.get(mid).equals(o)) {
                return true;
            } else if (a.get(mid).compareTo(o) >= 0) {
                p2 = mid - 1;
            } else {
                p1 = mid + 1;
            }
        }
        return false;
    }


    public static void mergeSort(List<String> a) {
        if (a.size() == 1) {
            return;
        }
        int mid = a.size() / 2;
        List<String> sub1 = new ArrayList<String>();
        sub1.addAll(a.subList(0, mid));
        List<String> sub2 = new ArrayList<String>();
        sub2.addAll(a.subList(mid, a.size()));
        mergeSort(sub1);
        mergeSort(sub2);
        int i = 0;
        int j = 0;
        while (i < sub1.size() && j < sub2.size()) {
            if (sub1.get(i).compareTo(sub2.get(j)) <= 0) {
                a.set(i + j, sub1.get(i));
                i += 1;
            } else {
                a.set(i + j, sub2.get(j));
                j += 1;
            }
        }
        while (i < sub1.size()) {
            a.set(i + j, sub1.get(i));
            i += 1;
        }
        while (j < sub2.size()) {
            a.set(i + j, sub2.get(j));
            j += 1;
        }
    }

    public static boolean patternMatch(String abbrev, String full) {
        Map<Character, Integer> table = buildTable(abbrev);
        int pPos = 0;
        while (pPos + abbrev.length() <= full.length()) {
            int charPos = abbrev.length() - 1;
            while (charPos >= 0 && full.charAt(charPos + pPos) == abbrev.charAt(charPos)) {
                charPos -= 1;
            }
            if (charPos == -1) {
                return true;
            } else {
                int shift = table.getOrDefault(full.charAt(charPos + pPos), -1);
                if (shift < charPos) {
                    pPos = pPos + (charPos - shift);
                } else {
                    pPos += 1;
                }
            }
        }
        return false;
    }

    public static Map<Character, Integer> buildTable(String abbrev) {
        Map<Character, Integer> res = new HashMap<>();
        for (int i = 0; i < abbrev.length(); i++) {
            res.put(abbrev.charAt(i), i);
        }
        return res;
    }


}
