package infoextraction;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Gabriela on 07-Jul-17.
 */
public class coreferenceResolution {
    private PrintWriter out = new PrintWriter(System.out);
    private HashMap<referenceRecorder,referenceRecorder> corefs = new HashMap<>();
    private Annotation annotation;
    private List<String> allEntities = new ArrayList<>();
    private List<String> allEntitiesAbbreviations = new ArrayList<>();
    private HashMap<String,String> transformEntity = new HashMap<>();


    public String wasTransformed(String s){
        if (transformEntity.get(s)!= null) return transformEntity.get(s);
        else return s;
    }


    public String isAbbrev(String s){
        if(s.toUpperCase().equals(s)) {
            if (allEntitiesAbbreviations.contains(s)) {
                transformEntity.put(s,allEntities.get(allEntitiesAbbreviations.indexOf(s)));
                return allEntities.get(allEntitiesAbbreviations.indexOf(s));
            }
        }
        return null;
    }


    public String checkIfExists(String s){
        if (allEntities.contains(s)) {
            for (String entity: allEntities){
                if (entity.contains(s)){
                    transformEntity.put(s,entity);
                    return entity;
                }
            }
        }
        return null;
    }
    public coreferenceResolution(Annotation annotation){
        this.annotation = annotation;
        getChains();
    }

    public String checkNERAssociation(referenceRecorder key){
        if(corefs.containsKey(key)){
            allEntities.contains(corefs.get(key).getReference());
            System.out.println(key.getStartIndex());
            System.out.println(key.getEndIndex());
            transformEntity.put(key.getReference(),corefs.get(key).getReference());
            return corefs.get(key).getReference();
        }
        return null;
    }


    private void getNamedEntities(List<CoreMap> sentences){
        for (CoreMap sentence: sentences) {
            Sentence s = new Sentence(sentence);
            allEntities.addAll(s.mentions("PERSON"));
            allEntities.addAll(s.mentions("ORGANIZATION"));
            allEntities.addAll(s.mentions("LOCATION"));
        }
    }

    public Properties startCorefPipeline(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
        return props;
    }

    private Annotation annotateSentence(String s){
        StanfordCoreNLP pipeline = new StanfordCoreNLP(startCorefPipeline());
        Annotation annotation = new Annotation(s);
        pipeline.annotate(annotation);
        return annotation;
    }

    private void getAbbreviations(){
        abbreviationManager aManage = new abbreviationManager();
        for(String entity: allEntities){
            allEntitiesAbbreviations.add(aManage.splitString(entity));
        }
    }
    public void getChains() {
//        Annotation annotation = annotateSentence(s);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        getNamedEntities(sentences);
        getAbbreviations();
        if (sentences != null && !sentences.isEmpty()) {
            Map<Integer, CorefChain> corefChains =
                    annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            if (corefChains == null) { return; }
            processText(corefChains,corefs);
        }
    }

    private void processText(Map<Integer, CorefChain> corefChains,
                             HashMap<referenceRecorder,referenceRecorder> corefs) {
        for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
            CorefChain.CorefMention repr = entry.getValue().getRepresentativeMention();
//            out.println("Chain " + entry.getKey());
//            out.print("Reprjf mention: ");
//            out.println(repr.mentionSpan);
//            out.println("--");
            referenceRecorder mapTo = new referenceRecorder(repr.mentionSpan,repr.sentNum);
            createChain(corefs,repr,mapTo,entry);
        }
    }


    private void createChain(HashMap<referenceRecorder,referenceRecorder> corefs,CorefChain.CorefMention repr,
                             referenceRecorder mapTo,Map.Entry<Integer,CorefChain> entry){
        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
            referenceRecorder key = new referenceRecorder(m.mentionSpan,repr.sentNum);
            corefs.put(key,mapTo);
        }
    }
    public HashMap<referenceRecorder,referenceRecorder> getCorefs(){ return corefs;}

    public void smth(){
        System.out.println("This is the map: ");
        for (Map.Entry<referenceRecorder, referenceRecorder> entry : getCorefs().entrySet()){
            referenceRecorder key = entry.getKey();
            referenceRecorder value = entry.getValue();
            System.out.print(key.getReference());
            System.out.print("->");
            System.out.println(value.getReference());
        }
    }
    public void printCorefs(){
        for (Map.Entry<referenceRecorder, referenceRecorder> entry : corefs.entrySet()){
            referenceRecorder key = entry.getKey();
            referenceRecorder value = entry.getValue();
            System.out.print(key.getReference());
            System.out.print("->");
            System.out.println(value.getReference());
        }
    }

    public static void main(String[] args) throws IOException {
    }
}
