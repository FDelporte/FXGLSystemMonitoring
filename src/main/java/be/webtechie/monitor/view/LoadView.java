package be.webtechie.monitor.view;

import be.webtechie.monitor.data.Reading;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.function.Function;

import static be.webtechie.monitor.Config.MONITOR_HEIGHT;

class LoadView extends VBox implements ReadingHandler {

    private final Function<Reading, Double> dataExtractor;

    private final String name;
    private final Text text;
    private final Rectangle loadRect = new Rectangle(20, MONITOR_HEIGHT / 2.0);

    LoadView(String name, Function<Reading, Double> dataExtractor) {
        this.dataExtractor = dataExtractor;
        this.name = name;

        text = new Text(name);
        text.setFill(Color.WHITE);

        setAlignment(Pos.BOTTOM_CENTER);
        setPrefWidth(80);
        setPrefHeight(loadRect.getHeight() + 20.0);

        getChildren().addAll(loadRect, text);
    }

    @Override
    public void onReading(Reading reading) {
        var value = dataExtractor.apply(reading);

        loadRect.setHeight((value / 100.0) * MONITOR_HEIGHT / 2.0);
        loadRect.setFill(getColor(value));

        text.setText(String.format(name + ": %.2f %s", value, "%"));
    }

    // color code values, e.g. 75%+ RED, 50%+ ORANGE, 25%+ YELLOW, 0%+ GREEN
    private Color getColor(double value) {
        if (value >= 75) {
            return Color.RED;
        } else if (value >= 50) {
            return Color.ORANGE;
        } else if (value >= 25) {
            return Color.YELLOW;
        }

        return Color.GREEN;
    }
}
