package infoextraction;

/**
 * Created by Gabriela on 07-Jul-17.
 */
public class acronymManager {

    public String splitString(String s){
        if (s.contains(" ") && !s.contains(".")) return splitOnSpace(s);
        if(s.contains(".") && !s.contains(" ")) return splitOnPeriod(s);
        return null;
    }
    //    Obtains the abbreviation of a string of words
    public String splitOnSpace(String s) {
        String[] words = s.split(" ");
        String abbrev = "";
        for (String word : words) {
            abbrev += word.charAt(0);
        }
        return abbrev;
    }

    //    Removes the dots from an abbreviation
//    Returns null if any of the resulting words is more than one character
    public String splitOnPeriod(String s) {
        String[] words = s.split("\\.");
        String abbrev = "";
        for (String letter : words) {
            if (letter.length() != 1) {
                return null;
            }
            abbrev += letter.charAt(0);
        }
        return abbrev;
    }


    public boolean isPossibleAbbrev(String a, String b){
        a = a.toLowerCase();
        b = b.toLowerCase();
//        System.out.println(0.34 * max(a,b));

        if (a.length() < 2 || b.length() < 2) return  false;
        else if(a.length() <=5 && b.length() <=5){
            if((double)computeLevenshteinDistance(a,b) <= 0.34 * max(a,b)){
                return true;
            }
        }
        else {
            if((double)computeLevenshteinDistance(a,b) <= 0.25 * max(a,b)){
                return true;
            }
        }
        return false;
    }

//    Calculates the edit distance between two strings to determine their similarity
    //    Source :https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
    public int computeLevenshteinDistance(String lhs, String rhs) {

        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }

    private int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    private int max(String a, String b){
        if (a.length() > b.length()) return a.length();
        else return b.length();
    }

}
