package infoextraction; /**
 * Created by Gabriela on 14-Jun-17.
 */
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;
import evaluationVisualtizer.charts;
import javafx.application.Application;


import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

//Java Heap Space: -Xms1024m -Xmx4024m
public class NLPPipeline {
    private PrintStream ps = new PrintStream(System.out);
    private graphDbPipeline database = new graphDbPipeline();
    private netTemplate network = new netTemplate();
    private coreferenceResolution corefResolution;


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
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,mention, coref,depparse,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        List<String> texts = new ArrayList<>();
        List <fileRecorder> fileRec = filestream.getText();
        //        List<String> texts = filestream.getText();

        for (fileRecorder file : fileRec){
            processText(file,pipeline);
        }

    }
    public void processText(fileRecorder file, StanfordCoreNLP pipeline){

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(file.getFileOutput());
        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        corefResolution = new coreferenceResolution(document);
        getAnnotations(sentences,file.getTitle());
        insertToDatabase(network);
    }

    public void getAnnotations (List<CoreMap> sentences,String filename) {
        for (CoreMap sentence : sentences) {
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
//            for (RelationTriple triple : triples) {
//                System.out.println(triple.confidence + "\t" +
//                        triple.subjectLemmaGloss() + "\t" +
//                        triple.relationLemmaGloss() + "\t" +
//                        triple.objectLemmaGloss());
//            }
            List <String> entitiesList = new ArrayList<>();
            entitiesList.addAll(addEntitiesToTemplate(sentence));
            for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                CoreLabel word = new CoreLabel(token);
                referenceRecorder ref = new referenceRecorder(word.word(),word.sentIndex());
                String poss = corefResolution.checkNERAssociation(ref);
                if(poss!=null){
                    entitiesList.add(poss);
                }
            }
            network = createPairsWithRel(entitiesList,getTimeStamps(sentence),triples, network, filename, new Sentence(sentence).sentenceIndex());
        }
//        network.printNetwork();
    }

    public void getAnnotationsFour (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            List <String> entitiesList = new ArrayList<>();
            entitiesList.addAll(addEntitiesToTemplate(sentence));
            for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                CoreLabel word = new CoreLabel(token);
                referenceRecorder ref = new referenceRecorder(word.word(),word.sentIndex());
                String poss = corefResolution.checkNERAssociation(ref);
                if(poss!=null){
                    entitiesList.add(poss);
                }
            }
            network = createPairs(entitiesList,getTimeStamps(sentence),network);
        }
//        network.printNetwork();
    }


    public void getAnnotationsThree (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
              network = createPairs(addEntitiesToTemplate(sentence),getTimeStamps(sentence),network);
        }
    }

    public void getAnnotationsTwo (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            network = createPairs(addEntitiesToTemplate(sentence),null,network);
        }
    }

    public void getAnnotationsOne (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            for (String s: getNamedEntities(sentence)) {
                network.addNode(s);
            }
        }
    }

    public int[] evalNetwork(){
        testOneNetwork one = new testOneNetwork();
        netTemplate ideal = one.getnT();
        calculatePreRec F1 = new calculatePreRec(ideal,network);
        System.out.print("F1 score: ");
        System.out.println(F1.getF1());

        variableOptimizer vO = new variableOptimizer();
        averageCalculator alpha = new averageCalculator();
        averageCalculator beta = new averageCalculator();
        averageCalculator gamma = new averageCalculator();

        for (variableTriples var:vO.getVt()){
            networkComparator nC = new networkComparator(var.getA(),var.getB(),var.getC(),ideal,network);
            alpha.addItem(var.getA(),(int)nC.calculatePositiveScore());
            beta.addItem(var.getB(),(int)nC.calculatePositiveScore());
            gamma.addItem(var.getC(),(int)nC.calculatePositiveScore());
//            System.out.print(var.getA());System.out.print(" ");
//            System.out.print(var.getB());System.out.print(" ");
//            System.out.print(var.getC());System.out.println(" ");
//
//            nC.printAllScores();

        }
        return alpha.getResults();
    }

    public void insertToDatabase(netTemplate network ){
        for (conTemplate con: network.getConnections()){
            database.addAdvancedConnection(con.getNode1(),con.getNode2(),con.getDate(),con.getFilename(),con.getRel(),con.getSentence());
        }

    }
//    Add to tests structures - no relations, just enttiies
    public List<String> addEntitiesToTemplate(CoreMap sentence){
        List<String> ents = new ArrayList<>();
        for (String s: getNamedEntities(sentence)){
            ents.add(s);
        }
        return ents;
    }

    private netTemplate createPairs (List<String> ents, List<String> date,netTemplate net) {
        for (String s1:ents) {
//            System.out.println(s1);
            for (int i = ents.indexOf(s1) + 1; i < ents.size(); i++) {
//                ps.print(s1);ps.print(" ");ps.print(ents.get(i));
//                ps.println(i);
                net.addConnection(s1, ents.get(i), listToString(date),"","",0);
            }
        }
        return net;
    }
    private netTemplate createPairsWithRel (List<String> ents,
                                            List<String> date,
                                            Collection<RelationTriple> triples,
                                            netTemplate net,
                                            String filename,
                                            int sentence) {
        for (String s1:ents) {
            for (int i = ents.indexOf(s1) + 1; i < ents.size(); i++) {
                String rels = returnRelation(s1,ents.get(i),triples);
                net.addConnection(s1, ents.get(i), listToString(date),filename,rels,sentence);
            }
        }
        return net;
    }


    private String returnRelation(String a,String b,Collection<RelationTriple> triples){
        String rels = "";
        for (RelationTriple trip:triples){
            if (containsEntities(a, b, trip)) {
                if(trip.relationLemmaGloss().length() > rels.length()){
                    rels = trip.relationLemmaGloss();
                }
            }
        }
        return rels;
    }

    private boolean containsEntities(String a,String b, RelationTriple rel){
        a = corefResolution.wasTransformed(a);
        b = corefResolution.wasTransformed(b);
        if(rel.objectLemmaGloss().equals(a) && rel.subjectLemmaGloss().equals(b)) return true;
        if(rel.objectLemmaGloss().equals(b) && rel.subjectLemmaGloss().equals(a)) return true;
        return false;
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
        List<String> namedEntities = new ArrayList<>();
        Sentence s = new Sentence(sentence);
        namedEntities.addAll(s.mentions("PERSON"));
        namedEntities.addAll(s.mentions("ORGANIZATION"));
        namedEntities.addAll(s.mentions("LOCATION"));
        namedEntities = manageNamedEntities(namedEntities,s.sentenceIndex());
        return namedEntities;
    }

    private List<String> manageNamedEntities(List<String> namedEntities,int index){
        List <String> toDelete = new ArrayList<>();
        List <String> toAdd = new ArrayList<>();
        for (String s: namedEntities){
            referenceRecorder ref = new referenceRecorder(s,index);
            String original = corefResolution.checkNERAssociation(ref);
            if(original!=null){
                toDelete.add(s);
                toAdd.add(original);
            }
            else if (corefResolution.isAbbrev(s)!=null){
                toDelete.add(s);
                toAdd.add(corefResolution.isAbbrev(s));
            }
            else if (corefResolution.checkIfExists(s)!= null){
                toDelete.add(s);
                toAdd.add(corefResolution.checkIfExists(s));
            }

        }
        namedEntities.removeAll(toDelete);
        namedEntities.addAll(toAdd);
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