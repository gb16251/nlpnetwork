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
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
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
import java.util.concurrent.TimeUnit;

import static org.neo4j.server.rest.transactional.ResultDataContent.graph;


public class displayPipeline {
    private graphDbPipeline database = new graphDbPipeline();
    colourManager colour = new colourManager();
    GraphModel graphModel;
    UndirectedGraph undirectedGraph;
    PreviewModel preview;

    private void setPreview(){
        preview = Lookup.getDefault().lookup(PreviewController.class).getModel();
        preview.getProperties().putValue(PreviewProperty.EDGE_COLOR,new EdgeColor(Color.GREEN));
        preview.getProperties().putValue(PreviewProperty.EDGE_THICKNESS,new Float(0.1f));
        preview.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT,
                preview.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));

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

    private void getNodes(){
        for (nodeContainer s :database.getEntityNodes()){
            Node n = graphModel.factory().newNode(s.toString());
            n.setLabel(s.toString());
            n.setAttribute("mentions",splitOnSpace(s.getOccurences()));
            undirectedGraph.addNode(n);
        }
    }
    private void manageEdges(){
        for (edgeContainer e: database.getRelationships()){
            Edge edge = graphModel.factory().newEdge(undirectedGraph.getNode(e.getNode1()),
                    undirectedGraph.getNode(e.getNode2()),0,false);
            edge.setAttribute("matches",e.getMatches());
            undirectedGraph.addEdge(edge);
        }

    }
    private void startDB(){
        database.readDatabase();
    }
    private void initializeWorkSpace(){
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get a graph model - it exists because we have a workspace
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        graphModel.getNodeTable().addColumn("mentions", Integer.class);
        graphModel.getEdgeTable().addColumn("matches",Integer.class);
        undirectedGraph = graphModel.getUndirectedGraph();
    }

    private void createDisplay(){
        startDB();
        initializeWorkSpace();
        getNodes();
        manageEdges();
        setPreview();
        manageNodeRankings();
        manageColourRanking();
        manageedgeThickness();
        exportGraph();
//        System.out.println("Node2 degree: " + undirectedGraph.getDegree(undirectedGraph.getNode("Bank of England")));

    }
    private void exportGraph(){
        //Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("io_gexf.gexf"));
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

    private void manageNodeRankings(){
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();
        Function degreeRanking = appearanceModel.getNodeFunction(undirectedGraph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer degreeTransformer = (RankingNodeSizeTransformer)(degreeRanking.getTransformer());
        degreeTransformer.setMinSize(10);
        degreeTransformer.setMaxSize(50);
        appearanceController.transform(degreeRanking);
    }

    private void manageColourRanking(){
        colourManager colors = new colourManager();
        for (Node n: undirectedGraph.getNodes()){
            n.setColor(new Color(colors.createSineWaves(n.getAttribute("mentions").toString())));
//            n.setR(0.2f);
//            n.setG(0.3f);
//            n.setB(0.7f);
        }
    }

    private void manageedgeThickness(){
        for (Edge e: undirectedGraph.getEdges()){
            e.setWeight(Double.parseDouble(e.getAttribute("matches").toString()));
        }
    }

    private void setAutoLayout(){
        AutoLayout autoLayout = new AutoLayout(5, TimeUnit.MINUTES);
        autoLayout.setGraphModel(graphModel);
        YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
        ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 500., 0f);//500 for the complete period
        autoLayout.addLayout(firstLayout, 0.5f);
        autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
        autoLayout.execute();

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("autolayout.gexf"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String args[]) {
        displayPipeline display = new displayPipeline();
        display.createDisplay();
    }

    }