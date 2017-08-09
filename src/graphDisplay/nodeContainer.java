package graphDisplay;

/**
 * Created by Gabriela on 09-Aug-17.
 */
public class nodeContainer {
    private String name = "";
    private String occurences = "";
    private int size;
    private int colour;

    public nodeContainer(String name,String occurences){
        this.name = name;
        this.occurences = occurences;
    }
    public String toString(){
        return this.name;
    }
}
