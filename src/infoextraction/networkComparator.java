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
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }

    public void setGamma(int gamma) {
        this.gamma = gamma;
    }

    public void setIdeal(netTemplate ideal) {
        this.ideal = ideal;
    }

    public void setTocompare(netTemplate tocompare) {
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
            for (conTemplate comparing : tocomp) {
                if (sameConnection(ideal, comparing) == 1) {
                    foundCon++;
                } else if (sameConnection(ideal, comparing) == 2) {
                    foundCon++;
                    date++;
                }
            }
        }
        if(idealCon != 0 ){
            result = (foundCon / idealCon) * beta + (date/idealCon) * gamma;
        }
        else{
            result = beta + gamma;
        }
        return result;
    }

    private int sameConnection(conTemplate ideal, conTemplate tocomp) {
        int i = 0;
        if ((ideal.getNode1().equals(tocomp.getNode1()) && (ideal.getNode2().equals(tocomp.getNode2())))) i = 1;
        if ((ideal.getNode2().equals(tocomp.getNode1()) && (ideal.getNode1().equals(tocomp.getNode2())))) i = 1;
        if (i == 1) {
            if (ideal.getDate().equals(tocomp.getDate())) i++;
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

    public static void main(String[] args) {
        testOneNetwork one = new testOneNetwork();
        netTemplate ideal = one.getnT();
        netTemplate net = new netTemplate();

//        ideal.addNode("libor");
//        ideal.addNode("boe");
//        ideal.addConnection("libor","boe","2007");
//        net.addNode("libor");
//        net.addNode("boe");
//        net.addNode("ba");
//
//        networkComparator nC = new networkComparator(60,30,10,ideal,net);
//        System.out.println(nC.calculatePositiveScore());



    }
}
