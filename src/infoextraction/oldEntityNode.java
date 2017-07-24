package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 28-Jun-17.
 */
public class oldEntityNode {
    private String nodeName = "";
    private List<nodeConnection> connections = new ArrayList<>();
    private String entityType = "";
    private int connectionNumber = 0;

    public oldEntityNode createNode(String s, List<nodeConnection> cons){
        nodeName+= s;
        return this;
    }
    public int getConnectionNumber(){return connectionNumber;}

    public String getNodeName(){ return nodeName;}

    public List<nodeConnection> getConnections() {
        return connections;
    }

    public void setNodeName(String nodeName) {
//        if(nodeName.equals("")) {
            this.nodeName = nodeName;
//        }
//        else {
//            System.err.print("Node already has a name.");
//        }
    }
    public void addConnection(nodeConnection connect){
        connections.add(connect);
    }

    public void setConnections(List<nodeConnection> connections) {
        this.connections = connections;
    }
    public boolean searchConnectionByConnectingEntity(String s){
        for (nodeConnection con: connections){
            if(con.getConnectingNodeName().equals(s)){
                return true;
            }
        }
        return false;
    }
    public void printConnections(){
        for (nodeConnection con : connections){
            System.out.print(nodeName);
            System.out.print(": ");
            System.out.print(con.getConnectingNodeName());
            if(con.hasDate()) {
                System.out.print("(");
                System.out.print(con.getDate());
                System.out.print(")");
            }
            System.out.println("");
        }
    }
}
