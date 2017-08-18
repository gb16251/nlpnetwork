package utils;

import infoextraction.fileRecorder;
import infoextraction.openFiles;
import metroMapMockup.controller;

import java.util.List;

/**
 * Created by Gabriela on 16-Aug-17.
 */
public class wikiCleaner {
    openFiles filestream = new openFiles();

    public static void main(String[] args) {
        wikiCleaner clean = new wikiCleaner();
        openFiles filestream = new openFiles();
        List<fileRecorder> fileRec = filestream.getText();
        for (fileRecorder file : fileRec){
        }

    }

    private void cleanText(){


    }

}
