package infoextraction;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.io.IOUtils;
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

/** This class demonstrates building and using a Stanford CoreNLP pipeline. */
public class StanfordCoreNlpDem {

  /** Usage: java -cp "*" infoextraction.StanfordCoreNlpDem [inputFile [outputTextFile [outputXmlFile]]] */
  public static void main(String[] args) throws IOException {
    // set up optional output files
    PrintWriter out;
    if (args.length > 1) {
      out = new PrintWriter(args[1]);
    } else {
      out = new PrintWriter(System.out);
    }
    PrintWriter xmlOut = null;
    if (args.length > 2) {
      xmlOut = new PrintWriter(args[2]);
    }

    // Create a CoreNLP pipeline. To build the default pipeline, you can just use:
    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    // Here's a more complex setup example:
    //   Properties props = new Properties();
    //   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
    //   props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    //   props.put("ner.applyNumericClassifiers", "false");
    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // Add in sentiment
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
    Annotation annotation;
    if (args.length > 0) {
      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
    } else {
      annotation = new Annotation("Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008."); }

    // run all the selected Annotators on this text
    pipeline.annotate(annotation);

    // this prints out the results of sentence analysis to file(s) in good formats
    pipeline.prettyPrint(annotation, out);
    if (xmlOut != null) {
      pipeline.xmlPrint(annotation, xmlOut);
    }

    // Access the Annotation in code
    // The toString() method on an Annotation just prints the text of the Annotation
    // But you can see what is in it with other methods like toShorterString()


    // An Annotation is a Map with Class keys for the linguistic analysis types.
    // You can get and use the various analyses individually.
    // For instance, this gets the parse tree of the first sentence in the text.
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    if (sentences != null && ! sentences.isEmpty()) {
      CoreMap sentence = sentences.get(0);

      // Access coreference. In the coreference link graph,
      // each chain stores a set of mentions that co-refer with each other,
      // along with a method for getting the most representative mention.
      // Both sentence and token offsets start at 1!
      out.println("Coreference information");
      Map<Integer, CorefChain> corefChains =
          annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
      if (corefChains == null) { return; }


//      HashMap<String,String> corefs = new HashMap<String,String>();
      HashMap<referenceRecorder,referenceRecorder> corefs = new HashMap<referenceRecorder,referenceRecorder>();


      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
          CorefChain.CorefMention repr = entry.getValue().getRepresentativeMention();
          out.println("Chain " + entry.getKey());
          out.print("Reprjf mention: ");
          out.println(repr.mentionSpan);
          out.println("--");
        referenceRecorder mapTo = new referenceRecorder(repr.mentionSpan,repr.sentNum);
        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
          referenceRecorder key = new referenceRecorder(m.mentionSpan,repr.sentNum);
          key.printValues();
          corefs.put(key,mapTo);
          // We need to subtract one since the indices count from 1 but the Lists start from 0
//          List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
          // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
//          out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
//                  ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
        }
      }
        out.println("This is the map: ");
        for (Map.Entry<referenceRecorder, referenceRecorder> entry : corefs.entrySet()){
            referenceRecorder key = entry.getKey();
            referenceRecorder value = entry.getValue();
            out.print(key.getReference());
            out.print("->");
            out.println(value.getReference());
        }

    }
    IOUtils.closeIgnoringExceptions(out);
    IOUtils.closeIgnoringExceptions(xmlOut);
  }


}
