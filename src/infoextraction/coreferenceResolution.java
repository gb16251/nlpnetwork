package infoextraction;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Gabriela on 07-Jul-17.
 */
public class coreferenceResolution {
    PrintWriter out = new PrintWriter(System.out);
    HashMap<referenceRecorder,referenceRecorder> corefs = new HashMap<referenceRecorder,referenceRecorder>();
    private Properties startCorefPipeline(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        return props;
    }

    private Annotation annotateSentence(String s){
        StanfordCoreNLP pipeline = new StanfordCoreNLP(startCorefPipeline());
        Annotation annotation = new Annotation(s);
        pipeline.annotate(annotation);
        return annotation;
    }


    public void getChains(String s ) {
        Annotation annotation = annotateSentence(s);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        if (sentences != null && !sentences.isEmpty()) {
            Map<Integer, CorefChain> corefChains =
                    annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            if (corefChains == null) { return; }
            processText(corefChains,corefs);
        }
    }

    private void processText(Map<Integer, CorefChain> corefChains, HashMap<referenceRecorder,referenceRecorder> corefs) {
        for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
            CorefChain.CorefMention repr = entry.getValue().getRepresentativeMention();
//            out.println("Chain " + entry.getKey());
//            out.print("Reprjf mention: ");
//            out.println(repr.mentionSpan);
//            out.println("--");
            referenceRecorder mapTo = new referenceRecorder();
            mapTo.setIndexes(repr.startIndex, repr.endIndex, repr.sentNum);
            mapTo.setReference(repr.mentionSpan);
            createChain(corefs,repr,mapTo,entry);
        }
    }


    private void createChain(HashMap<referenceRecorder,referenceRecorder> corefs,CorefChain.CorefMention repr,
                             referenceRecorder mapTo,Map.Entry<Integer,CorefChain> entry){
        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
            referenceRecorder key = new referenceRecorder();
            key.setIndexes(m.startIndex, m.endIndex,repr.sentNum); key.setReference(m.mentionSpan);
            key.printValues();
            corefs.put(key,mapTo);
        }
    }
    public HashMap<referenceRecorder,referenceRecorder> getCorefs(){ return corefs;}

    public static void main(String[] args) throws IOException {
        coreferenceResolution cr = new coreferenceResolution();
        cr.getChains("Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.");
        cr.out.println("This is the map: ");
        for (Map.Entry<referenceRecorder, referenceRecorder> entry : cr.corefs.entrySet()){
            referenceRecorder key = entry.getKey();
            referenceRecorder value = entry.getValue();
            cr.out.print(key.getReference());
            cr.out.print("->");
            cr.out.println(value.getReference());
        }
    }
    }
