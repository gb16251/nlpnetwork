package metroMapMockup;

import infoextraction.dateChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class metroLine {
    private int id;
    private String name;
    private List<metroStop> stops = new ArrayList<>();
    private double[] sortedYears;
    private List<Integer> added = new ArrayList<>();


    public void PrintSortedYears(){
        for(int i = 0; i<sortedYears.length;i++){
            System.out.println(sortedYears[i]);
        }
    }

    public double[] getSortedYears() {
        return sortedYears;
    }

    public metroStop searchByYear(double a ){
        for (metroStop stop: stops){
            if(Double.compare(stop.getCoord(),a) == 0){
                if(!added.contains(stop.getId())) {
                    stop.setAdded(name);
                    added.add(stop.getId());
                    return stop;
                }

            }
        }
        System.out.println(a);
        return null;
    }

    public void sortList() {
        sortedYears = new double[stops.size()];
        int i = 0;
        for (metroStop Stop : stops) {
            sortedYears[i] = Stop.getCoord();
            i++;
        }
        Arrays.sort(sortedYears);

    }


//    public void manageCoord(){
//        List<metroStop> todelete = new ArrayList<>();
//        for(metroStop s: stops){
//            if(dates.getYearMonth(s.getYear()) < 0){
//                todelete.add(s);
//            }
//            else{
//                s.setCoord(dates.getYearMonth(s.getYear()));
//            }
//        }
//        stops.removeAll(todelete);
//    }

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
