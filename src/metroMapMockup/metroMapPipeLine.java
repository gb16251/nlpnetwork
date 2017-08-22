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

import java.io.File;
import java.io.IOException;

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
            ec.exportFile(new File("metromaptrial8.gexf"));
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
            n.setLabel(stop.getYear() );
            undirectedGraph.addNode(n);
        }
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
                id++;
                lastNodeId = Integer.toString(s.getId());
                edge.setAttribute("entity", m.getName());
                undirectedGraph.addEdge(edge);
            }


            Node n2 = graphModel.factory().newNode(m.getName() + " end");
            n2.setLabel(m.getName());
            undirectedGraph.addNode(n2);

            Edge edge = graphModel.factory().newEdge(undirectedGraph.getNode(lastNodeId),
                    undirectedGraph.getNode(m.getName() + " end"), id, false);
            id++;
            edge.setAttribute("entity", m.getName());
            undirectedGraph.addEdge(edge);

        }

    }

    public static void main(String args[]) {
        metroMapPipeLine display = new metroMapPipeLine();
        display.createDisplay();
    }

}
