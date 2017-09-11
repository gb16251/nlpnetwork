package graphDisplay;/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org
This file is part of Gephi.
Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */

import infoextraction.graphDbPipeline;
import metroMapMockup.metroMapPipeLine;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
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


public class displayPipeline {
    private graphDbPipeline database;
    private String fileName;
    colourManager colour = new colourManager();
    GraphModel graphModel;
    UndirectedGraph undirectedGraph;
    PreviewModel preview;

    public static void main(String[] args){
        graphDbPipeline db = new graphDbPipeline("demo2/neo4j-store");
        db.readDatabase();
        displayPipeline d = new displayPipeline(db,"fullthingy2");
        d.createDisplay();
    }
//    Constructor
    public displayPipeline(graphDbPipeline database,String filename){
        this.database = database;
        this.fileName = filename;
    }

//    Create some default preview properties
    private void setPreview(){
        preview = Lookup.getDefault().lookup(PreviewController.class).getModel();
        preview.getProperties().putValue(PreviewProperty.EDGE_COLOR,new EdgeColor(Color.GREEN));
        preview.getProperties().putValue(PreviewProperty.EDGE_THICKNESS,new Float(0.1f));
        preview.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT,
                preview.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));
    }

//    Get the sent numbers by splitting the string on spaces and calculate the average
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

//    Import nodes from database
    private void getNodes(){
        for (nodeContainer s :database.getEntityNodes()){
            Node n = graphModel.factory().newNode(s.toString());
            n.setLabel(s.toString());
            n.setAttribute("mentions",splitOnSpace(s.getOccurences()));
            undirectedGraph.addNode(n);
        }
    }

//    Import matches edges from the database
    private void manageEdges(){
        for (edgeContainer e: database.getRelationships()){
            Edge edge = graphModel.factory().newEdge(undirectedGraph.getNode(e.getNode1()),
                    undirectedGraph.getNode(e.getNode2()),0,false);
            edge.setAttribute("matches",e.getMatches());
            edge.setAttribute("documents",e.getDocument());
            edge.setAttribute("year",null);
            edge.setAttribute("relationship",null);

            undirectedGraph.addEdge(edge);
        }
    }

//    Import INTERACTION type edges from the database
    private void manageInteractionEdges(){
        for (edgeContainer e: database.getInteractionRelationships()){
            Edge edge = graphModel.factory().newEdge(undirectedGraph.getNode(e.getNode1()),
                    undirectedGraph.getNode(e.getNode2()),0,false);
            edge.setAttribute("matches",e.getMatches());
            edge.setAttribute("documents",e.getDocument());
            edge.setAttribute("year",e.getDate());
            edge.setAttribute("hasrel","yes");
            edge.setAttribute("relationship",e.getRelationship());
            undirectedGraph.addEdge(edge);
        }
    }

//    Initialize a project and workspace
    private void initializeWorkSpace(){
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        graphModel.getNodeTable().addColumn("mentions", Integer.class);
        graphModel.getEdgeTable().addColumn("matches",Integer.class);
        graphModel.getEdgeTable().addColumn("documents",String.class);
        graphModel.getEdgeTable().addColumn("relationship",String.class);
        graphModel.getEdgeTable().addColumn("year",String.class);
        graphModel.getEdgeTable().addColumn("hasrel",String.class);

        undirectedGraph = graphModel.getUndirectedGraph();
    }

//    Create a display for the graph
    public void createDisplay(){
        initializeWorkSpace();
        getNodes();
        manageInteractionEdges();
        manageEdges();
        setPreview();
        manageNodeRankings();
        manageColourRanking();
        manageedgeThickness();
        exportGraph();
//        System.out.println("Node2 degree: " + undirectedGraph.getDegree(undirectedGraph.getNode("Bank of England")));

    }

//    Export graph to file
    private void exportGraph(){
        //Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("gephi/" + fileName +".gexf"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private int getMaxSentence(){
        int sent = 0;
        for (Node n: undirectedGraph.getNodes()){
            if(Integer.parseInt(n.getAttribute("mentions").toString()) > sent){
                sent = Integer.parseInt(n.getAttribute("mentions").toString());
            }
        }
        return sent;
    }

//    Manage node size by degree
    private void manageNodeRankings(){
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();
        Function degreeRanking = appearanceModel.getNodeFunction(undirectedGraph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer degreeTransformer = (RankingNodeSizeTransformer)(degreeRanking.getTransformer());
        degreeTransformer.setMinSize(10);
        degreeTransformer.setMaxSize(50);
        appearanceController.transform(degreeRanking);
    }

//    Colour nodes by how early they appear in text
    private void manageColourRanking(){
        colourManager colors = new colourManager();
        for (Node n: undirectedGraph.getNodes()){
            n.setColor(new Color(colors.createSineWaves(n.getAttribute("mentions").toString())));
        }

    }

//    Rank edge weight by number of matches
    private void manageedgeThickness(){
        for (Edge e: undirectedGraph.getEdges()){
            e.setWeight(Double.parseDouble(e.getAttribute("matches").toString()));
        }
    }

}