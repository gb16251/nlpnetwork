package infoextraction;

import edu.stanford.nlp.trees.Tree;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static edu.stanford.nlp.trees.Tree.valueOf;

/**
 * Created by Gabriela on 11-Jul-17.
 */
public class sentenceSimplifier {
    PrintStream ps = new PrintStream(System.out);
    simplifiedSentences extra = new simplifiedSentences();


    public Tree pruneTree (Tree tree){
        String sentence = tree.toString();
        int index = sentence.indexOf("WHNP");
//        ps.println(index);
        int closeIndex = findClosingParen(sentence.toCharArray(), index-1 );
        String s = "";
        s = s+ (sentence.substring(0,index-1) + sentence.substring(closeIndex+1,sentence.length()));
//        ps.println(s);
//        TODO: This throws exception or smth
        Tree t2 = valueOf(s);
        return t2;
    }
    private int findClosingParen(char[] text, int openPos) {
        int closePos = openPos;
        int counter = 1;
        while (counter > 0) {
            char c = text[++closePos];
            if (c == '(') {
                counter++;
            }
            else if (c == ')') {
                counter--;
            }
        }
        return closePos;
    }
    public String getCleanTree(Tree tree) {
        String s = "";
        List<String> NPTags = Arrays.asList("#","ROOT","NNP", "NP-TMP", "PDT", "MD", "RP", "PRT", "NP", "VB", "VBZ", "WDT", "WHPP", "TO", "ADJP", "CD", "VBP", "WHNP", "WP", "NN", "VP", "RB", "JJ", "NNS", "PP", "PRP", "VBG", "VBD", "DT", "PRP$", "NNP", "NP", "CC", "VBN", "POS", "IN", ",", ".", "EX", "S", "WRB", "WHADVP", "SBAR", "ADVP");
        Iterator<Tree> iter = tree.iterator();
        while (iter.hasNext()) {
            String word = iter.next().nodeString();
            if (!NPTags.contains(word)) {
                s += word;
                s += " ";
            }
        }
        return s;
    }
    public void getSubordinates(Tree tree){
        if (tree.depth() <2 ) return;
        String sentence = tree.toString();
        int index = sentence.indexOf("S");
        int closeIndex = findClosingParen(sentence.toCharArray(), index+2 );
        ps.println(sentence.length());
        String s = "";
        s = s+ (sentence.substring(0,index-1) + sentence.substring(closeIndex+1,sentence.length()));
        ps.println(s);
        Tree t2 = valueOf(s);
//        getSubordinates(t2);
//        getSubordinates(valueOf(s2));

    }
    public void getSimplifiedSentences(Tree tree){
        getSubordinates(tree);

    }
}
