package infoextraction;

/**
 * Created by Gabriela on 14-Aug-17.
 */
public class floodFill {
    public static void main(String[] args) {
        graphDbPipeline db = new graphDbPipeline();
        db.readDatabase();
        System.out.println(db.countDisconnectedGraphs());
    }
}
