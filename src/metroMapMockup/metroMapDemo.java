package metroMapMockup;

/**
 * Created by Gabriela on 15-Aug-17.
 */
import javafx.application.Application;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

/** Displays a LineChart which displays the value of a plotted Node when you hover over the Node. */
public class metroMapDemo extends Application {
    @SuppressWarnings("unchecked")


//    @Override public void start(Stage stage) {
//        final LineChart lineChart =
//                new LineChart(new NumberAxis(2005,2017.0,1), new NumberAxis());
//        controller con = new controller();
//        for(metroLine m: con.getMap().getLines()){
//            XYChart.Series series = new XYChart.Series();
//            series.setName(m.getName());
//            List<metroStop> s = con.getMap().getSpecificStops(m.getName());
//            for(metroStop stop :s ){
//                series.getData().add(new XYChart.Data(stop.getCoord(),Math.floor(stop.getYcoord())));
//            }
//            lineChart.getData().add(series);
//        }
//
//        lineChart.setBackground(null);
//        lineChart.setCursor(Cursor.CROSSHAIR);
//        lineChart.setTitle("Metro Map");
//        stage.setScene(new Scene(lineChart, 500, 400));
//        stage.show();
//    }


    @Override public void start(Stage stage) {
        final LineChart lineChart =
                new LineChart(new NumberAxis(2005,2017.0,1), new NumberAxis());
        controller con = new controller();
        for (metroLine m : con.getMap().getLines()) {
            final ObservableList<XYChart.Data<Integer, Integer>> dataset = FXCollections.observableArrayList();
            XYChart.Series series = new XYChart.Series();
            series.setName(m.getName());
            List<metroStop> s = con.getMap().getSpecificStops(m.getName());
            for (metroStop stop : s) {
                final XYChart.Data<Integer, Integer> data = new XYChart.Data<>((int) stop.getCoord(), (int) Math.floor(stop.getYcoord()));
                data.setNode(
                        new HoverNode(stop.getLine1() + " + " + stop.getLine2() + " in " + stop.getYear())
                );
                dataset.add(data);
            }
            lineChart.getData().add(new XYChart.Series(
                    m.getName(),
                    FXCollections.observableArrayList(dataset)));
        }
//            final LineChart lineChart =
//                    new LineChart(new NumberAxis(2005, 2017.0, 1), new NumberAxis(),
//                            FXCollections.observableArrayList(
//                                    new XYChart.Series(
//                                            "MetroMap",
//                                            FXCollections.observableArrayList(dataset))));

            lineChart.setBackground(null);
            lineChart.setCursor(Cursor.CROSSHAIR);
            lineChart.setTitle("Metro Map");
            stage.setScene(new Scene(lineChart, 900, 900));
            stage.show();
    }


    public static void main(String[] args) { launch(args); }
}