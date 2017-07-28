package infoextraction;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gabriela on 12-Jul-17.
 */
public class dateChecker {
    List<String> months = Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");

    private boolean isMonth(String s) {
        if (months.contains(s.toLowerCase())) {
            return true;
        }
        return false;
    }

    public List<String> checkDates(String s) {
        String words[] = checkIfNeedsToSplit(s);
        List<String> dates = new ArrayList<>();
        if (words == null) {
            if (isYear(s) || isMonth(s)) {
                dates.add(s);
            }
        } else {
            if (processString(words) != null) {
                 dates.addAll(processString(words));
            }
        }
        return dates;
    }

    private List<String> processString(String words[]) {
        List<String> validDates = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if(!words[i].equals("")) {
                if (isMonth(words[i])) {
                    validDates.add(words[i]);
                } else if (isYear(words[i])) {
                    validDates.add(words[i]);
                }
            }
        }
        return validDates;
    }

    private String[] checkIfNeedsToSplit(String s) {
        String words[] = s.split("\\s+");
        if (words.length == 1) {
            return null;
        }
        return words;
    }

    //
//    The program will throw away any years that are more than 3 years
//    greater than the current date - OK for discovering future plans but
//            discovering too many things that have not actually happened yet
//            can result in too much noise.
    private boolean isYear(String s) {
        int year = Year.now().getValue() + 3;
        if (isDigits(s)) {
            int potentialYear = Integer.parseInt(s);
            if (potentialYear <= year && potentialYear > 1300) {
                return true;
            }
        }
        return false;
    }

    private boolean isDigits(String s) {
        for (int i = 0; i <= s.length() - 1; i++) {
            if (s.charAt(i) > '9' || s.charAt(i) < '0') {
                return false;
            }
        }
        return true;
    }
}
