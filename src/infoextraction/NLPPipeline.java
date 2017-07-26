package infoextraction; /**
 * Created by Gabriela on 14-Jun-17.
 */
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;
import evaluationVisualtizer.charts;
import javafx.application.Application;


import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class NLPPipeline {
    private PrintStream ps = new PrintStream(System.out);
    private graphDbPipeline database = new graphDbPipeline();
    private netTemplate network = new netTemplate();




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
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
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
              network = createPairs(addEntitiesToTemplate(sentence),getTimeStamps(sentence),network);
        }
    }

    public int[] evalNetwork(){
        testOneNetwork one = new testOneNetwork();
        netTemplate ideal = one.getnT();

        variableOptimizer vO = new variableOptimizer();
        averageCalculator alpha = new averageCalculator();
        averageCalculator beta = new averageCalculator();
        averageCalculator gamma = new averageCalculator();

        for (variableTriples var:vO.vt){
            networkComparator nC = new networkComparator(var.getA(),var.getB(),var.getC(),ideal,network);
            alpha.addItem(var.getA(),(int)nC.calculatePositiveScore());
            beta.addItem(var.getB(),(int)nC.calculatePositiveScore());
            gamma.addItem(var.getC(),(int)nC.calculatePositiveScore());
            System.out.println();
        }
        return beta.getResults();
    }

    public void insertToDatabase(netTemplate network ){
        for (conTemplate con: network.getConnections()){
            database.addBasicConnection(con.getNode1(),con.getNode2(),con.getDate());
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

    private netTemplate createPairs (List<String> ents, List<String> date,netTemplate net) {
        for (String s1:ents){
            for (int i = s1.indexOf(s1) + 1; i< ents.size();i++){
                net.addConnection(s1,ents.get(i),listToString(date));
            }
        }
        return net;
    }


    public String listToString(List<String> dates){
        String date = "";
        for (String s: dates) {
            date += s;
            date += " ";
        }
        return date;
    }

    public List<String> getNamedEntities(CoreMap sentence){
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

}