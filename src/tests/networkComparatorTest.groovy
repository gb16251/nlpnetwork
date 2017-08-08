package tests

import infoextraction.netTemplate
import infoextraction.networkComparator

/**
 * Created by Gabriela on 28-Jul-17.
 */
class networkComparatorTest extends GroovyTestCase {
    void testCalculatePositiveScore() {
        test1()
        test2()
        test3()
        test4()
    }
     void test1(){
        def ideal = new netTemplate()
        def tocompare = new netTemplate()
        ideal.addNode("libor")
        ideal.addNode("bank of england")
        tocompare.addNode("libor")
        tocompare.addNode("bank of england")
         def net = new networkComparator(50,30,20,ideal,tocompare)
         assertToString(String.valueOf(net.calculatePositiveScore()),"100.0")
    }
    void test2(){
        def ideal = new netTemplate()
        def tocompare = new netTemplate()
        ideal.addConnection("libor","boe","2007","","",0)
        tocompare.addNode("libor")
        tocompare.addNode("boe")
        def net = new networkComparator(50,30,20,ideal,tocompare)
        assertToString(String.valueOf(net.calculatePositiveScore()),"50.0")
    }
    void test3() {
        def ideal = new netTemplate()
        def tocompare = new netTemplate()
        ideal.addConnection("raul", "ana", "October 2012","","",0)
        tocompare.addConnection("ana", "raul", "November 2013","","",0)
        tocompare.addNode("michel")
        def net = new networkComparator(50, 40, 10, ideal, tocompare)
        assertToString(String.valueOf(net.calculatePositiveScore()), "90.0")
    }

        void test4(){
            //            mary :0
//            john: 33
//            gary: 33 + 33/2 + 0 = 49.5
//            mark : 33 + 33 + 34/2 = 83
//            rose : 33 + 33 + 34/2 = 83
//            jack : 33 + 33 + 0 = 66
            def ideal = new netTemplate()
            def tocompare = new netTemplate()
            ideal.addConnection("mary","john","","","",0)
            ideal.addConnection("john","gary","","","",0)
            ideal.addConnection("gary","mark","2009","","",0)
            ideal.addConnection("mark","rose","2003","","",0)
            ideal.addConnection("rose","jack", "july","","",0)

            tocompare.addNode("john")
            tocompare.addConnection("gary","mark","","","",0)
            tocompare.addConnection("mark","rose","2003","","",0)
            tocompare.addConnection("jack","rose", "2009","","",0)

            def net = new networkComparator(33,33,34,ideal,tocompare)
            System.out.print(net.calculatePositiveScore())

            assert(net.calculatePositiveScore()>= 52)
            assert(net.calculatePositiveScore()<= 53)

        }

}
