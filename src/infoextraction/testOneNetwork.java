package infoextraction;

/**
 * Created by Gabriela on 26-Jul-17.
 */
public class testOneNetwork {
    netTemplate nT = new netTemplate();

    public netTemplate getnT() {
        return nT;
    }

    public testOneNetwork(){
        nT.addConnection("Bank of England","Libor","");
        nT.addConnection("Bank of England","BBC Panorama","");
        nT.addConnection("Libor","BBC Panorama","");
        nT.addConnection("Bank of England","Libor","");
        nT.addConnection("Bank of England","UK","");
        nT.addConnection("Bank of England","Barclays","2012");
        nT.addConnection("Bank of England","Paul Tucker","2012");
        nT.addConnection("Bank of England","Bob Diamond","2012");
        nT.addConnection("Bank of England","Treasury","2012");
        nT.addConnection("Bob Diamond","Paul Tucker","2012");
        nT.addConnection("Bob Diamond","Barclays","2012");
        nT.addConnection("Paul Tucker","Barclays","2012");
        nT.addConnection("Paul Tucker","Treasury","2012");
        nT.addConnection("Bob Diamond","Treasury","2012");
        nT.addConnection("Barclays","Treasury","2012");

        nT.addConnection("Barclays","Mark Dearlove","");
        nT.addConnection("Barclays","Libor","");
        nT.addConnection("Barclays","Peter Johnson","");
        nT.addConnection("Libor","Mark Dearlove","");
        nT.addConnection("Peter Johnson","Mark Dearlove","");
        nT.addConnection("Peter Johnson","Libor","");

        nT.addConnection("UK","Bank of England","");

        nT.addConnection("Bank of England","Mark Dearlove","");

        nT.addConnection("Peter Johnson","Mark Dearlove","October 2008");
        nT.addConnection("Paul Tucker","Mark Dearlove","October 2008");
        nT.addConnection("Paul Tucker","Bank of England","October 2008");
        nT.addConnection("Paul Tucker","Barclays","October 2008");
        nT.addConnection("Paul Tucker","Bob Diamond","October 2008");
        nT.addConnection("Peter Johnson","Paul Tucker","October 2008");
        nT.addConnection("Peter Johnson","Bank of England","October 2008");
        nT.addConnection("Peter Johnson","Barclays","October 2008");
        nT.addConnection("Peter Johnson","Bob Diamond","October 2008");
        nT.addConnection("Paul Tucker","Bank of England","October 2008");
        nT.addConnection("Paul Tucker","Barclays","October 2008");
        nT.addConnection("Paul Tucker","Bob Diamond","October 2008");
        nT.addConnection("Bank of England","Barclays","October 2008");
        nT.addConnection("Bank of England","Bob Diamond","October 2008");
        nT.addConnection("Barclays","Bob Diamond","October 2008");
        nT.addConnection("Chris Philp","Treasury","October 2008");


        nT.addConnection("Paul Tucker","Bob Diamond","2012");
        nT.addConnection("Treasury","Bob Diamond","2012");
        nT.addConnection("Paul Tucker","Treasury","2012");

        nT.addConnection("Peter Johnson","Barclays","");
        nT.addConnection("Peter Johnson","Libor","");
        nT.addConnection("Barclays","Libor","");

        nT.addConnection("Serious Fraud Office","Barclays","");
        nT.addConnection("Serious Fraud Office","BBC Panorama","");

        nT.addConnection("Bank of England","Libor","");
        nT.addConnection("Bank of England","UK","");
        nT.addConnection("Libor","UK","");

        nT.addConnection("Bank of England","Serious Fraud Office","");
        nT.addConnection("Bank of England","Libor","");
        nT.addConnection("Serious Fraud Office","Libor","");

    }
}
