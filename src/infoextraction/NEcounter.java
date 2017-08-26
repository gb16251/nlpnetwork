package infoextraction;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Gabriela on 26-Aug-17.
 */
public class NEcounter {
    List <String> namedEntities = new ArrayList<>();

    public static void main(String[] args) {
        NEcounter pipe = new NEcounter();
        pipe.startPipeLine();
    }


    public void startPipeLine(){
        openFiles filestream = new openFiles();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,mention");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        List<fileRecorder> fileRec = filestream.getText();
        for (fileRecorder file : fileRec){
            processText(file,pipeline);
            System.out.print(file.getTitle());
            System.out.print(": ");
            for (String s: namedEntities){
                System.out.print(s);
                System.out.print(" ");
            }
            System.out.println(" ");
            namedEntities.clear();
        }

    }
    public void processText(fileRecorder file, StanfordCoreNLP pipeline){

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(file.getFileOutput());
        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        getAnnotations(sentences);
    }
    public void getAnnotations (List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            Sentence sent = new Sentence(sentence);
            namedEntities.addAll(sent.mentions("PERSON"));
            namedEntities.addAll(sent.mentions("ORGANIZATION"));
            namedEntities.addAll(sent.mentions("LOCATION"));
        }
    }

}
