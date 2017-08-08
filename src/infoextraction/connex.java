package infoextraction;

/**
 * Created by Gabriela on 21-Jul-17.
 */
public class connex {
    private int first;
    private int second;
    private String date ="";
    private String rel = "";
    private String filename = "";


    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public String getDate() {
        return date;
    }

    public String getRel() {
        return rel;
    }

    public String getFilename() {
        return filename;
    }

    public void setConnection(int first, int second, String date, String filename, String rel){
        this.first = first;
        this.second = second;
        this.date += date;
        this.rel += rel;
        this.filename += filename;
    }

    public boolean matchesLink(int first, int second,String date){
        if ((first == this.first && second == this.second)|| (second == this.first && first == this.second)){
            if (date.equals(this.date)){
                return true;
            }
        }
        return false;
    }
    public boolean hasNode(int i){
        if(first == i || second == i){
            return true;
        }
        return false;
    }
}
