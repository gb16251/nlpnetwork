package evaluationVisualtizer; /**
 * Created by Gabriela on 26-Jul-17.
 */

import infoextraction.NLPPipeline;
import infoextraction.inforun;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class charts extends Application {


    @Override public void start(Stage stage) {
        stage.setTitle("Optimization");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Gamma Value");
        yAxis.setLabel("Average score");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Evaluation Stage: 1");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("Evaluation template");
        //populating the series with data
        String[] arr = new String[]{"resrc/Yanukovych","Yanukovych"};
        inforun pipe = new inforun(arr);
        int[] results =  pipe.getNlp().evalNetwork();
        for (int i = 0; i<=100;i++){
            series.getData().add(new XYChart.Data(i,results[i]));
        }

        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().add(series);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String args) {
        launch(args);
    }
}
