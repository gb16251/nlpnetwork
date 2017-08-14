package graphDisplay;

/**
 * Created by Gabriela on 10-Aug-17.
 */
public class colourManager {
    public static void main(String args[]) {
        colourManager colours = new colourManager();
    }


    public int createSineWaves(String s){
        int i = Integer.parseInt(s);
        double frequency = .3;
//        for (int i = 0; i<32;i++){
//            System.out.print((frequency*i)*128 + 127);
//            System.out.print(" ");
//            System.out.println(Math.sin(frequency*i));
//        }
        return (int)(Math.sin(frequency*i)*128+127);
    }

}
