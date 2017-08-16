package metroMapMockup;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class metroStop {
    private String year;
    private String line1;
    private String line2;
    private double ycoord;
    private double coord;

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

    public metroStop(String line1, String line2, String year){
        this.year = year;
        this.line1 = line1;
        this.line2 = line2;
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
