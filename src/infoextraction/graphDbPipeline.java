package infoextraction;

import graphDisplay.edgeContainer;
import graphDisplay.nodeContainer;
import metroMapMockup.metroLine;
import metroMapMockup.metroMap;
import metroMapMockup.metroStop;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.io.fs.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gabriela on 22-Jun-17.
 */
public class graphDbPipeline {
    private static File DB_PATH;
    private static final String NAME_KEY = "neo4j";
    private static GraphDatabaseService graphDb;
    private static Index<Node> entities;
    IndexHits<Relationship> timeRelationships;
    public enum RelTypes implements RelationshipType
    {
        INTERACTION,
        MATCHES
    }
    public graphDbPipeline(String fileName){
        String s = "databases/" + fileName;
        DB_PATH = new File( s);
    }

    public void readDatabase(){
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        registerShutdownHook();

    }


    public void initializeGraphDB() throws IOException{
        FileUtils.deleteRecursively( DB_PATH );
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        registerShutdownHook();
        try ( Transaction tx = graphDb.beginTx() ) {
            entities = graphDb.index().forNodes( "entity" );
            tx.success();
        }
    }
    public void addTriple(String e1, String rel, String e2){

    }

    public Node addNode(String ent1,int sentence){
        try ( Transaction tx = graphDb.beginTx() ){
            Node node = graphDb.createNode();
            node.setProperty("entity", ent1);
            node.setProperty("mentionPlace"," "+ Integer.toString(sentence)+ " ");
            entities.add(node, "entity", node.getProperty("entity"));
            tx.success();
            return node;
        }
    }
    public void addBasicConnection(String ent1, String ent2,String date){
        Node first;
        Node second;
//        System.out.println("I'm adding a basic connection");

        if((first = searchNodeByName(ent1)) == null) {
            first = addNode(ent1,0);
//            System.out.println("I'm adding a node");

        }
        if((second = searchNodeByName(ent2)) == null) {
//            System.out.println("I'm adding a second node");

            second = addNode(ent2,0);
        }
        createRelationship(first,second,date);
    }


    public void addAdvancedConnection(String ent1, String ent2,String date,String filename,String rel, int sentence){
        Node first;
        Node second;
//        System.out.println("I'm adding a basic connection");

        if((first = searchNodeByName(ent1)) == null) {
            first = addNode(ent1,sentence);
        }
        else addMention(first,sentence);

        if((second = searchNodeByName(ent2)) == null) {
            second = addNode(ent2,sentence);
        }
        else addMention(second,sentence);
        createAdvancedRelationship(first,second,date,filename, rel);
    }


    private void createAdvancedRelationship(Node first, Node second, String date,String filename,String relationshipName){
        setMatches(first,second,filename);
//        setSources(first,second,filename);
        if (needNewConnection(first, second, date,relationshipName) && (!date.equals("") || !relationshipName.equals(""))) {
            try (Transaction tx = graphDb.beginTx()) {
                Relationship rel = first.createRelationshipTo(second, RelTypes.INTERACTION);
                rel.setProperty("date", date);
                rel.setProperty("relationship", relationshipName);
                rel.setProperty("document", filename);
                tx.success();
            }
        }

    }
    private void setSources(Node first, Node second,String filename) {
        try (Transaction tx = graphDb.beginTx()) {
            if(!fillSources(first,second,filename)){
                Relationship rel = first.createRelationshipTo(second, RelTypes.MATCHES);
                rel.setProperty("files",filename);
            }
            tx.success();
        }
    }


    private boolean fillSources (Node first, Node second,String filename){
        for (Relationship r: first.getRelationships(RelTypes.MATCHES)) {
            if (r.getOtherNode(first).getProperty("entity").equals(second.getProperty("entity"))) {
                if (!sourceExists(r,filename)) {
                    r.setProperty("files", r.getProperty("files").toString()+ " " + filename);
                }
//                System.out.println(r.getProperty("matches").toString());
                return true;
            }
        }
        return false;
    }

//    See if the given source already exists on the edge
    private boolean sourceExists(Relationship r,String filename){
        if(r.getProperty("files").toString().contains(filename)) return true;
        return false;
    }

//    Add new INTERACTION between nodes
    private void createRelationship(Node first, Node second, String date){
        setMatches(first,second,"");
        if (needNewConnection(first, second, date,"") && !date.equals("")) {
            try (Transaction tx = graphDb.beginTx()) {
                Relationship rel = first.createRelationshipTo(second, RelTypes.INTERACTION);
                rel.setProperty("date", date);
                tx.success();
            }
        }
    }

//    Create a MATCHES type edge between nodes
    private void setMatches(Node first, Node second,String filename){
        try (Transaction tx = graphDb.beginTx()) {
            if (!increaseMatches(first,second,filename)){
                Relationship rel = first.createRelationshipTo(second, RelTypes.MATCHES);
                rel.setProperty("matches", "1");
                rel.setProperty("files", filename + " " );
            }
            tx.success();
        }
    }

//    Find and increase the matches edge and add a new file if needed
    private boolean increaseMatches (Node first, Node second,String filename){
        for (Relationship r: first.getRelationships(RelTypes.MATCHES)) {
            if (r.getOtherNode(first).getProperty("entity").equals(second.getProperty("entity"))) {
                r.setProperty("matches", increaseString(r.getProperty("matches").toString()));
                if (!sourceExists(r,filename)) {
                    r.setProperty("files", r.getProperty("files").toString()+ " " + filename);
                }
                return true;
            }
        }
        return false;
    }

//    Increases the matches string
    private String increaseString(String s){
        int i = Integer.parseInt(s);
        i++;
        return Integer.toString(i);
    }

//    Add a new connection between the two nodes
    private boolean needNewConnection(Node first, Node second, String date,String rel){
        try (Transaction tx = graphDb.beginTx()) {
            for (Relationship r : first.getRelationships()) {
                if (r.getOtherNode(first) == second) {
                    if (r.getProperty("date") == date && r.getProperty("relationship") == rel) {
                        return false;
                    }
                }
            }
            tx.success();
        }
        return true;
    }



//    Add mention place to node
    private Node addMention(Node node, int i){
        try (Transaction tx = graphDb.beginTx()) {
            if (!node.getProperty("mentionPlace").toString().contains(" " + Integer.toString(i) + " ")) {
                node.setProperty("mentionPlace", node.getProperty("mentionPlace").toString() + " " + Integer.toString(i)+" ");
                tx.success();
            }
        }
            return node;
    }


    public void addMentionPlace(String node, int i ){
        Node n = searchNodeByName(node);
        if (n!=null){
            n.setProperty("mentionPlace",n.getProperty("mentionPlace").toString() + " " + Integer.toString(i));
        }
    }
//    Check if a node already exists before adding it to the Db
        private Node searchNodeByName(String s){
            try (Transaction tx = graphDb.beginTx()) {
                Node found;
                IndexHits<Node> hits = entities.get("entity", s);
                found = hits.getSingle();
                tx.success();
                if (found == null) {
                    return null;
                }
                return found;
            }
    }

    private TraversalDescription traversal() {
        TraversalDescription entityTraversal = graphDb.traversalDescription()
                .depthFirst()
                .relationships(RelationshipType.withName("date"))
                .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);
        return entityTraversal;
    }
    private boolean linkFinder(Node start,Node end) {
        for (Node currentNode : traversal()
                .evaluator(Evaluators.toDepth(1))
                .traverse(start)
                .nodes()){
            if (currentNode == end){
                return true;
            }
        }
        return false;
    }
    public List<nodeContainer> getEntityNodes(){
        List<nodeContainer> addToDisplay = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Node> iterable = graphDb.getAllNodes();
            for (Node n : iterable) {
                nodeContainer container = new nodeContainer(n.getProperty("entity").toString(), n.getProperty("mentionPlace").toString());
                addToDisplay.add(container);
            }
            tx.success();
        }
        return addToDisplay;
    }

    public List<edgeContainer> getInteractionRelationships() {
        List<edgeContainer> edges = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Relationship> rels = graphDb.getAllRelationships();
            for (Relationship r : rels) {
                if (r.getType().name().equals("INTERACTION")) {
                    edgeContainer e = new edgeContainer(r.getEndNode().getProperty("entity").toString(),
                            r.getStartNode().getProperty("entity").toString(),
                            r.getProperty("document").toString(),
                            1);
                    e.setAdditionalInfo( r.getProperty("date").toString(),
                            r.getProperty("relationship").toString());
                    edges.add(e);
//                    System.out.println(r.getEndNode().getProperty("entity").toString());
//                    System.out.println( r.getStartNode().getProperty("entity").toString());
//                    System.out.println(r.getProperty("matches").toString());
                }
                tx.success();
            }
        }
        return edges;
    }

    public List<edgeContainer> getRelationships() {
        List<edgeContainer> edges = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Relationship> rels = graphDb.getAllRelationships();
            for (Relationship r : rels) {
                if (r.getType().name().equals("MATCHES")) {
                    edgeContainer e = new edgeContainer(r.getEndNode().getProperty("entity").toString(),
                            r.getStartNode().getProperty("entity").toString(),
                            r.getProperty("files").toString(),
                            Integer.parseInt(r.getProperty("matches").toString()));
                    edges.add(e);
//                    System.out.println(r.getEndNode().getProperty("entity").toString());
//                    System.out.println( r.getStartNode().getProperty("entity").toString());
//                    System.out.println(r.getProperty("matches").toString());
                }
                tx.success();
            }
        }
        return edges;
    }


    public int countDisconnectedGraphs() {
        removeAllLabels();
        boolean done = false;
        int count = 0;
            while(!done) {
                Node n = findUnlabelledNode();
                if (n == null) {
                    done = true;
                }
                else {
                    count++;
                    try (Transaction tx = graphDb.beginTx()) {
                        n.addLabel(Label.label("checked"));
                        Traverser trav = findConnectedGraph(n);
                        for (Node node: trav.nodes()){
                            node.addLabel(Label.label("checked"));
                        }
                        tx.success();
                    }
                }
            }
        removeAllLabels();
        return count;
    }

    private void removeAllLabels(){
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Node> iterable = graphDb.getAllNodes();
            for (Node n : iterable) {
                n.removeLabel(Label.label("checked"));
            }
            tx.success();
        }
    }

    private Node findUnlabelledNode(){
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Node> iterable = graphDb.getAllNodes();
            for (Node n : iterable) {
                if (!n.hasLabel(Label.label("checked"))) {
                    System.out.println(n.getProperty("entity"));
                    return n;
                }
            }
            tx.success();
            return null;
        }
    }

    public HashMap<String,Integer> setMetroId(){
        HashMap<String,Integer> metroID = new HashMap<>();
        int i = 1;
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Node> iterable = graphDb.getAllNodes();
            for (Node n : iterable) {
                if(n.hasRelationship(RelTypes.INTERACTION)){
                    metroID.put(n.getProperty("entity").toString(),i++);
                }
            }
            tx.success();
        }
        return metroID;
    }

    public List<metroLine>  getMetroLines(){
        int i = 1;
        List<metroLine> lines = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Node> iterable = graphDb.getAllNodes();
            for (Node n : iterable) {
                if(n.hasRelationship(RelTypes.INTERACTION)){
                lines.add(new metroLine(i++,n.getProperty("entity").toString()));
                }
            }
            tx.success();
        }
        return lines;
    }
    public  List<metroStop> getMetroStops() {
        List<metroStop> stops = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            int i = 0;
            Iterable<Relationship> rels =  graphDb.getAllRelationships();
            for(Relationship r: rels){
                if (r.hasProperty("date") || r.hasProperty("relationship")) {
                    String date = "0";
                    if (r.getProperty("date").toString().length() > 0) {
                         date = r.getProperty("date").toString();
                    }
                    stops.add(new metroStop(r.getEndNode().getProperty("entity").toString(),
                                r.getStartNode().getProperty("entity").toString(),
                                date,
                                r.getProperty("relationship").toString(),
                                i));
                    i++;
                }
            }
            tx.success();
        }
        return stops;
    }


    private Traverser findConnectedGraph(Node startNode)
    {
        TraversalDescription td = graphDb.traversalDescription();
        return td.traverse( startNode );
    }

        private static void shutdown()
    {
        graphDb.shutdown();
    }
    private static void registerShutdownHook()
    {
        // Registers a shutdown hook for the Neo4j and index service instances
        // so that it shuts down nicely when the VM exits (even if you
        // "Ctrl-C" the running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                shutdown();
            }
        } );
    }
}
