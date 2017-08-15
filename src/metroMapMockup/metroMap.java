package metroMapMockup;

import infoextraction.dateChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class metroMap {
    private List<metroLine> lines = new ArrayList<>();

    public void addLine(metroLine m){
        lines.add(m);
    }

    public void manageMap(){
        manageCoord();
        manageLines();
    }


    public void manageLines(){
        List<metroLine> todelete = new ArrayList<>();
        for(metroLine l: lines){
            if(l.getStops().size()==0){
                todelete.add(l);
            }
        }
        lines.removeAll(todelete);
    }
    public void manageCoord(){
        for (metroLine l: lines){
            l.manageCoord();
        }
    }

    public void printLines(){
        for (metroLine m : lines){
            System.out.print(m.getName());
            System.out.print(": ");
            for(metroStop stop: m.getStops()) {
                System.out.print("(");
                System.out.print(stop.getLine());
                System.out.print(",");
                System.out.print(stop.getYear());
                System.out.print(",  ");
                System.out.print(stop.getCoord());
                System.out.print(")  ");
            }
            System.out.println(" ");
        }
    }

    public int getAverageStops(){
        int sum = 0;
        int metrolines = 0;
        for (metroLine m : lines){
            if(m.getStops().size() > 0){
                sum += m.getStops().size();
                metrolines++;
            }
        }
        return sum/metrolines;
    }

}
