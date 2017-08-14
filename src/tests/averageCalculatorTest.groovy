package tests

import infoextraction.averageCalculator

/**
 * Created by Gabriela on 14-Aug-17.
 */
class averageCalculatorTest extends GroovyTestCase {
    def ac = new averageCalculator();

    void testCalculator(){
        for (int i = 0; i<= 100; i++){
            ac.addItem(i,100 - i);
        }
        int[] array = ac.getResults();
        for (int j = 0; j<=100;j++){
            assert(array[j] == 100 - j) ;
        }

        for (int i = 0; i<= 100; i++){
            ac.addItem(i,0);
        }
        int[] array2 = ac.getResults();

        for (int j = 0; j<=100;j++){
            assert(array2[j] <= (100 - j)/2 + 1) ;
            assert(array2[j] >= (100 - j)/2 - 1) ;
        }
    }
}
