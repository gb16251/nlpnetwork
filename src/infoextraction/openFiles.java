package infoextraction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriela on 14-Jun-17.
 */

//This class opens and reads the files that will be processed further on
public class openFiles {
    private PrintStream ps = new PrintStream(System.out);
    private String path = "D:/openie for conflicts of interest/resrc/set4";
    private String sp = ",";
    private String nl = "\n";

    private List<String> getFiles() {
        List<String> results = new ArrayList<String>();
        File[] files = new File(path).listFiles();
        if (files == null){
            throw new Error("No files in the given folder. Please check that you entered a valid directory and try again!");
        }
        for (File file : files) {
            if (file.isFile()) {
                results.add(path + "/" + file.getName());
            }
        }
//        ps.print(results);
        return results;
    }

    public List<fileRecorder> getText(){
        List <String> results = getFiles();
        List <String> fileOutputs = new ArrayList<>();
        List<fileRecorder> fileRec= new ArrayList<>();

        for (String file: results){
            fileOutputs.add(readFile(file));
            fileRec.add(new fileRecorder(file, readFile(file)));
        }
//        for (String s : fileOutputs){ ps.println(s);}

        return fileRec;
    }

//    Return a string for each file
    private String readFile(String filename){
       String fileoutput = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
//            ps.println(filename);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                fileoutput += sCurrentLine;
                fileoutput += " ";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        fileoutput += " ";
//        ps.println(fileoutput);
        return fileoutput;
    }
    public static void main(String[] args) {
        openFiles fileopener = new openFiles();
        fileopener.getText();

    }
}


