package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 21-Jul-17.
 */
public class netTemplate {
    private List<String> nodes = new ArrayList<>();
    private List<connex> cons = new ArrayList<>();

    public void addConnection(String first, String second, String date){
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
            templates.add(new conTemplate(nodes.get(con.getFirst()),nodes.get(con.getSecond()),con.getDate()));
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
