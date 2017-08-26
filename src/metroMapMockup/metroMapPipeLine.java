package metroMapMockup;

import graphDisplay.colourManager;
import graphDisplay.displayPipeline;
import infoextraction.graphDbPipeline;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 22-Aug-17.
 */
public class metroMapPipeLine {
    private graphDbPipeline database = new graphDbPipeline();
    metroMap map;
    colourManager colour = new colourManager();
    GraphModel graphModel;
    UndirectedGraph undirectedGraph;
    PreviewModel preview;
    int size;



    private void createDisplay(){
        startDB();
        map = new metroMap(database.getMetroLines(),database.getMetroStops());
        initializeWorkSpace();
        getNodes();
        getEdges();
//        map.printLines();
        exportGraph();
    }

    private void initializeWorkSpace(){
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get a graph model - it exists because we have a workspace
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        graphModel.getNodeTable().addColumn("Entity1", String.class);
        graphModel.getNodeTable().addColumn("Entity2", String.class);
        graphModel.getNodeTable().addColumn("Year", String.class);
        graphModel.getNodeTable().addColumn("Relation", String.class);
        graphModel.getEdgeTable().addColumn("entity",String.class);
        undirectedGraph = graphModel.getUndirectedGraph();
    }
    private void startDB(){
        database.readDatabase();
    }
    private void exportGraph(){
        //Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("demo2MetroMapFull.gexf"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private void getNodes(){
        for(metroStop stop: map.getStops()){
            Node n = graphModel.factory().newNode(Integer.toString(stop.getId())) ;
            n.setAttribute("Entity1",stop.getLine1());
            n.setAttribute("Entity2",stop.getLine2());
            n.setAttribute("Year",stop.getYear());
            n.setAttribute("Relation",stop.getRelationship());
            n.setColor(Color.WHITE);
            n.setLabel(stop.getYear() );
            undirectedGraph.addNode(n);
        }
    }

    private int getNodeNumber(){
        return undirectedGraph.getNodeCount();
    }

    private int findFirstPerfectSquare(int i){
        int sqrt = (int) Math.sqrt(i);
        while(sqrt * sqrt != i){
            i++;
            sqrt = (int) Math.sqrt(i);
        }
        return i;
    }

    private boolean[][] setLayout(){
        int i = (int) Math.sqrt(findFirstPerfectSquare(getNodeNumber()+ 10));
        size = i*3;
        boolean[][] layout = new boolean[i*3][i*3];
        for (int j = 0;j<i*3;j++){
            for(int l = 0; l<l*3;l++){
                layout[j][l] = false;
            }
        }
        return layout;
    }
    private int[] FindNextEmptyCell(boolean[][] array){
        int[] result = new int[2];
        for (int j = 1;j<size -1;j = j+3 ) {
            for (int l = 1; l < size - 1; l = l + 3) {
                if (!array[j][l] && array[j - 1][l] && !array[j - 1][l + 1] && !array[j - 1][l - 1] &&
                        !array[j + 1][l] && !array[j + 1][l + 1] && !array[j + 1][l - 1] && !array[j][l + 1]
                        && !array[j][l - 1]) {
                    result[0] = j;
                    result[1] = l;
                    return result;
                }
            }
        }
         return result;
    }

    private boolean[][] markAsOccupied(boolean[][] array, int[] cell){
        for (int j = cell[0] -1; j<= cell[0] + 1;j++ ) {
            for (int l = cell[1] - 1; l < cell[1] - 1; l++) {
                array[j][l] = true;
            }
        }
        return array;
    }

    private boolean[][] placeNode(Node n, boolean[][] array){
        int[] pos = FindNextEmptyCell(array);
        array = markAsOccupied(array,pos);
        n.setPosition((float)pos[0],(float)pos[1]);
        n.setX((float)pos[0]);
        n.setY((float)pos[1]);
        return array;
    }

    private void getEdges(){
        int id = 0;
        String lastNodeId ;
        for(metroLine m :map.getLines()) {
            Node n = graphModel.factory().newNode(m.getName() + " start");
            n.setLabel(m.getName());
            lastNodeId = m.getName() + " start";
            undirectedGraph.addNode(n);
            for (int i = 0; i < m.getSortedYears().length; i++) {
                metroStop s = m.searchByYear(m.getSortedYears()[i]);
                Edge edge = graphModel.factory().newEdge(undirectedGraph.getNode(lastNodeId),
                            undirectedGraph.getNode(Integer.toString(s.getId())),id, false);
                edge.setColor(new Color(colour.createSineWaves(Integer.toString(m.getId())),
                        colour.createSineWaves(Integer.toString(m.getId())),
                        0));
                edge.setWeight(20);
                id++;
                lastNodeId = Integer.toString(s.getId());
                edge.setLabel(m.getName());
                edge.setAttribute("entity", m.getName());
                undirectedGraph.addEdge(edge);
            }
            undirectedGraph.removeNode(n);

//            Node n2 = graphModel.factory().newNode(m.getName() + " end");
//            n2.setLabel(m.getName());
//            undirectedGraph.addNode(n2);
//
//            Edge edge = graphModel.factory().newEdge(undirectedGraph.getNode(lastNodeId),
//                    undirectedGraph.getNode(m.getName() + " end"), id, false);
//            id++;
//            edge.setAttribute("entity", m.getName());
//            undirectedGraph.addEdge(edge);

        }

    }

    public static void main(String args[]) {
        metroMapPipeLine display = new metroMapPipeLine();
        display.createDisplay();
    }

}
