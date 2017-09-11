package infoextraction;

/**
 * Created by Gabriela on 26-Jul-17.
 */

//A class for calculating the precision, recall and F1 score for nodes
public class calculatePreRec {
    private netTemplate ideal = new netTemplate();
    private netTemplate tocompare = new netTemplate();
    private int falsePos = 0;
    private int truePos = 0;
    private int falseNeg = 0;
    private float F1 = 0;

    public float getF1() {
        return F1;
    }

    public calculatePreRec(netTemplate ideal, netTemplate tocompare){
        this.ideal = ideal;
        this.tocompare = tocompare;
        getF1score();
    }


    public void getF1score(){
        getTruePositiveNodes();
        getFalsePositivesNodes();
        float prec= (float)(truePos)/(float)(truePos + falsePos);
        float rec = (float)truePos/(float)(truePos + falseNeg);
        F1 = 2/((1/prec) + (1/rec));
    }

    private void getTruePositiveNodes(){
        for (String s: ideal.getNodes()){
            if(tocompare.getNodes().contains(s)){
                truePos++;
            }
            else {
                falseNeg++;
                System.out.println(s);
            }
        }
    }

    private void getFalsePositivesNodes(){
        for(String s:tocompare.getNodes()){
            if(!ideal.getNodes().contains(s)) {
                falsePos++;
            }
        }
    }


}
