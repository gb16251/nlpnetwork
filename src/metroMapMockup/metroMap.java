package metroMapMockup;

import infoextraction.dateChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class metroMap {
    private List<metroLine> lines = new ArrayList<>();
    private List<metroStop> stops = new ArrayList<>();
    private dateChecker dates = new dateChecker();

    public metroMap(List<metroLine> lines,List<metroStop> stops){
        this.lines = lines;
        this.stops = stops;
        manageCoord();
        setYcoord();
    }

    public void setYcoord(){
        for (metroStop s: stops){
            s.setYcoord(getID(s.getLine1()) + getID(s.getLine2()));
        }
    }

    public List<metroLine> getLines() {
        return lines;
    }

    public List<metroStop> getSpecificStops(String string){
        List<metroStop> sp = new ArrayList<>();
        for(metroStop s: stops){
            if(s.getLine1().equals(string) || s.getLine2().equals(string))
                sp.add(s);
        }
        return sp;
    }

    public void addLine(metroLine m){
        lines.add(m);
    }


    public int getID(String s){
        for (metroLine line:lines){
            if(line.getName().equals(s)) return line.getId();
        }
        return -1;
    }

    public void manageCoord(){
        List<metroStop> todelete = new ArrayList<>();
        for(metroStop s: stops){
            if(dates.getYearMonth(s.getYear()) < 0){
                todelete.add(s);
            }
            else{
                s.setCoord(dates.getYearMonth(s.getYear()));
            }
        }
        stops.removeAll(todelete);
    }


    public void printLines(){
//        for (metroLine m : lines){
//            System.out.print(m.getName());
//            System.out.print(": ");
            for(metroStop stop: stops) {
                System.out.print("(");
                System.out.print(stop.getLine1());
                System.out.print(",");
                System.out.print(stop.getLine2());
                System.out.print(",");
                System.out.print(stop.getYear());
                System.out.print(",  ");
                System.out.print(stop.getCoord());
                System.out.print(")  ");
                System.out.println(" ");

            }
//        }
    }

    public int getAverageStops(){
        int sum = 0;
        int metrolines = 0;
        for (metroLine m : lines){
            if(stops.size() > 0){
                sum += stops.size();
                metrolines++;
            }
        }
        return sum/metrolines;
    }

}
