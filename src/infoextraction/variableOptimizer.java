package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 19-Jul-17.
 */
public class variableOptimizer {
    private List <variableTriples> vt = new ArrayList<>();

    public List<variableTriples> getVt() {
        return vt;
    }

    public variableOptimizer(){
        optimize();
    }
    public void optimize(){
        for (int i = 0; i<= 100;i++){
            for (int j = 100 - i;j>=0;j--){
                int k = 100 - (j+i);
                vt.add(new variableTriples(i,j,k));
            }
        }
    }
    public static void main(String[] args) {
        variableOptimizer vo = new variableOptimizer();
        vo.optimize();
    }

}
