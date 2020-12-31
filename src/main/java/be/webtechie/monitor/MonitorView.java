package be.webtechie.monitor;

import be.webtechie.monitor.data.Reading;
import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

import static be.webtechie.monitor.Config.*;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class MonitorView extends Parent implements ReadingHandler {

    private String name;
    private String ipAddress;

    private boolean isExpanded = false;
    private boolean isAnimating = false;

    private Rectangle bg;

    private double tX;
    private double tY;

    private CollapsedView collapsedView;
    private ExpandedView expandedView;

    public MonitorView(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;

        collapsedView = new CollapsedView(name, ipAddress);
        expandedView = new ExpandedView(name);

        bg = new Rectangle(MONITOR_WIDTH, MONITOR_HEIGHT, Color.rgb(5, 5, 5));
        bg.setStrokeType(StrokeType.INSIDE);
        bg.setStroke(Color.MEDIUMAQUAMARINE);
        bg.setStrokeWidth(2);

        setOnMouseClicked(e -> {
            if (isExpanded) {
                collapse();
            } else {
                expand();
            }
        });

        setCursor(Cursor.HAND);

        getChildren().addAll(bg, collapsedView);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void onReading(Reading reading) {
        var copy = reading.copy();

        collapsedView.onReading(copy);
        expandedView.onReading(copy);
    }

    public void expand() {
        if (isAnimating)
            return;

        isAnimating = true;

        tX = getTranslateX();
        tY = getTranslateY();

        isExpanded = true;

        getChildren().remove(collapsedView);

        toFront();

        animationBuilder()
                .duration(ANIMATION_DURATION)
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(() -> {
                    getChildren().add(expandedView);
                    isAnimating = false;
                })
                .translate(this)
                .from(new Point2D(getTranslateX(), getTranslateY()))
                .to(new Point2D(0, 0))
                .buildAndPlay();

        animationBuilder()
                .duration(ANIMATION_DURATION)
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .animate(bg.widthProperty())
                .from(MONITOR_WIDTH)
                .to(APP_WIDTH)
                .buildAndPlay();

        animationBuilder()
                .duration(ANIMATION_DURATION)
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .animate(bg.heightProperty())
                .from(MONITOR_HEIGHT)
                .to(APP_HEIGHT)
                .buildAndPlay();
    }

    public void collapse() {
        if (isAnimating)
            return;

        isAnimating = true;
        isExpanded = false;

        getChildren().remove(expandedView);

        animationBuilder()
                .duration(ANIMATION_DURATION)
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(() -> {
                    getChildren().add(collapsedView);
                    isAnimating = false;
                })
                .translate(this)
                .from(new Point2D(0, 0))
                .to(new Point2D(tX, tY))
                .buildAndPlay();

        animationBuilder()
                .duration(ANIMATION_DURATION)
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .animate(bg.widthProperty())
                .to(MONITOR_WIDTH)
                .from(APP_WIDTH)
                .buildAndPlay();

        animationBuilder()
                .duration(ANIMATION_DURATION)
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .animate(bg.heightProperty())
                .to(MONITOR_HEIGHT)
                .from(APP_HEIGHT)
                .buildAndPlay();
    }

    private class CollapsedView extends Parent implements ReadingHandler {
        private Text titleName;
        private Text titleIpAddress;

        private List<LoadView> views = new ArrayList<>();

        CollapsedView(String name, String ipAddress) {
            titleName = getUIFactoryService().newText(name, Color.WHITE, 12.0);
            titleName.setTranslateX(MONITOR_WIDTH / 2.0 - titleName.getLayoutBounds().getWidth() / 2.0);
            titleName.setTranslateY(15);

            titleIpAddress = getUIFactoryService().newText(ipAddress, Color.WHITE, 12.0);
            titleIpAddress.setTranslateX(MONITOR_WIDTH / 2.0 - titleIpAddress.getLayoutBounds().getWidth() / 2.0);
            titleIpAddress.setTranslateY(30);

            views.add(new LoadView("CPU", r -> r.getCpuUsage()));
            views.add(new LoadView("RAM", r -> r.getVirtualMemory().getUsed() * 1.0));

            HBox box = new HBox(5);
            box.setTranslateX(10);
            box.setTranslateY(titleIpAddress.getTranslateY() + 20);
            box.setAlignment(Pos.BOTTOM_LEFT);
            box.setPrefHeight(MONITOR_HEIGHT / 2.5 + 40);

            views.forEach(box.getChildren()::add);

            getChildren().addAll(titleName, titleIpAddress, box);
        }

        @Override
        public void onReading(Reading reading) {
            views.forEach(v -> v.onReading(reading));
        }
    }

    private class LoadView extends VBox implements ReadingHandler {

        private final Function<Reading, Double> dataExtractor;

        private String name;
        private Text text;
        private Rectangle loadRect = new Rectangle(20, MONITOR_HEIGHT / 2.0);

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

            loadRect.setHeight( (value / 100.0) * MONITOR_HEIGHT / 2.0 );
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

    private class ExpandedView extends Parent implements ReadingHandler {

        private List<CanvasLineChart> lineCharts = new ArrayList<>();
        private Text title;

        ExpandedView(String name) {
            title = getUIFactoryService().newText(name, Color.WHITE, 14.0 * MONITOR_SCALE_RATIO);
            title.setTranslateX(APP_WIDTH / 2.0 - title.getLayoutBounds().getWidth() / 2.0);
            title.setTranslateY(50);

            var chart = new CanvasLineChart("CPU", APP_WIDTH / 2.5, APP_HEIGHT / 2.5, Color.RED, reading -> reading.getCpuUsage());
            chart.setTranslateY(title.getTranslateY() + 10);

            lineCharts.add(chart);

            getChildren().addAll(title);

            lineCharts.forEach(getChildren()::add);
        }

        @Override
        public void onReading(Reading reading) {
            lineCharts.forEach(chart -> chart.onReading(reading));
        }
    }

    private static class CanvasLineChart extends VBox implements ReadingHandler {

        private final int PIXELS_PER_UNIT_X = 10;
        private final int PIXELS_PER_UNIT_Y;
        private final int MAX_ITEMS;

        private final String name;
        private final Color color;
        private final Function<Reading, Double> dataExtractor;

        private Deque<Double> buffer;

        private double oldX = -1;
        private double oldY = -1;

        private GraphicsContext g;

        CanvasLineChart(String name, double width, double height, Color color, Function<Reading, Double> dataExtractor) {
            this.name = name;
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
}
