/**
 * Created by Gabriela on 06-Jul-17.
 */
public class referenceRecorder {
    private String reference = "";
    private int startIndex;
    private int endIndex;
    private int sentence;


    public void setReference(String ref){ reference +=ref;}
    public void setIndexes (int start, int end, int sent){
        startIndex = start;
        endIndex = end;
        sentence = sent;
    }
    public String getReference() {return reference;}
    public int getStartIndex() {return startIndex;}
    public int getEndIndex(){ return endIndex;}
    public int getSentence(){ return endIndex;}



    public void printValues(){
        System.out.print("Name: ");
        System.out.println(reference);

        System.out.print("Start index: ");
        System.out.println(startIndex);

        System.out.print("End Index: ");
        System.out.println(endIndex);


    }

}
