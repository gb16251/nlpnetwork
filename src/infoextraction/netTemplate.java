package infoextraction;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
                    con.getDate(),
                    con.getFilename(),
                    con.getRel(),
                    con.getSentence());
            templates.add(cT);
        }
        return templates;
    }

    public void addConnection(String first, String second, String date,String filename,String rel,int sentence){
        if (first.equals("") ||  second.equals("")){
            return;
        }
        if(!first.equals(second)) {
            addIndices(manageNode(first), manageNode(second), date,filename,rel,sentence);
        }

    }

    private int manageNode(String s){
        if (!nodes.contains(s)){
            nodes.add(s);
        }
        return nodes.indexOf(s);
    }

    private void addIndices(int first, int second, String date,String filename, String rel,int sentence){
//        if(!connectionExists(first,second,date)) {
            connex newcon = new connex();
            newcon.setConnection(first, second, date,filename,rel,sentence);
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
                    con.getDate(),
                    con.getFilename(),
                    con.getRel(),
                    con.getSentence()));
        }
        return templates;
    }

    public void printNetwork(){
        for (connex con: cons){
            System.out.print("[");
            System.out.print(nodes.get(con.getFirst()));
            System.out.print(",");
            System.out.print(nodes.get(con.getSecond()));
            System.out.print(" ");
            System.out.print(con.getDate());
            System.out.print(" ");
            System.out.print(con.getRel());
            System.out.print(" ");
            System.out.println(con.getFilename());
        }
    }
    public void printNetWorkToFile(String filename) {
        List<String> lines = getNetworkToPrint (filename);
        Path file = Paths.get("the-file-name2.txt");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.print(e.getMessage());
//Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        }
    }

    private List<String> getNetworkToPrint (String filename) {
        List<String> e = new ArrayList<>();
        e.add(filename);
        for (connex con : cons) {
            String s = "[" +nodes.get(con.getFirst())+ "," + nodes.get(con.getSecond()) + "," + con.getDate() + "," + con.getRel() +"]";
            e.add(s);
        }
        return e;
    }

}
