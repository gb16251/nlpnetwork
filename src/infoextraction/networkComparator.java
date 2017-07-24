package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 28-Jun-17.
 */
public class networkComparator {
    private networkTemplate idealNetwork = new networkTemplate();
    private networkTemplate testNetwork = new networkTemplate();
    private List<String> scoreList =new ArrayList<>();
    private int score;

    public void setTestNetwork (networkTemplate net){
        testNetwork = net;
    }
    public void setIdealNetwork (networkTemplate net){
        idealNetwork = net;
    }


    public void compareNetworks(){
        List<entityNode> idealNodes = testNetwork.getNodes();
        entityNode test = new entityNode();
        for (entityNode ideal : idealNodes){
            if ((test = testNetwork.getNodeByName(ideal.getNodeName()) )!= null ){
                scoreList.add(Double.toString(compareNodes(ideal, test)));
            }
            else {
                scoreList.add(Double.toString(0.0) );
            }


        }
    }
    private double compareNodes(entityNode ideal, entityNode test){
        double nodeScore = 0.5; //Node gets half a point if the node exists
        nodeScore += compareConnections(ideal,test);
        return nodeScore;

    }
    private double compareConnections(entityNode ideal, entityNode test){
        double score = 0;
        double finalscore = 0;
        double idealnumber = (double)ideal.getConnectionNumber();
        List<nodeConnection> idealcon = ideal.getConnections();
        List<nodeConnection> testcon = test.getConnections();
        for (nodeConnection con: idealcon){
            if( test.searchConnectionByConnectingEntity(con.getConnectingNodeName())){
                score++;
            }
        }
        if( idealnumber == 0) {
            if(score == 0){
                finalscore = 0.5;
            }
//            For now if a node ideally has 0 connections and the test has more than 0,
//            the test gets 0 points
            else {
                finalscore = 0;
            }
        }
        else{
            finalscore = 0.5* (score/idealnumber);
        }

        return finalscore;
    }

}
