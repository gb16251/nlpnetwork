package metroMapMockup;

/**
 * Created by Gabriela on 15-Aug-17.
 */
public class metroStop {
    private String year;
    private String thisline;
    private String line;
    private double coord;

    public double getCoord() {
        return coord;
    }

    public void setCoord(double coord) {
        this.coord = coord;
    }

    public metroStop(String thisline, String line, String year){
        this.year = year;
        this.thisline = thisline;
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public String getYear() {
        return year;
    }
}
