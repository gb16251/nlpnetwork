package infoextraction;

import graphDisplay.displayPipeline;
import metroMapMockup.metroMapPipeLine;

import java.io.IOException;
import java.util.List;

/**
 * Created by Gabriela on 07-Sep-17.
 */
public class inforun {
    graphDbPipeline database;
    private NLPPipeline nlp;

    public NLPPipeline getNlp() {
        return nlp;
    }

    public static void main(String[] args){
        inforun run = new inforun(args);
    }
    public inforun(String[] args){
//        database = new graphDbPipeline(args[1]);
//        startDB();
        openFiles files = new openFiles(args[0]);
        List<fileRecorder> fileRec = files.getText();
        nlp = new NLPPipeline(fileRec);
        nlp.evalDefault();
//        insertToDatabase(nlp.getNetwork());
//        displayPipeline disp = new displayPipeline(database, args[1]);
//        disp.createDisplay();
//        metroMapPipeLine mm = new metroMapPipeLine(database,args[1]);
//        mm.createDisplay();
    }

    private void startDB(){
        try {
            database.initializeGraphDB();
        }
        catch (IOException e ){}
    }
    public void insertToDatabase(netTemplate network ){
        for (conTemplate con: network.getConnections()){
            database.addAdvancedConnection(con.getNode1(),con.getNode2(),con.getDate(),con.getFilename(),con.getRel(),con.getSentence());
        }
    }

}
