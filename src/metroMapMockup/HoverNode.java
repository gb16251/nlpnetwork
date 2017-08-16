package metroMapMockup;

/**
 * Created by Gabriela on 16-Aug-17.
 */

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/** a node which displays a value on hover, but is otherwise empty */
class HoverNode extends StackPane {
    HoverNode(String s) {
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
