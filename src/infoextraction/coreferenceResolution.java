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
    private Map<referenceRecorder,String> recordChanges = new ArrayMap<>();
    private acronymManager aManage = new acronymManager();
    private Map<referenceRecorder,referenceRecorder> corefs = new ArrayMap<>();
    private Annotation annotation;
    private List<String> allEntities = new ArrayList<>();
    private List<String> allLocations = new ArrayList<>();
    private List<String> allOrganizations = new ArrayList<>();
    private List<String> allPersons = new ArrayList<>();
    private List<String> allMisc = new ArrayList<>();
    private List<String> allEntitiesAbbreviations = new ArrayList<>();
    private HashMap<String,String> transformEntity = new HashMap<>();
    private List<String> prepositions = Arrays.asList("a","the","an","of","in","with","on","at","for","to","by","and");

//    Start of the process things

//Constructor
    public coreferenceResolution(Annotation annotation,String fileName, int currentSent){
        this.annotation = annotation;
        getChains(fileName,currentSent);
    }


    //    Used to get the named entities for the coref
    private void getNamedEntities(List<CoreMap> sentences){
        for (CoreMap sentence: sentences) {
            Sentence s = new Sentence(sentence);
            allPersons.addAll(s.mentions("PERSON"));
            allEntities.addAll(allPersons);
            allMisc.addAll(s.mentions("MISC"));
            allEntities.addAll(allMisc);
            allOrganizations.addAll(s.mentions("ORGANIZATION"));
            allEntities.addAll(allOrganizations);
            allLocations.addAll(s.mentions("LOCATION"));
            allEntities.addAll(allLocations);
        }
    }

    //    Generate abbreviations for all the entities
    public void getAbbreviations(){
        for(String entity: allEntities){
            allEntitiesAbbreviations.add(aManage.splitString(entity));
        }
    }

    //    Get info at the beginning of the process
    public void getChains(String fileName,int currentSent) {
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        getNamedEntities(sentences);
        getAbbreviations();
        if (sentences != null && !sentences.isEmpty()) {
            Map<Integer, CorefChain> corefChains =
                    annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            if (corefChains == null) { return; }
            processText(corefChains,corefs,fileName,currentSent);
        }
    }



    //    Generate corefchains
    private void processText(Map<Integer, CorefChain> corefChains,
                             Map<referenceRecorder,referenceRecorder> corefs,
                             String fileName,
                             int currentSent) {
        for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
            CorefChain.CorefMention repr = entry.getValue().getRepresentativeMention();
            referenceRecorder mapTo = new referenceRecorder(repr.mentionSpan,repr.sentNum + currentSent,fileName);
            createChain(corefs,repr,mapTo,entry,fileName,currentSent);
        }
    }


    //    Create a new chain with my structure
    private void createChain(Map<referenceRecorder,referenceRecorder> corefs,CorefChain.CorefMention repr,
                             referenceRecorder mapTo,Map.Entry<Integer,CorefChain> entry,
                             String fileName,
                             int currentSent){
        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
            referenceRecorder key = new referenceRecorder(m.mentionSpan,repr.sentNum + currentSent,fileName);
            corefs.put(key,mapTo);
        }
    }


//    End start of coref methods


//    Individual stuff

    public List<String> manageNamedEntities(List<String> namedEntities,int index,String fileName){
        List <String> toDelete = new ArrayList<>();
        List <String> toAdd = new ArrayList<>();
        for (String s: namedEntities){
            referenceRecorder ref = new referenceRecorder(s,index,fileName);
            String original = checkNERAssociation(ref);
            if(allLocations.contains(s)){
                if(wasTransformed(s)!=null){
                    toDelete.add(s);
                    toAdd.add(wasTransformed(s));
                }
                else if (managePotentialAbbrev(s)!=null){
                    toDelete.add(s);
                    toAdd.add(managePotentialAbbrev(s));
                }
                else if (checkIfExists(s)!= null){
                    toDelete.add(s);
                    toAdd.add(checkIfExists(s));
                }
                else if(getEditDistanceMatch(s)!=null){
                    toDelete.add(s);
                    toAdd.add(getEditDistanceMatch(s));
                }
                else if(original!=null){
                    toDelete.add(s);
                    toAdd.add(original);
                }
            }
            else if(allOrganizations.contains(s)){
                if(wasTransformed(s)!=null){
                    toDelete.add(s);
                    toAdd.add(wasTransformed(s));
                }
                else if (managePotentialAbbrev(s)!=null){
                    toDelete.add(s);
                    toAdd.add(managePotentialAbbrev(s));
                }
                else if (checkIfExists(s)!= null){
                    toDelete.add(s);
                    toAdd.add(checkIfExists(s));
                }
                else if(getEditDistanceMatch(s)!=null){
                    toDelete.add(s);
                    toAdd.add(getEditDistanceMatch(s));
                }
                else if(original!=null){
                    toDelete.add(s);
                    toAdd.add(original);
                }
            }
            else if(allPersons.contains(s)){
                if(wasTransformed(s)!=null){
                    toDelete.add(s);
                    toAdd.add(wasTransformed(s));
                }
                if(namedBefore(s)!=null){
                    toDelete.add(s);
                    toAdd.add(namedBefore(s));
                }
                else if (checkIfExists(s)!= null){
                    toDelete.add(s);
                    toAdd.add(checkIfExists(s));
                }
                else if(getEditDistanceMatch(s)!=null){
                    toDelete.add(s);
                    toAdd.add(getEditDistanceMatch(s));
                }
                else if(original!=null){
                    toDelete.add(s);
                    toAdd.add(original);
                }
            }
             else if(allMisc.contains(s)){
                if(wasTransformed(s)!=null){
                    toDelete.add(s);
                    toAdd.add(wasTransformed(s));
                }
                else if (managePotentialAbbrev(s)!=null){
                    toDelete.add(s);
                    toAdd.add(managePotentialAbbrev(s));
                }
                else if (checkIfExists(s)!= null){
                    toDelete.add(s);
                    toAdd.add(checkIfExists(s));
                }
                else if(getEditDistanceMatch(s)!=null){
                    toDelete.add(s);
                    toAdd.add(getEditDistanceMatch(s));
                }
                else if(original!=null){
                    toDelete.add(s);
                    toAdd.add(original);
                }
            }
//
//            if(wasTransformed(s)!=null){
//                toDelete.add(s);
//                toAdd.add(wasTransformed(s));
//            }
//            else if (managePotentialAbbrev(s)!=null){
//                toDelete.add(s);
//                toAdd.add(managePotentialAbbrev(s));
//            }
//            else if (checkIfExists(s)!= null){
//                toDelete.add(s);
//                toAdd.add(checkIfExists(s));
//            }
//            else if(getEditDistanceMatch(s)!=null){
//                toDelete.add(s);
//                toAdd.add(getEditDistanceMatch(s));
//            }
//            else if(original!=null){
//                toDelete.add(s);
//                toAdd.add(original);
//            }
        }
        namedEntities.removeAll(toDelete);
        namedEntities.addAll(toAdd);
        return namedEntities;
    }



// Did we find a coref for this NER earlier?
    public String wasTransformedforRelations(String s, int sentence, String fileName){
        referenceRecorder ref = new referenceRecorder(s,sentence,fileName);
        String val = checkNERAssociation(ref);
        if (transformEntity.get(s)!= null) {
            return transformEntity.get(s);
        }
        else if (val!=null){
            return val;
        }
        else return s;
    }

    private String namedBefore(String ent){
        if(!ent.contains(" ")) {
            for (int i = 0; i<allEntities.indexOf(ent); i++) {
                String[] words = aManage.getArray(allEntities.get(i));
                if(words!=null) {
                    if (words.length > 1 && allEntities.get(i).contains(ent)) {
                        transformEntity.put(ent, allEntities.get(i));
                        return allEntities.get(i);
                    }
                }
            }
        }
        return null;
    }

    public String wasTransformed(String s){
        if (transformEntity.get(s)!= null) return transformEntity.get(s);
        else return null;
    }


    public String managePotentialAbbrev(String s){
        if(isAbbrev(s)!= null) return isAbbrev(s);
        if(abbrevWithNoPrep(s)!= null) return abbrevWithNoPrep(s);
        return null;
    }

//IS the word an acronym?
    public String isAbbrev(String s){
        if(s.toUpperCase().equals(s) && !s.contains(" ")) {
            String abbrev = getRidOfPeriods(s);
//            System.out.println(abbrev);
            if (allEntitiesAbbreviations.contains(abbrev)) {
                transformEntity.put(s,allEntities.get(allEntitiesAbbreviations.indexOf(abbrev)));
                return allEntities.get(allEntitiesAbbreviations.indexOf(abbrev));
            }
        }
        return null;
    }

//    Delte . for Abbreviations
    private String getRidOfPeriods(String s){
        if(s.contains(".")) {
            return aManage.splitOnPeriod(s);
        }
        return s;
    }

    //    Does this match a previous entity if we remove the
//    prepositions from the words and abbreviate them?
    public String abbrevWithNoPrep(String s){
        for(int i = 0; i<= allEntities.indexOf(s);i++) {
            String m = aManage.splitString(removeStopWords(allEntities.get(i)));
            if (m != null) {
                if (m.equals(s)) {
                    transformEntity.put(s,allEntities.get(i));
                    return allEntities.get(i);
                }
            }
        }
        return null;
    }

    //    Remove common prepositions
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


    //    Is this word part of another String in the NERs?
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


//    Does this reference to a Named Entity/ For openIE
    public String checkNERAssociation(referenceRecorder key){
        referenceRecorder val = checkMap(key);
        if(val!=null) {
            if (allEntities.contains(val.getReference())) {
//                transformEntity.put(key.getReference(), val.getReference());
                return val.getReference();
            }
        }
        return null;

    }

//    Search the map for this reference
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


//    For common noun coref resolution:
//    if part of a chain is in a sentence and
//    it redirects to a Named Entity add it to the list
//    and return it

    public List<String> getSentenceCorefs(int index){
        List<String> references = new ArrayList<>();
        for(Map.Entry<referenceRecorder, referenceRecorder> entry : corefs.entrySet()){
            if(entry.getKey().getSentence() == index){
                if(allEntities.contains(entry.getValue().getReference())){
                    references.add(entry.getValue().getReference());
                }
            }
        }

        return references;
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
        String bestMatch = null;
        String toCompare = createString(aManage.getArray(s));
        for (int i = 0; i<allEntities.indexOf(s);i++){
            String[] entArray = aManage.getArray(allEntities.get(i));
//            System.out.println(allEntities.get(i));
            String entity = createString(entArray);
            int editD = aManage.computeLevenshteinDistance(toCompare,entity);
//            System.out.println((double)editD);
//            System.out.println((double)getMinString(toCompare,entity)/5);
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

    public Map<referenceRecorder,referenceRecorder> getCorefs(){ return corefs;}

    public void putReference(referenceRecorder a, referenceRecorder b){
        corefs.put(a,b);
    }

    public void setCorefs(HashMap<referenceRecorder, referenceRecorder> corefs) {
        this.corefs = corefs;
    }

    public void setAllEntities(List<String> allEntities) {
        this.allEntities = allEntities;
    }

    public void printCorefs(){
        for (Map.Entry<referenceRecorder, referenceRecorder> entry : corefs.entrySet()){
            referenceRecorder key = entry.getKey();
            referenceRecorder value = entry.getValue();
            System.out.print(key.getReference());
            System.out.print(key.getSentence());
            System.out.print("->");
            System.out.println(value.getReference());
        }
    }


}
