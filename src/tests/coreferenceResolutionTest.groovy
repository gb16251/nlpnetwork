package tests

import infoextraction.coreferenceResolution
import infoextraction.referenceRecorder

/**
 * Created by Gabriela on 18-Aug-17.
 */
class coreferenceResolutionTest extends GroovyTestCase {
    void testCheckNERAssociation() {
        def corefs = new coreferenceResolution(null,"a");
        List<String> entities = Arrays.asList("Libor","Bank of England","UK","United Kingdom");
        corefs.setAllEntities(entities);
        corefs.putReference(new referenceRecorder("UK",2,"a"),new referenceRecorder("United Kingdom",1,"a"))
        assert(corefs.checkNERAssociation(new referenceRecorder("UK",2,"a")).equals("United Kingdom"))
        String s = "US";
        String b = "U.S."

        assert(b.toUpperCase().equals(b))
    }
}
