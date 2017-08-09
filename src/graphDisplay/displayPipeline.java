package graphDisplay;

import infoextraction.graphDbPipeline;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;

/**
 * Created by Gabriela on 09-Aug-17.
 */
public class displayPipeline {
    private graphDbPipeline database = new graphDbPipeline();
    Graph graph = new SingleGraph("tutorial 1");


    public static void main(String args[]) {
        new displayPipeline();
    }

    public displayPipeline(){
        startDB();
        getNodes();
        database.getRelationships();
        graph.display();

    }

    private void startDB(){
        database.readDatabase();
    }

    private void getNodes(){
        for (nodeContainer s :database.getEntityNodes()){
            graph.addNode(s.toString());
        }
    }
}
