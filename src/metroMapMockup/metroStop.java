package metroMapMockup;

import edu.stanford.nlp.util.ArraySet;

import java.util.Set;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class metroStop {
    private String year;
    private String line1;
    private String line2;
    private String relationship;
    private double ycoord;
    private double coord;
    private int id;
    private int newId = -1;
    private Set<String> entities = new ArraySet<>();
    private String added;

    public void setAdded(String added) {
        this.added = added;
    }

    public String getAdded() {
        return added;
    }

    public int getNewId() {
        return newId;
    }

    public void setNewId(int newId) {
        this.newId = newId;
    }

    public int getId() {
        return id;
    }

    public String getRelationship() {
        return relationship;
    }


    public double getCoord() {
        return coord;
    }

    public void setYcoord(double ycoord) {
        this.ycoord = ycoord;
    }

    public double getYcoord() {
        return ycoord;
    }

    public void setCoord(double coord) {
        this.coord = coord;
    }

    public metroStop(String line1, String line2, String year,String relationship, int id){
        this.year = year;
        this.line1 = line1;
        this.line2 = line2;
        this.relationship = relationship;
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }
    public String getLine2() {
        return line2;
    }


    public String getYear() {
        return year;
    }
}
