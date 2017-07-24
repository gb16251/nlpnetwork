package infoextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 19-Jul-17.
 */
public class entitiesCollector {
    List<String> ents = new ArrayList<>();

    public void setEnts(List<String> ents) {
        this.ents = ents;
    }
    public void addEnts(List<String> newents){
        ents.addAll(newents);
    }
    public List<String> getEnts(){
        return ents;
    }
}
