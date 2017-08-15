package infoextraction;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.PrintStream;
import java.util.ArrayList;
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
    List <String> newsents = new ArrayList<>();


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
        List<String> sentences = new ArrayList<>();
        if (tree.depth() <2 ) return;
        String sentence = tree.toString();
        int index = sentence.indexOf("S");
        int closeIndex = findClosingParen(sentence.toCharArray(), index+2 );
        String s = "";
        s = s+ (sentence.substring(0,index-1) + sentence.substring(closeIndex+1,sentence.length()));
        Tree t2 = valueOf(s);
        t2.pennPrint();
//        ps.println(getCleanTree(t2));
    }

    public void getSubordinatesRecursive(Tree tree){
        if(tree == null) return;
        if(tree.depth()< 3) return;

        String sentence = tree.toString();
        int index = sentence.indexOf("S") + 2;
        if(index<=0){
            index = sentence.indexOf("SBAR") + 4;
        }
        if (index<=0) return;
        int closeIndex = findClosingParen(sentence.toCharArray(), index );
        String s1 = "";
        String s2 = "";
        s1 = (sentence.substring(0,index-1) + sentence.substring(closeIndex+1,sentence.length()));
        s2 = (sentence.substring(index,closeIndex+1));
        System.out.println(s1);
        System.out.println(s2);
        getSubordinatesRecursive(valueOf(s1));
//        getSubordinatesRecursive(valueOf(addRoot(s2)));
    }

    public String addRoot(String s){
        if(!s.contains("ROOT")){
            return "(ROOT  (" + s;
        }
        return s;
    }


    public void getSimplifiedSentences(Tree tree){
        getSubordinates(tree);

    }
}
