package infoextraction;

/**
 * Created by Gabriela on 21-Jul-17.
 */
public class connex {
    private int first;
    private int second;
    private String date ="";

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public String getDate() {
        return date;
    }

    public void setConnection(int first, int second, String date){
        this.first = first;
        this.second = second;
        this.date += date;
    }

    public boolean matchesLink(int first, int second,String date){
        if ((first == this.first && second == this.second)|| (second == this.first && first == this.second)){
            if (date.equals(this.date)){
                return true;
            }
        }
        return false;
    }
}
