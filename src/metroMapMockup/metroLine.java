package metroMapMockup;

import infoextraction.dateChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class metroLine {
    private int id;
    private String name;
    private List<metroStop> stops = new ArrayList<>();
    private dateChecker dates = new dateChecker();


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

    public String getName() {
        return name;
    }

    public void addStop(metroStop stop){
        stops.add(stop);
    }
    public metroLine(int id,String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public List<metroStop> getStops() {
        return stops;
    }
}
