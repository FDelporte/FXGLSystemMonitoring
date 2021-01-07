package be.webtechie.monitor.view;

import be.webtechie.monitor.data.Reading;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

import static be.webtechie.monitor.Config.MONITOR_SCALE_RATIO;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

class CanvasLineChart extends VBox implements ReadingHandler {

    private final int PIXELS_PER_UNIT_X = 10;
    private final int PIXELS_PER_UNIT_Y;
    private final int MAX_ITEMS;

    private final Color color;
    private final Function<Reading, Double> dataExtractor;

    private final Deque<Double> buffer;

    private double oldX = -1;
    private double oldY = -1;

    private final GraphicsContext g;

    CanvasLineChart(String name, double width, double height, Color color, Function<Reading, Double> dataExtractor) {
        this.color = color;
        this.dataExtractor = dataExtractor;

        MAX_ITEMS = (int) (width / PIXELS_PER_UNIT_X);
        buffer = new ArrayDeque<>(MAX_ITEMS);

        PIXELS_PER_UNIT_Y = (int) (height / 100.0);

        Canvas canvas = new Canvas(width, height);
        canvas.setTranslateX(15);

        g = canvas.getGraphicsContext2D();

        setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(canvas, getUIFactoryService().newText(name, Color.WHITE, 6.0 * MONITOR_SCALE_RATIO));
    }

    @Override
    public void onReading(Reading reading) {
        double value = dataExtractor.apply(reading);

        buffer.addLast(value);

        if (buffer.size() > MAX_ITEMS) {
            buffer.removeFirst();
        }

        render();
    }

    private void render() {
        g.setFill(Color.BLACK);
        g.clearRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

        g.setStroke(Color.WHITE);
        g.setLineWidth(0.5);
        g.setFill(Color.WHITE);

        for (int y = 100; y >= 0; y -= 10) {
            g.strokeLine(0, y * PIXELS_PER_UNIT_Y, g.getCanvas().getWidth(), y * PIXELS_PER_UNIT_Y);
            g.fillText("" + (100 - y), 0, y * PIXELS_PER_UNIT_Y);
        }

        g.setStroke(color);
        g.setLineWidth(2.5);

        buffer.forEach(dataY -> {
            // invert
            double y = 100 - dataY;

            if (oldY > -1) {
                g.strokeLine(
                        25 + oldX * PIXELS_PER_UNIT_X,
                        oldY * PIXELS_PER_UNIT_Y,
                        25 + (oldX + 1) * PIXELS_PER_UNIT_X,
                        y * PIXELS_PER_UNIT_Y
                );
            }

            oldX = oldX + 1;
            oldY = y;
        });

        oldX = -1;
        oldY = -1;
    }
}
