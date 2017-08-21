package tests

import infoextraction.coreferenceResolution
import infoextraction.referenceRecorder

/**
 * Created by Gabriela on 18-Aug-17.
 */
class coreferenceResolutionTest extends GroovyTestCase {
    void testCheckNERAssociation() {
        def corefs = new coreferenceResolution(null,"a");
        List<String> entities = Arrays.asList("Libor","Bank of England","United Kingdom","UK","United States","BE","Royal Bank of Scotland","RBS","Royal Bank",
        "British Bankers Association","British Bankers ' Association");
        corefs.setAllEntities(entities);
        corefs.getAbbreviations();
        corefs.putReference(new referenceRecorder("UK",2,"a"),new referenceRecorder("United Kingdom",1,"a"))
        assert(corefs.checkNERAssociation(new referenceRecorder("UK",2,"a")).equals("United Kingdom"))
        String s = "US";
        String b = "U.S."
        assert(b.toUpperCase().equals(b))

        corefs.putReference(new referenceRecorder("US",2,"a"),new referenceRecorder("United States",1,"a"))
        assert(corefs.isAbbrev("U.S.").equals("United States"))
        assert (corefs.removeStopWords("Bank of England").equals("Bank England"))
        assert (corefs.abbrevWithNoPrep("BE").equals("Bank of England"))
        assert (corefs.abbrevWithNoPrep("RBS").equals("Royal Bank of Scotland"))
        assert(corefs.checkIfExists("Royal Bank").equals("Royal Bank of Scotland"))
        assert (corefs.getEditDistanceMatch("British Bankers ' Association"). equals("British Bankers Association"))

    }
}
