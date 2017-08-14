package graphDisplay;

import infoextraction.graphDbPipeline;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

/**
 * Created by Gabriela on 09-Aug-17.
 */
public class oldDisplayPipeline {
    private graphDbPipeline database = new graphDbPipeline();
    Graph graph = new MultiGraph("tutorial 1");
    colourManager colour = new colourManager();
    private String styleSheet =
            "" +
                    "node {\n" +
                    "\tfill-mode: dyn-plain;\n" +
                    "\tfill-color: green, red, blue;\n" +
                    "}" +
                    "edge {\n" +
                    "\tshape: line;\n" +
                    "\tfill-color: #222;\n" +
                    "\tpadding: 0px;\n" +
                    "}";


    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new oldDisplayPipeline();
    }

    public oldDisplayPipeline(){
        startDB();
        getNodes();
        manageEdges();
        styleNodes();
        graph.display();
//        System.out.println(graph.getNodeCount());

    }

    public int splitOnSpace(String s) {
        String[] words = s.split(" ");
        int average = 0;
        int length = 0;
        if (words.length == 1) return Integer.parseInt(words[0]);
        for (String word : words) {
            if (!word.equals("")) {
                average += Integer.parseInt(word);
                length++;
            }
        }
//        System.out.println(average/length);
        return average/length;
    }

    private void startDB(){
        database.readDatabase();
    }

    private void manageEdges(){
        for (edgeContainer e: database.getRelationships()){
            graph.addEdge(e.getNode1() + "/" + e.getNode2(), e.getNode1(), e.getNode2());
        }

    }
    private void getNodes(){
        for (nodeContainer s :database.getEntityNodes()){
            graph.addNode(s.toString());
            graph.getNode(s.toString()).addAttribute("mentions",splitOnSpace(s.getOccurences()));
        }
    }

    private void styleNodes(){
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", styleSheet);
        for(Node node:graph) {
            int size = (node.getDegree()+1)*3;
            node.setAttribute("x", size);

            node.addAttribute("ui.style", "fill-color: rgb(" + colour.createSineWaves(node.getAttribute("mentions").toString()) + ",100,255); size:" + Integer.toString(size)+"px;");
        }
    }


}
