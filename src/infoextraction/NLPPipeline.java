/**
 * Created by Gabriela on 14-Jun-17.
 */
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.CoreMap;


import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class NLPPipeline {
    PrintStream ps = new PrintStream(System.out);
    graphDbPipeline database = new graphDbPipeline();
    private void startDB(){
        try {
            database.initializeGraphDB();
        }
        catch (IOException e ){}
    }

    public static void main(String[] args) {
        NLPPipeline pipe = new NLPPipeline();
        pipe.startDB();
        pipe.startPipeLine();

    }

    public void startPipeLine(){
        openFiles filestream = new openFiles();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma, depparse,natlog");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        List<String> texts = filestream.getText();
        for (String text : texts){
            processText(text,pipeline);
        }

    }
    public void processText(String text, StanfordCoreNLP pipeline){

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        getAnnotations(sentences);
    }



    public void getAnnotations (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            Sentence sent = new Sentence(sentence);
//            ps.print(sent);
//            addToDataBase(sentence);
              createPairs(addEntitiesToTemplate(sentence),getTimeStamps(sentence));
        }
    }
//    Add to test structures - no relations, just enttiies
    public List<String> addEntitiesToTemplate(CoreMap sentence){
        List<String> ents = new ArrayList<>();
        for (String s: getNamedEntities(sentence)){
            ents.add(s);
        }
        return ents;
    }

//    Create pairs from entities list
    public void createPairs (List<String> ents, List<String> date){
        networkTemplate net = new networkTemplate();
        for (String s1: ents){
            entityNode Node = new entityNode();
            Node.setNodeName(s1);
            for (String s2 : ents){
                if(!s2.equals(s1)) {
                    nodeConnection connect = new nodeConnection();
                    connect.setConnectingNodeName(s2);
                    connect.setDate(date);
                    Node.addConnection(connect);
                    database.addBasicConnection(s1,s2,listToString(date));
                }
            }
            net.addNode(Node);
        }
        net.printNetworkCons();
    }

    public String listToString(List<String> dates){
        String date = "";
        for (String s: dates) {
            date += s;
            date += " ";
        }
        return date;
    }
    public void addToDataBase(CoreMap sentence){
        for (String s: getNamedEntities(sentence)){
            database.addNode(s);
        }
    }
    public List<String> getNamedEntities(CoreMap sentence ){
        List<String> namedEntities;
        Sentence s = new Sentence(sentence);
        namedEntities = s.mentions("PERSON");
        namedEntities.addAll(s.mentions("ORGANIZATION"));
        namedEntities.addAll(s.mentions("LOCATION"));
        return namedEntities;
    }
    public List<String> getTimeStamps(CoreMap sentence ) {
        List<String> timeStamps;
        Sentence s = new Sentence(sentence);
        timeStamps = s.mentions("DATE");
        timeStamps = checkValidDate(timeStamps);
        return timeStamps;
    }

    private List<String> checkValidDate(List<String> e){
        List<String> dates = new ArrayList<>();
        dateChecker dc = new dateChecker();
        for (String s : e){
            dates.addAll(dc.checkDates(s));
        }
        return dates;
    }

        public void getNPs(List<CoreMap> sentences,StanfordCoreNLP pipeline){

        for (CoreMap sentence : sentences) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            List<String> NPTags = Arrays.asList("#","NP-TMP","PDT","MD","RP","PRT","NP","VB","VBZ","WDT","WHPP","TO","ADJP","CD","VBP","WHNP","WP", "NN", "VP","RB","JJ","NNS","PP","PRP","VBG","VBD","DT","PRP$","NNP","NP","CC","VBN","POS","IN",",",".","EX","S","WRB","WHADVP","SBAR","ADVP");
            for (Tree subtree: tree)
            {
                if(subtree.label().value().equals("VP"))
                {
                    for (Tree NP: subtree) {
                        if (!NPTags.contains(NP.label().value())) {
                            ps.print(NP.nodeString());
                            ps.print(" ");
                        }
                    }
                    ps.println();
                }
            }
        }
    }
    public void getCleanTree(Tree tree) {
        List<String> NPTags = Arrays.asList("#", "NP-TMP", "PDT", "MD", "RP", "PRT", "NP", "VB", "VBZ", "WDT", "WHPP", "TO", "ADJP", "CD", "VBP", "WHNP", "WP", "NN", "VP", "RB", "JJ", "NNS", "PP", "PRP", "VBG", "VBD", "DT", "PRP$", "NNP", "NP", "CC", "VBN", "POS", "IN", ",", ".", "EX", "S", "WRB", "WHADVP", "SBAR", "ADVP");
        for (Tree subtree : tree) {
            String s = "";
            if (!NPTags.contains(subtree.label().value())) {
                s += subtree.nodeString();
                s += " ";
            }
            ps.print(s);
        }
    }

    public int findClosingParen(char[] text, int openPos) {
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
    public String prunetree (Tree tree){
        String sentence = tree.toString();
        int index = sentence.indexOf("WHNP");
        ps.print(index);
        return sentence;
    }

}