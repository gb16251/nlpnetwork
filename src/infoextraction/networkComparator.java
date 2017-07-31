package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 24-Jul-17.
 */
public class networkComparator {
    private netTemplate ideal = new netTemplate();
    private netTemplate tocompare = new netTemplate();
    private int alpha;
    private int beta;
    private int gamma;
    List<Integer> scores = new ArrayList<Integer>();


    public networkComparator(int a, int b, int c, netTemplate ideal, netTemplate tocompare){
        this.alpha = a;
        this.beta = b;
        this.gamma = c;
        this.ideal = ideal;
        this.tocompare = tocompare;
    }

    public float calculatePositiveScore() {
        getTruePositivesNodes();
        return addScore() / scores.size();
    }


    private int addScore() {
        int sum = 0;
        for (int i : scores) {
            sum += i;
        }
        return sum;
    }

    private void getTruePositivesNodes() {
        int i = 0;
        for (String s : ideal.getNodes()) {
            i = 0;
            if (tocompare.hasNode(s)) {
                i += alpha;
                List<conTemplate> conToCompare = tocompare.getNodeConnections(s);
                List<conTemplate> idealcon = ideal.getNodeConnections(s);
                i += compareConnections(idealcon, conToCompare);
            } else {
                i = 0;
            }

            scores.add(i);
        }
    }

    private int compareConnections(List<conTemplate> id, List<conTemplate> tocomp) {
        int foundCon = 0;
        int idealCon = 0;
        int date = 0;
        int result =0;
        for (conTemplate ideal : id) {
            idealCon++;
            for (conTemplate comp: tocomp){
                if (sameConnection(ideal, comp) == 1) {
                    foundCon++;
                } else if (sameConnection(ideal,comp) == 2) {
                    foundCon++;
                    date++;
                }
            }
        }
        if(idealCon != 0 ){
            float res = getMin(foundCon,idealCon)* (float)beta + ((float)date/(float)idealCon) * (float)gamma;
            result = Math.round(res);
        }
        else{
            result = beta + gamma;
        }
        return result ;
    }

    private float getMin(int foundCon, int idealCon){
        if(foundCon>idealCon) return 1;
        return (float)foundCon / (float)idealCon;

    }

    private int sameConnection(conTemplate ideal, conTemplate tocomp) {
        int i = 0;
        if ((ideal.getNode1().equals(tocomp.getNode1()) && (ideal.getNode2().equals(tocomp.getNode2())))) i = 1;
        if ((ideal.getNode2().equals(tocomp.getNode1()) && (ideal.getNode1().equals(tocomp.getNode2())))) i = 1;
        if (i == 1) {
            if (ideal.getDate().toLowerCase().equals(tocomp.getDate().toLowerCase())) i++;
        }
        return i;
    }


    private void getTrueFalsePositiveRatio() {
        int falseNodes = 0;
        int trueNodes = 0;

        List<String> idealNodes = ideal.getNodes();
        for (String s : tocompare.getNodes()) {
            if (!idealNodes.contains(s)) {
                falseNodes++;
            }
            else{
                trueNodes++;
            }
        }
        System.out.println(trueNodes/ideal.getNodes().size());
        System.out.println(trueNodes/tocompare.getNodes().size());

    }
    public void printAllScores(){
        System.out.print("Number of scores:");
        System.out.println(scores.size());
        for (int i: scores){
            System. out.print(i);
            System. out.print(" ");
        }
        System. out.println();

    }

}
