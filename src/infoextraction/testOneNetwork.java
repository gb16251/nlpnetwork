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
        nT.addConnection("Bank of England","Libor","","","",0);
        nT.addConnection("Bank of England","BBC Panorama","","","",0);
        nT.addConnection("Libor","BBC Panorama","","","",0);
        nT.addConnection("Bank of England","Libor","","","",0);
        nT.addConnection("Bank of England","UK","","","",0);
        nT.addConnection("Bank of England","Barclays","2012","","",0);
        nT.addConnection("Bank of England","Paul Tucker","2012","","",0);
        nT.addConnection("Bank of England","Bob Diamond","2012","","",0);
        nT.addConnection("Bank of England","Treasury","2012","","",0);
        nT.addConnection("Bob Diamond","Paul Tucker","2012","","",0);
        nT.addConnection("Bob Diamond","Barclays","2012","","",0);
        nT.addConnection("Paul Tucker","Barclays","2012","","",0);
        nT.addConnection("Paul Tucker","Treasury","2012","","",0);
        nT.addConnection("Bob Diamond","Treasury","2012","","",0);
        nT.addConnection("Barclays","Treasury","2012","","",0);
        nT.addConnection("Barclays","Mark Dearlove","","","",0);
        nT.addConnection("Barclays","Libor","","","",0);
        nT.addConnection("Barclays","Peter Johnson","","","",0);
        nT.addConnection("Libor","Mark Dearlove","","","",0);
        nT.addConnection("Peter Johnson","Mark Dearlove","","","",0);
        nT.addConnection("Peter Johnson","Libor","","","",0);
        nT.addConnection("UK","Bank of England","","","",0);
        nT.addConnection("Bank of England","Mark Dearlove","","","",0);
        nT.addConnection("Peter Johnson","Mark Dearlove","October 2008","","",0);
        nT.addConnection("Paul Tucker","Mark Dearlove","October 2008","","",0);
        nT.addConnection("Paul Tucker","Bank of England","October 2008","","",0);
        nT.addConnection("Paul Tucker","Barclays","October 2008","","",0);
        nT.addConnection("Paul Tucker","Bob Diamond","October 2008","","",0);
        nT.addConnection("Peter Johnson","Paul Tucker","October 2008","","",0);
        nT.addConnection("Peter Johnson","Bank of England","October 2008","","",0);
        nT.addConnection("Peter Johnson","Barclays","October 2008","","",0);
        nT.addConnection("Peter Johnson","Bob Diamond","October 2008","","",0);
        nT.addConnection("Paul Tucker","Bank of England","October 2008","","",0);
        nT.addConnection("Paul Tucker","Barclays","October 2008","","",0);
        nT.addConnection("Paul Tucker","Bob Diamond","October 2008","","",0);
        nT.addConnection("Bank of England","Barclays","October 2008","","",0);
        nT.addConnection("Bank of England","Bob Diamond","October 2008","","",0);
        nT.addConnection("Barclays","Bob Diamond","October 2008","","",0);
        nT.addConnection("Chris Philip","Treasury","October 2008","","",0);
        nT.addConnection("Paul Tucker","Bob Diamond","2012","","",0);
        nT.addConnection("Treasury","Bob Diamond","2012","","",0);
        nT.addConnection("Paul Tucker","Treasury","2012","","",0);
        nT.addConnection("Peter Johnson","Barclays","","","",0);
        nT.addConnection("Peter Johnson","Libor","","","",0);
        nT.addConnection("Barclays","Libor","","","",0);
        nT.addConnection("Serious Fraud Office","Barclays","","","",0);
        nT.addConnection("Serious Fraud Office","BBC Panorama","","","",0);
        nT.addConnection("Bank of England","Libor","","","",0);
        nT.addConnection("Bank of England","UK","","","",0);
        nT.addConnection("Libor","UK","","","",0);
        nT.addConnection("Bank of England","Serious Fraud Office","","","",0);
        nT.addConnection("Bank of England","Libor","","","",0);
        nT.addConnection("Serious Fraud Office","Libor","","","",0);

    }
}
