package infoextraction;

/**
 * Created by Gabriela on 21-Jul-17.
 */
public class conTemplate {
    private String node1 = "";
    private String node2 = "";
    private String date = "";
    private String rel = "";
    private int sentence;
    private String filename = "";


    public conTemplate(String node1,String node2,String date,String filename,String rel,int sentence){
        this.node1 = node1;
        this.node2 = node2;
        this.date = date;
        this.filename = filename;
        this.rel = rel;
        this.sentence = sentence;
    }

    public int getSentence() {
        return sentence;
    }

    public String getDate() {
        return date;
    }

    public String getNode1() {
        return node1;
    }

    public String getNode2() {
        return node2;
    }

    public String getFilename() {
        return filename;
    }

    public String getRel() {
        return rel;
    }

    public boolean hasNode(String s){
        if (node1.equals(s) || node2.equals(s))return true;
        return false;
    }
    public void printTemplate(){
        System.out.print(node1);
        System.out.print(" ");

        System.out.print(node2);
        System.out.print(" ");

        System.out.println(date);


    }
}
