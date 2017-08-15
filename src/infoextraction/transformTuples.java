package infoextraction;

/**
 * Created by Gabriela on 07-Aug-17.
 */
public class transformTuples {
    private String original = "";
    private String artificial = "";

    public String getArtificial() {
        return artificial;
    }

    public String getOriginal() {
        return original;
    }

    public transformTuples(String original, String artificial){
        this.original = original;
        this.artificial = artificial;
    }
}

