package infoextraction;

/**
 * Created by Gabriela on 21-Jul-17.
 */
public class conTemplate {
    private String node1 = "";
    private String node2 = "";
    private String date = "";
    public conTemplate(String node1,String node2,String date){
        this.node1 = node1;
        this.node2 = node2;
        this.date = date;
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
}
