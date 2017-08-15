package metroMapMockup;

import infoextraction.graphDbPipeline;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class controller {
    graphDbPipeline db = new graphDbPipeline();

    public static void main(String[] args) {
        controller con = new controller();
        con.control();
    }

    public void control(){
        db.readDatabase();
        metroMap map = db.getMetroMap();
        map.manageMap();
        map.printLines();
        System.out.println(map.getAverageStops());
    }
}
