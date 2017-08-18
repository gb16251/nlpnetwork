package infoextraction;

/**
 * Created by Gabriela on 06-Jul-17.
 */
public class referenceRecorder {
    private String reference = "";
    private String docName = "";
    private int sentence = 0;


    public referenceRecorder(String ref, int sent,String docName){
        this.reference =ref;
        this.sentence = sent;
        this.docName = docName;
    }

    public String getReference() {return reference;}
    public int getSentence(){ return sentence;}

    public String getDocName() {
        return docName;
    }
}
