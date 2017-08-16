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

/** Displays a LineChart which displays the value of a plotted Node when you hover over the Node. */
public class LineChartWithHover extends Application {
    @SuppressWarnings("unchecked")
    @Override public void start(Stage stage) {
        final LineChart lineChart = new LineChart(
                new NumberAxis(), new NumberAxis(),
                FXCollections.observableArrayList(
                        new XYChart.Series(
                                "MetroMap",
                                FXCollections.observableArrayList(
                                        plot(23, 14, 15, 24, 34, 36, 22, 45, 43, 17, 29, 25)
                                )
                        )
                )
        );
        lineChart.setCursor(Cursor.CROSSHAIR);

        lineChart.setTitle("Stock Monitoring, 2013");

        stage.setScene(new Scene(lineChart, 500, 400));
        stage.show();
    }



//    public ObservableList<XYChart.Data<Double,Double>> plotMe(){
//        final ObservableList<XYChart.Data<Integer, Integer>> dataset = FXCollections.observableArrayList();
//
//    }
    /** @return plotted y values for monotonically increasing integer x values, starting from x=1 */
    public ObservableList<XYChart.Data<Integer, Integer>> plot(int... y) {
        final ObservableList<XYChart.Data<Integer, Integer>> dataset = FXCollections.observableArrayList();
        int i = 0;
        int m = 0;
        int j = 0;
        while (i < y.length) {
            final XYChart.Data<Integer, Integer> data = new XYChart.Data<>(m++,j++);
            data.setNode(
                    new HoveredThresholdNode("bla"
                    )
            );

            dataset.add(data);
            i++;
        }

        return dataset;
    }

    /** a node which displays a value on hover, but is otherwise empty */
    class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(String s) {
            setPrefSize(15, 15);

            final Label label = createDataThresholdLabel(s);

            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    getChildren().setAll(label);
                    setCursor(Cursor.NONE);
                    toFront();
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    getChildren().clear();
                    setCursor(Cursor.CROSSHAIR);
                }
            });
        }

        private Label createDataThresholdLabel(String value) {
            final Label label = new Label(value + "");
            label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
            label.setTextFill(Color.FORESTGREEN);
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }

    public static void main(String[] args) { launch(args); }

}