import java.util.List;

/**
 * Created by Gabriela on 28-Jun-17.
 */
public class nodeConnection {
    private String edgeName = "";
    private String connectingNodeName = "";
    private String date = "";

    public String getEdgeName(){ return edgeName;}
    public String getConnectingNodeName() {return connectingNodeName;}
    public String getDate(){ return  date;}

    public void setEdgeName(String s) {edgeName += s;}
    public void setConnectingNodeName (String s ) {connectingNodeName += s;}

    public void setDate(List<String> dates){
        for (String s: dates) {
            date += s;
            date += " ";
        }
    }
    public boolean hasDate(){
        if(date.equals("")) {return false;}
        return true;
    }

}
