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
    private acronymManager aManage = new acronymManager();
    private Map<referenceRecorder,referenceRecorder> corefs = new ArrayMap<>();
    private Annotation annotation;
    private List<String> allEntities = new ArrayList<>();
    private List<String> allEntitiesAbbreviations = new ArrayList<>();
    private HashMap<String,String> transformEntity = new HashMap<>();
    private List<String> prepositions = Arrays.asList("a","the","an","of","in","with","on","at","for","to","by","and");


    public coreferenceResolution(Annotation annotation,String fileName){
        this.annotation = annotation;
        getChains(fileName);
    }

    public String wasTransformed(String s){
        if (transformEntity.get(s)!= null) return transformEntity.get(s);
        else return s;
    }


    public String isAbbrev(String s){
        if(s.toUpperCase().equals(s) && !s.contains(" ")) {
            String abbrev = getRidOfPeriods(s);
            System.out.println(abbrev);
            if (allEntitiesAbbreviations.contains(abbrev)) {
                transformEntity.put(s,allEntities.get(allEntitiesAbbreviations.indexOf(abbrev)));
                return allEntities.get(allEntitiesAbbreviations.indexOf(abbrev));
            }
        }
        return null;
    }

    private String getRidOfPeriods(String s){
        if(s.contains(".")) {
            return aManage.splitOnPeriod(s);
        }
        return s;
    }


    public String checkIfExists(String s){
        if (allEntities.contains(s)) {
            for (int i = 0; i< allEntities.indexOf(s);i++){
                if (allEntities.get(i).contains(s)){
                    transformEntity.put(s,allEntities.get(i));
                    return allEntities.get(i);
                }
            }
        }
        return null;
    }




    public String checkNERAssociation(referenceRecorder key){
        referenceRecorder val = checkMap(key);
        if(val!=null) {
            if (allEntities.contains(val.getReference())) {
                transformEntity.put(key.getReference(), val.getReference());
                return val.getReference();
            }
        }
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


    public String abbrevWithNoPrep(String s){
        for(int i = 0; i<= allEntities.indexOf(s);i++) {
            String m = aManage.splitString(removeStopWords(allEntities.get(i)));
            if (m != null) {
                if (m.equals(s)) {
                    return allEntities.get(i);
                }
            }
        }
        return s;
    }

    public String removeStopWords(String word){
        for (String s: prepositions){
            if (word.contains(" " + s + " ")) {
                word = word.replace(" " + s + " ", " ");
            }
            else if (word.contains(s + " ")) {
                word = word.replace(s + " ", "");
            }
            else if (word.contains(" " + s)) {
                word = word.replace(" " + s, "");
            }
        }
        return word;
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

    public String getBestMatch(String s){
        List<String> possibleMatches = new ArrayList<>();
        String[] words = aManage.getArray(s);
        int len = words.length;
        for (int i = 0; i< allEntities.indexOf(s); i++){
            String[] entity = aManage.getArray(allEntities.get(i));
            int entlen = entity.length;
            if (len<entlen){
                int matchingTokens = countMatchingTokens(words,allEntities.get(i));
                if(len == matchingTokens ){
                    possibleMatches.add((allEntities.get(i)));
                }
            }
        }
        return s;
    }


    public String getEditDistanceMatch(String s){
        int minEditD = 1000;
        String bestMatch = "";
        String toCompare = createString(aManage.getArray(s));

        for (int i = 0; i<allEntities.indexOf(s);i++){
            String[] entArray = aManage.getArray(allEntities.get(i));
            System.out.println(allEntities.get(i));
            String entity = createString(entArray);
            int editD = aManage.computeLevenshteinDistance(toCompare,entity);
            System.out.println((double)editD);
            System.out.println((double)getMinString(toCompare,entity)/5);
            if((double)editD < (double)getMinString(toCompare,entity)/5 ){

                if(editD<minEditD){
                    minEditD = editD;
                    bestMatch = allEntities.get(i);
                }
            }
        }
        return bestMatch;
    }

    private int getMinString(String a, String b){
        if(a.length() > b.length()) return b.length();
        return a.length();
    }

    private String createString(String[] words){
        String result = "";
        if(words!=null) {
            for (String s : words) {
                if(s.length()!= 0) {
                    result += s;
                }
            }
        }
        return result;
    }

    private int countMatchingTokens(String[] words, String s){
        int i = 0;
        for (String match: words){
            if (s.contains(match)){
                i++;
            }
        }
        return i;
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
