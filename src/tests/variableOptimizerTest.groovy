package tests

import infoextraction.variableOptimizer
import infoextraction.variableTriples

/**
 * Created by Gabriela on 28-Jul-17.
 */
class variableOptimizerTest extends GroovyTestCase {
    void testOptimize() {
        test1()
    }
    void test1(){
        def vo = new variableOptimizer()
        def varlist = vo.getVt();
        vo.optimize()
        assert(varlist.size() == 10302)
        for(int i = 0; i<10302;i++){
            assert(varlist.get(i).getA() + varlist.get(i).getB() + varlist.get(i).getC() == 100)
        }
    }
}
