package infoextraction; /**
 * Created by Gabriela on 14-Jun-17.
 */
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
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
    private netTemplate network = new netTemplate();
    private coreferenceResolution corefResolution;
    private int currentSent = 1;
    List <fileRecorder> fileRec;

    public NLPPipeline(List<fileRecorder> fileRec){
        this.fileRec = fileRec;
        startPipeLine();
    }

    public netTemplate getNetwork() {
        return network;
    }

    public void startPipeLine(){
//        openFiles filestream = new openFiles();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,mention, dcoref,depparse,relation,natlog,openie");
//        props.setProperty("coref.language", "en");
//        props.setProperty("coref.algorithm", "neural");
        //"statistical" : "neural"
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        fileRec = filestream.getText();
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
        corefResolution = new coreferenceResolution(document,file.getTitle(),currentSent);
        getAnnotations(sentences,file.getTitle());
        corefResolution.printCorefs();
    }

    public void getAnnotations (List<CoreMap> sentences,String filename) {
        network = new netTemplate();
        for (CoreMap sentence : sentences) {
            Sentence sent = new Sentence(sentence);
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            List<RelationMention> relMentions = sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
            List <String> entitiesList = new ArrayList<>();
            entitiesList.addAll(corefResolution.getSentenceCorefs(sent.sentenceIndex()));
            entitiesList.addAll(addEntitiesToTemplate(sentence,filename));
            List<String> newnamedEntities = corefResolution.manageNamedEntities(entitiesList,sent.sentenceIndex(),filename);
            newnamedEntities = removeDuplicates(newnamedEntities);
            if(newnamedEntities.size()>1) {
                network = createPairsWithRel(newnamedEntities, getTimeStamps(sentence), triples, network, filename, currentSent,relMentions);
            }
            currentSent++;
        }
        network.printNetWorkToFile(filename);
    }

    public void getAnnotationsFour (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            List <String> entitiesList = new ArrayList<>();
            entitiesList.addAll(addEntitiesToTemplate(sentence,""));
            for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                CoreLabel word = new CoreLabel(token);
                referenceRecorder ref = new referenceRecorder(word.word(),word.sentIndex(),"");
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
              network = createPairs(addEntitiesToTemplate(sentence,""),getTimeStamps(sentence),network);
        }
    }

    public void getAnnotationsTwo (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            network = createPairs(addEntitiesToTemplate(sentence,""),null,network);
        }
    }

    public void getAnnotationsOne (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            for (String s: getNamedEntities(sentence,"")) {
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
        }
        return alpha.getResults();
    }

//    Add to tests structures - no relations, just enttiies
    public List<String> addEntitiesToTemplate(CoreMap sentence,String fileName){
        List<String> ents = new ArrayList<>();
        for (String s: getNamedEntities(sentence,fileName)){
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
                                            int sentence,
                                            List<RelationMention> relMentions) {
        for (String s1:ents) {
            System.out.println(sentence);
            for (int i = ents.indexOf(s1) + 1; i < ents.size(); i++) {
                System.out.println(ents);
                String rels = returnRelation(s1,ents.get(i),triples,sentence,filename);
                String relsM = returnRelm(s1,ents.get(i),relMentions,sentence,filename);
                net.addConnection(s1, ents.get(i), listToString(date),filename,rels,sentence);
                if(rels.length()>0){
                    net.addConnection(s1, ents.get(i), listToString(date),filename,relsM,sentence);
                }
            }
        }
        return net;
    }

    private String returnRelm(String a,String b, List<RelationMention> triples, int sentence, String filename){
        String rels = "";
        if(triples!=null) {
            for (RelationMention m : triples) {
                if (relmContainsEntities(a, b, m, sentence, filename)) {
                    rels = m.getType();
                    System.out.println(m.getType());
                }
            }
        }
        return rels;
    }

    private boolean relmContainsEntities(String a,String b, RelationMention rel, int sentence, String filename){
        boolean founda = false, foundb = false;
        a = corefResolution.wasTransformedforRelations(a,sentence,filename);
        b = corefResolution.wasTransformedforRelations(b,sentence,filename);
        if(rel.getArgNames()!=null) {
            for (String arg : rel.getArgNames()) {
                if (arg.equals(a)) {
                    founda = true;
                }
                if (arg.equals(b)) {
                    foundb = true;
                }
            }
        }
        return founda && foundb;
    }

    private String returnRelation(String a,String b,Collection<RelationTriple> triples, int sentence, String filename){
        String rels = "";
        for (RelationTriple trip:triples){
            if (containsEntities(a, b, trip, sentence,filename)) {
                if(trip.relationLemmaGloss().length() > rels.length()){
                    rels = trip.relationLemmaGloss();
                }
            }
        }
        return rels;
    }

    private boolean containsEntities(String a,String b, RelationTriple rel, int sentence, String filename){
        a = corefResolution.wasTransformedforRelations(a,sentence,filename);
        b = corefResolution.wasTransformedforRelations(b,sentence,filename);
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

    public List<String> getNamedEntities(CoreMap sentence,String fileName){
        List<String> namedEntities = new ArrayList<>();
        Sentence s = new Sentence(sentence);
        namedEntities.addAll(s.mentions("PERSON"));
        namedEntities.addAll(s.mentions("ORGANIZATION"));
        namedEntities.addAll(s.mentions("LOCATION"));
        namedEntities.addAll(s.mentions("MISC"));

        return namedEntities;
    }

    private List<String> removeDuplicates(List<String> ents){
        List<String> newents = new ArrayList<>();
        for(String s: ents){
            if(!newents.contains(s) && s!=null){
                newents.add(s);
            }
        }
        return newents;
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



}