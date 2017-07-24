package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 28-Jun-17.
 */
public class oldNetworkTemplate {
    private String networkName = "";
    private List<oldEntityNode> nodes = new ArrayList<>();
    private int nodesCounter = 0;

    public String getNetworkName() {return networkName;}
    public void setNetworkName(String s) {
        if (networkName.length() == 0) {
            networkName += s;
        }
    else {
        System.err.print("Network already has a name.");
        }
    }
    public List<oldEntityNode> getNodes() { return nodes;}

    public void addNode(oldEntityNode n){
        nodes.add(n);
        nodesCounter++;
    }

    public oldEntityNode getNodeByName(String s){
        int index;
        if ((index = searchNodeByName(s))!= -1){
            return nodes.get(index);
        }
        return null;
    }

    private int searchNodeByName( String s){
        for (oldEntityNode n : nodes){
            if (s.equals(n.getNodeName())){
                return nodes.indexOf(n);
            }
        }
        return -1;
    }

    public void printNetworkCons(){
        for (oldEntityNode node: nodes){
            node.printConnections();
            System.out.println();
        }
    }

}
