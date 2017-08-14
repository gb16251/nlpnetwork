package tests

import infoextraction.acronymManager

/**
 * Created by Gabriela on 14-Aug-17.
 */
class acronymManagerTest extends GroovyTestCase {
    def am = new acronymManager();
    void testSplitString() {
        assert(am.splitString("march") == null);
        assert(am.splitString("march.")== null);
        assert(am.splitString("ma rch.")== null);
        assert(am.splitString("m.a.r.c.h").equals("march"));
        assert(am.splitString("m a r c h").equals("march"));
        assert(am.splitString("Serious Fraud Office").equals("SFO"));
    }

    void testisPossibleAbbrev(){
        assert(!am.isPossibleAbbrev("USA","AMERICA"))
        assert(am.isPossibleAbbrev("USA","US"))
        assert(!am.isPossibleAbbrev("",""))
        assert(am.isPossibleAbbrev("BE","BOE"))
        assert(!am.isPossibleAbbrev("A","AA"))
    }
}
