package metroMapMockup;

import edu.stanford.nlp.util.ArraySet;
import graphDisplay.colourManager;
import graphDisplay.displayPipeline;
import infoextraction.graphDbPipeline;
import infoextraction.inforun;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Gabriela on 22-Aug-17.
 */
public class metroMapPipeLine {
    private graphDbPipeline database;
    metroMap map;
    String fileName;
    colourManager colour = new colourManager();
    GraphModel graphModel;
    DirectedGraph directedGraph;
    PreviewModel preview;
    int size;


    public static void main(String[] args){
        graphDbPipeline db = new graphDbPipeline("demo2/neo4j-store");
        db.readDatabase();
        metroMapPipeLine mm = new metroMapPipeLine(db,"fullthingy");
        mm.createDisplay();
    }

    public metroMapPipeLine(graphDbPipeline database,String filename){
        this.database = database;
        this.fileName = filename;
    }

    public void createDisplay(){
        map = new metroMap(database.getMetroLines(),database.getMetroStops());
        initializeWorkSpace();
        getNodes();
        getEdges();
        setPreview();
        exportGraph();
    }

    private void setPreview(){
        preview = Lookup.getDefault().lookup(PreviewController.class).getModel();
        preview.getProperties().putValue(PreviewProperty.EDGE_COLOR,new EdgeColor(Color.GREEN));
        preview.getProperties().putValue(PreviewProperty.EDGE_THICKNESS,new Float(0.1f));
        preview.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT,
                preview.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));

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

        directedGraph = graphModel.getDirectedGraph();
    }

    private void exportGraph(){
        //Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("gephi/" + fileName + "mm"+".gexf"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private void getNodes(){
        for(metroStop stop: map.getReducedStops()){
            Node n = graphModel.factory().newNode(Integer.toString(stop.getId())) ;
            n.setAttribute("Entity1",stop.getLine1());
            n.setAttribute("Entity2",stop.getLine2());
            n.setAttribute("Year",stop.getYear());
            n.setAttribute("Relation",stop.getRelationship());
            n.setColor(Color.WHITE);
            n.setLabel(stop.getYear() + " " + stop.getRelationship());
            directedGraph.addNode(n);
        }
    }

    private void getEdges(){
        int id = 0;
        String lastNodeId ;
        float y = 100;
        for(metroLine m :map.getLines()) {
            float x = -800;
            Node n = graphModel.factory().newNode(m.getName() + " start");
            n.setX(-1000); n.setY(y);
            n.setLabel(m.getName());
            lastNodeId = m.getName() + " start";
            directedGraph.addNode(n);
            for (int i = 0; i < m.getSortedYears().length; i++) {
                metroStop s = m.searchByYear(m.getSortedYears()[i]);
                Node target = getNodeByRel(s.getYear(),s.getRelationship());
                if(!edgeExists(directedGraph.getNode(lastNodeId), target)) {
                    addEdge(m,target,lastNodeId,id);
                    x = setTargetNodePosition(target,x,y);
                    id++;
                    lastNodeId = target.getId().toString();
                }
            }
            y = y - 50;
        }
    }

    private float setTargetNodePosition(Node target, float x, float y){
        if(target.y() == 0){
            target.setY(y);
        }
        if( target.x() == 0){
            target.setX(x);
        }
        return x + 200;
    }

    private void addEdge(metroLine m, Node target,String lastNodeId, int id){
        Edge edge = graphModel.factory().newEdge(directedGraph.getNode(lastNodeId),
                target, id, true);
        edge.setLabel(m.getName());
        edge.setAttribute("entity", m.getName());
        directedGraph.addEdge(edge);
    }
    private boolean edgeExists(Node n1, Node n2){
        for(Edge e : directedGraph.getEdges().toArray()) {
            if (e.getSource().getLabel().equals(n1.getLabel()) && e.getTarget().getLabel().equals(n2.getLabel())) {
                return true;
            }
            if (e.getSource().getLabel().equals(n2.getLabel()) && e.getTarget().getLabel().equals(n1.getLabel())) {
                return true;
            }

        }
        return false;
    }


    private Node getNodeByRel(String year, String rel){
        for(Node n :directedGraph.getNodes().toArray()){
            if(n.getAttribute("Year").toString().equals(year)){
                if(rel.length()==0){
                    return n;
                }
                 else if(rel.length()>0) {
                    if (n.getAttribute("Relation").toString().equals(rel)) {
                        return n;
                    }
                }
            }
        }
        return null;
    }
    private void startDB(){
        try {
            database.initializeGraphDB();
        }
        catch (IOException e ){}
    }

}
