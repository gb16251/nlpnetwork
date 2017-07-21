import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gabriela on 18-Jul-17.
 */
public class libor1ModelNetwork {
    networkTemplate nt = new networkTemplate();
    List<String> libor1 = Arrays.asList("Bank of England","Libor","BBC Panorama","UK","Barclays","Bob Diamond",
            "Paul Tucker","Treasury select committee","Mark Dearlove","Peter Johnson","Chris Philp","Serious Fraud Office",
            "Jay Merchant","Alex Pabon"," Ryan Reich"," Stelios Contogoulas");


    public void createlibor1Model(){
        for (String s: libor1){
            entityNode en = new entityNode();
            en.setNodeName(s);
            nt.addNode(en);
        }
        createlibor1Connections();
    }

    private void createlibor1Connections(){
        List<nodeConnection> ns = new ArrayList<>();
        ns.add(new nodeConnection().setUpConnection("","Mark Dearlove"));
        ns.add(new nodeConnection().setUpConnection("","Barclays"));
        ns.add(new nodeConnection().setUpConnection("","Mark Dearlove"));
        ns.add(new nodeConnection().setUpConnection("","Bank of England"));

        ns.add(new nodeConnection().setUpConnection("","BBC Panorama"));
        nt.addNode(new entityNode().createNode("Libor",ns));



    }



}
