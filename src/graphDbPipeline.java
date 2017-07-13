import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Rels;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.io.fs.FileUtils;

import javax.management.relation.Relation;
import java.io.File;
import java.io.IOException;

/**
 * Created by Gabriela on 22-Jun-17.
 */
public class graphDbPipeline {
    private static final File DB_PATH = new File( "tri2/neo4j-store" );
    private static final String NAME_KEY = "neo4j";
    private static GraphDatabaseService graphDb;
    private static Index<Node> entities;
    IndexHits<Relationship> timeRelationships;
    public enum RelTypes implements RelationshipType
    {
        UNKNOWN,
        LOCATED_IN,
        MATCHES
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

    public Node addNode( String ent1){
        try ( Transaction tx = graphDb.beginTx() ){
            Node node = graphDb.createNode();
            node.setProperty("entity", ent1);
            entities.add(node, "entity", node.getProperty("entity"));
            tx.success();
            return node;
        }
    }
    public void addBasicConnection(String ent1, String ent2,String date){
        Node first;
        Node second;

        if((first = searchNodeByName(ent1)) == null) {
            first = addNode(ent1);
        }
        if((second = searchNodeByName(ent2)) == null) {
            second = addNode(ent2);
        }
        createRelationship(first,second,date);
    }

    private void createRelationship(Node first, Node second, String date){
        if(date.equals("")){

        }
        else if (needNewConnection(first,second,date)) {
            try (Transaction tx = graphDb.beginTx()) {
                Relationship rel = first.createRelationshipTo(second, RelTypes.UNKNOWN);
                rel.setProperty("date", date);
                tx.success();
            }
        }
    }
    private void setMatches(Node first, Node second){
        try (Transaction tx = graphDb.beginTx()) {
            if (!increaseMatches(first,second)){
                Relationship rel = first.createRelationshipTo(second, RelTypes.MATCHES);
                rel.setProperty("matches", "1");
            }
            tx.success();
        }
    }

    private boolean increaseMatches (Node first, Node second){
        for (Relationship r: first.getRelationships(RelTypes.MATCHES)) {
            if (r.getOtherNode(first) == second) {
                r.setProperty("matches", increaseString(r.getProperty("matches").toString()));
                return true;
            }
        }
        return false;
    }

    private String increaseString(String s){
        int i = Integer.parseInt(s);
        i++;
        return Integer.toString(i);
    }

//    TODO: See if there already is an empty connection and then add to the number of matches if there is no date
    private boolean needNewConnection(Node first, Node second, String date){
        for (Relationship r: first.getRelationships(RelationshipType.withName("date"))) {
            if(r.getOtherNode(first) == second){
                if(r.getProperty("date") == date){
                    return false;
                }
            }
        }
        return true;
    }
//    Check if a node already exists before adding it to the Db
        private Node searchNodeByName(String s){
        try ( Transaction tx = graphDb.beginTx() ) {
            IndexHits<Node> hits = entities.get("entity", s);
            Node found = hits.getSingle();
            if (found == null) {
                return null;
            }
            tx.success();
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
