package infoextraction; /**
 * Created by Gabriela on 19-Jun-17.
 */
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.PrintStream;
import java.util.*;

import static edu.stanford.nlp.trees.Tree.valueOf;

/**
 * A demo illustrating how to call the OpenIE system programmatically.
 */
public class openiedemo {
    PrintStream ps = new PrintStream(System.out);


    public static void main(String[] args) throws Exception {
        openiedemo demo = new openiedemo();
        openFiles filestream = new openFiles("");
        // Create the Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,depparse,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        List <fileRecorder> fileRec = filestream.getText();

//         Annotate an example document.
        for (fileRecorder file : fileRec) {
            demo.processText(file,pipeline);
        }

        // Loop over sentences in the document
//        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
//            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//            tree.pennPrint(demo.ps);
//
//            ss.getSubordinates(tree);

//            tree.getSimplifiedSentences(tree);
//            tree.pennPrint(demo.ps);
//            demo.ps.print(tree.toString());
        // Get the OpenIE triples for the sentence
//            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
//            for (RelationTriple triple : triples) {
//                System.out.println(triple.confidence + "\t" +
//                        triple.subjectLemmaGloss() + "\t" +
//                        triple.relationLemmaGloss() + "\t" +
//                        triple.objectLemmaGloss());
//            }
//        }

//        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
//            // Get the OpenIE triples for the sentence
//            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
//            // Print the triples
//            for (RelationTriple triple : triples) {
//                System.out.println(triple.confidence + "\t" +
//                        triple.subjectLemmaGloss() + "\t" +
//                        triple.relationLemmaGloss() + "\t" +
//                        triple.objectLemmaGloss());
//            }
//        }
    }
    public void processText(fileRecorder file, StanfordCoreNLP pipeline){

        Annotation document = new Annotation(file.getFileOutput());
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        sentenceSimplifier ss = new sentenceSimplifier();
                for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
                    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//                    Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            // Print the triples
//            for (RelationTriple triple : triples) {
//                System.out.println(triple.confidence + "\t" +
//                        triple.subjectLemmaGloss() + "\t" +
//                        triple.relationLemmaGloss() + "\t" +
//                        triple.objectLemmaGloss());
//            }

                    tree.pennPrint();
                    ss.getSubordinatesRecursive(tree);


                }



    }
}

