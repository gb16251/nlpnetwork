package graphDisplay;

/**
 * Created by Gabriela on 09-Aug-17.
 */
public class edgeContainer {
    private String node1 = "";
    private String node2 = "";
    private String document = "";
    private String date = "";
    private String relationship = "";
    private int matches;

    public void setAdditionalInfo(String date, String relationship){
        this.relationship = relationship;
        this.date = date;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getDate() {
        return date;
    }

    public edgeContainer(String node1, String node2, String document, int matches){
        this.node1 = node1;
        this.node2 = node2;
        this.matches = matches;
        this.document = document;
    }

    public String getDocument() {
        return document;
    }

    public String getNode1() {
        return node1;
    }

    public String getNode2() {
        return node2;
    }

    public int getMatches() {
        return matches;
    }
}
