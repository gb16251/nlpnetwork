package metroMapMockup;

import infoextraction.graphDbPipeline;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class controller {
    graphDbPipeline db = new graphDbPipeline();
    metroMap map;

    public controller(){
        db.readDatabase();
        map = new metroMap(db.getMetroLines(),db.getMetroStops());
        map.printLines();
    }

    public metroMap getMap() {
        return map;
    }

    public static void main(String[] args) {
        controller con = new controller();
    }

    public void control(){
        db.readDatabase();
        map = new metroMap(db.getMetroLines(),db.getMetroStops());
        map.printLines();
        System.out.println(map.getAverageStops());
    }
}
