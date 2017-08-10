package graphDisplay;

/**
 * Created by Gabriela on 09-Aug-17.
 */
public class edgeContainer {
    private String node1 = "";
    private String node2 = "";
    private int matches;

    public edgeContainer(String node1,String node2,int matches){
        this.node1 = node1;
        this.node2 = node2;
        this.matches = matches;
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
