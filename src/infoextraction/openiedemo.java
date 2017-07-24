package infoextraction; /**
 * Created by Gabriela on 19-Jun-17.
 */
import edu.stanford.nlp.ling.CoreAnnotations;
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

        // Create the Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,depparse,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

//         Annotate an example document.
        Annotation doc = new Annotation("The Serious Fraud Office which brought the Barclays prosecutions told Panorama that evidence of lowballing was provided to the defence.\n" +
                "Libor is the rate at which banks lend to each other, setting a benchmark for mortgages and loans for ordinary customers.\n" +
                "The recording calls into question evidence given in 2012 to the Treasury select committee by former Barclays boss Bob Diamond and Paul Tucker, the man who went on to become the deputy governor of the Bank of England.\n");
//        Annotation doc = new Annotation("The Serious Fraud Office which brought the Barclays prosecutions told Panorama");
        pipeline.annotate(doc);
        sentenceSimplifier ss = new sentenceSimplifier();

        // Loop over sentences in the document
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            tree.pennPrint(demo.ps);

            ss.getSubordinates(tree);

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
        }
    }

}

