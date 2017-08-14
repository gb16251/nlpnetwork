package tests

import infoextraction.connex

/**
 * Created by Gabriela on 14-Aug-17.
 */
class connexTest extends GroovyTestCase {
    def con = new connex();
    void testMatchesLink() {
        con.setConnection(1,2,"","","",10)
        assert (!con.matchesLink(1,1,""))
        assert (!con.matchesLink(1,2,"2007"))
        assert (con.matchesLink(1,2,""))
        assert (con.matchesLink(2,1,""))
    }

    void testHasNode() {
        con.setConnection(30,20,"2009","","",10)
        assert(con.hasNode(20))
        assert(con.hasNode(30))
        assert(!con.hasNode(10))
        assert(!con.hasNode(2009))



    }
}
