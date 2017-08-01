package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 21-Jul-17.
 */
public class netTemplate {
    private List<String> nodes = new ArrayList<>();
    private List<connex> cons = new ArrayList<>();

    public List<String> getNodes() {
        return nodes;
    }
    public void addNode(String s){
        nodes.add(s);
    }

    public boolean hasNode(String s ){
        if(nodes.contains(s)) return true;
        return false;
    }

    public List<conTemplate> getNodeConnections(String s){
        List<connex> connections = new ArrayList<connex>();
        if (!nodes.contains(s)){
            return null;
        }
        else{
            int index = nodes.indexOf(s);
            for (connex con: cons){
                if (con.hasNode(index)){
                    connections.add(con);
                }
            }
        }
        List<conTemplate> templateList = getNodeConnections(connections);
        return templateList;
    }
    public List<conTemplate> getNodeConnections(List<connex> connections){
        List<conTemplate> templates = new ArrayList<conTemplate>();
        for (connex con: connections){
            conTemplate cT = new conTemplate(
                    nodes.get(con.getFirst()),
                    nodes.get(con.getSecond()),
                    con.getDate());
            templates.add(cT);
        }
        return templates;
    }

    public void addConnection(String first, String second, String date){
        if (first.equals("") ||  second.equals("")){
            return;
        }
        if(!first.equals(second)) {
            addIndices(manageNode(first), manageNode(second), date);
        }

    }

    private int manageNode(String s){
        if (!nodes.contains(s)){
            nodes.add(s);
        }
        return nodes.indexOf(s);
    }

    private void addIndices(int first, int second, String date){
//        if(!connectionExists(first,second,date)) {
            connex newcon = new connex();
            newcon.setConnection(first, second, date);
            cons.add(newcon);
//        }
    }

    private boolean connectionExists(int first, int second, String date){
        if (!cons.isEmpty()) {
            for (connex con : cons) {
                if (con.matchesLink(first, second, date)) return true;
            }
        }
        return false;
    }

    public List<conTemplate> getConnections(){
        List<conTemplate> templates = new ArrayList<>();
        for (connex con: cons){
            templates.add(new conTemplate(
                    nodes.get(con.getFirst()),
                    nodes.get(con.getSecond()),
                    con.getDate()));
        }
        return templates;
    }

    public void printNetwork(){
        for (connex con: cons){
            System.out.print(nodes.get(con.getFirst()));
            System.out.print(" ");
            System.out.print(nodes.get(con.getSecond()));
            System.out.print(" ");
            System.out.println(con.getDate());

        }
    }
}
