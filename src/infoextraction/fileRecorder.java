package infoextraction;

/**
 * Created by Gabriela on 01-Aug-17.
 */
public class fileRecorder {
    private String title = "";
    private String fileOutput;


    public fileRecorder(String title, String fileOutput){
        this.title = title;
        this.fileOutput = fileOutput;
    }
    public String getFileOutput() {
        return fileOutput;
    }

    public String getTitle() {
        return title;
    }
}
