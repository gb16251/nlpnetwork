package tests

import infoextraction.dateChecker

/**
 * Created by Gabriela on 28-Jul-17.
 */
class dateCheckerTest extends GroovyTestCase {
    void testCheckDates() {
     test1()
        test2()
    }

    void test1(){
        def dC = new dateChecker()
        List<String> vars = Arrays.asList("october","november","2003","december 2009","october 2008","march","April");
        for (String s: vars){
            assert(ltos(dC.checkDates(s)).contains(s))
        }
    }

    void test2(){
        def dC = new dateChecker()
        List<String> vars = Arrays.asList("90", "donut"," 20,09","2999999","M.arch");
        for (String s: vars){
            assert (dC.checkDates(s).isEmpty())
        }
    }

    String ltos(List<String> datelist){
        String date = "";
        for (String s : datelist){
            date += s;
            date += " ";
        }
        return date;
    }
}
