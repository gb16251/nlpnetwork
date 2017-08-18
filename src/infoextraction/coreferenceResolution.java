package infoextraction;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.ArrayMap;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Gabriela on 07-Jul-17.
 */
public class coreferenceResolution {
    private PrintWriter out = new PrintWriter(System.out);
    private Map<referenceRecorder,referenceRecorder> corefs = new ArrayMap<>();
    private Annotation annotation;
    private List<String> allEntities = new ArrayList<>();
    private List<String> allEntitiesAbbreviations = new ArrayList<>();
    private HashMap<String,String> transformEntity = new HashMap<>();


    public String wasTransformed(String s){
        if (transformEntity.get(s)!= null) return transformEntity.get(s);
        else return s;
    }

    public String isAbbrev(String s){
        if(s.toUpperCase().equals(s) && !s.contains(" ")) {
            if (allEntitiesAbbreviations.contains(s)) {
                transformEntity.put(s,allEntities.get(allEntitiesAbbreviations.indexOf(s)));
                return allEntities.get(allEntitiesAbbreviations.indexOf(s));
            }
        }
        return null;
    }

//TODO: do more than
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


    public coreferenceResolution(Annotation annotation,String fileName){
        this.annotation = annotation;
//        getChains(fileName);
    }

    public String checkNERAssociation(referenceRecorder key){
        referenceRecorder val = checkMap(key);
        try {
            if (allEntities.contains(val.getReference())) {
                transformEntity.put(key.getReference(), val.getReference());
                return val.getReference();
            }
        }
        catch (NullPointerException e){System.err.println("Reference must have a name.");}
        return null;
    }

    private referenceRecorder checkMap(referenceRecorder ref){
        for (Map.Entry<referenceRecorder, referenceRecorder> entry : corefs.entrySet()) {
            referenceRecorder key = entry.getKey();
            if(key.getReference().equals(ref.getReference()) &&
                    key.getSentence() == ref.getSentence() &&
            key.getDocName().equals(ref.getDocName())){
                return entry.getValue();
            }
        }
        return null;
    }

//    private getMatchingReference(re)




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
        acronymManager aManage = new acronymManager();
        for(String entity: allEntities){
            allEntitiesAbbreviations.add(aManage.splitString(entity));
        }
    }
    public void getChains(String fileName) {
//        Annotation annotation = annotateSentence(s);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        getNamedEntities(sentences);
        getAbbreviations();
        if (sentences != null && !sentences.isEmpty()) {
            Map<Integer, CorefChain> corefChains =
                    annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            if (corefChains == null) { return; }
            processText(corefChains,corefs,fileName);
        }
    }

    private void processText(Map<Integer, CorefChain> corefChains,
                             Map<referenceRecorder,referenceRecorder> corefs,
                             String fileName) {
        for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
            CorefChain.CorefMention repr = entry.getValue().getRepresentativeMention();
//            out.println("Chain " + entry.getKey());
//            out.print("Reprjf mention: ");
//            out.println(repr.mentionSpan);
//            out.println("--");
            referenceRecorder mapTo = new referenceRecorder(repr.mentionSpan,repr.sentNum,fileName);
            createChain(corefs,repr,mapTo,entry,fileName);
        }
    }


    private void createChain(Map<referenceRecorder,referenceRecorder> corefs,CorefChain.CorefMention repr,
                             referenceRecorder mapTo,Map.Entry<Integer,CorefChain> entry,
                             String fileName){
        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
            referenceRecorder key = new referenceRecorder(m.mentionSpan,repr.sentNum,fileName);
            corefs.put(key,mapTo);
        }
    }
    public Map<referenceRecorder,referenceRecorder> getCorefs(){ return corefs;}


    public void printCorefs(){
        for (Map.Entry<referenceRecorder, referenceRecorder> entry : corefs.entrySet()){
            referenceRecorder key = entry.getKey();
            referenceRecorder value = entry.getValue();
            System.out.print(key.getReference());
            System.out.print("->");
            System.out.println(value.getReference());
        }
    }


//    utils


    public void putReference(referenceRecorder a, referenceRecorder b){
        corefs.put(a,b);
    }


    public void setCorefs(HashMap<referenceRecorder, referenceRecorder> corefs) {
        this.corefs = corefs;
    }

    public void setAllEntities(List<String> allEntities) {
        this.allEntities = allEntities;
    }

    public static void main(String[] args) throws IOException {
    }
}
