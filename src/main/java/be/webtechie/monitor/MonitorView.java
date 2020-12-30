package be.webtechie.monitor;

import be.webtechie.monitor.data.Reading;
import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

        bg = new Rectangle(MONITOR_WIDTH, MONITOR_HEIGHT);
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

        private Text textCPU = new Text("CPU: %");
        private Text textRAM = new Text("RAM: %");
        private Text textPackets = new Text("DISK: %");

        CollapsedView(String name, String ipAddress) {
            titleName = getUIFactoryService().newText(name, Color.WHITE, 12.0);
            titleName.setTranslateX(MONITOR_WIDTH / 2.0 - titleName.getLayoutBounds().getWidth() / 2.0);
            titleName.setTranslateY(15);

            titleIpAddress = getUIFactoryService().newText(ipAddress, Color.WHITE, 12.0);
            titleIpAddress.setTranslateX(MONITOR_WIDTH / 2.0 - titleIpAddress.getLayoutBounds().getWidth() / 2.0);
            titleIpAddress.setTranslateY(30);

            // TODO: extract into a custom Text class
            // TODO: color code values, e.g. 75%+ RED, 50%+ ORANGE, 25%+ YELLOW, 0%+ GREEN
            textCPU.setFill(Color.WHITE);
            textRAM.setFill(Color.WHITE);
            textPackets.setFill(Color.WHITE);

            VBox box = new VBox(5, textCPU, textRAM, textPackets);
            box.setTranslateX(10);
            box.setTranslateY(titleName.getTranslateY() + 20);
            box.setTranslateY(titleIpAddress.getTranslateY() + 40);

            getChildren().addAll(titleName, titleIpAddress, box);
        }

        @Override
        public void onReading(Reading reading) {
            textCPU.setText(String.format("CPU: %.2f %s", reading.getCpuUsage(), "%"));
            textRAM.setText(String.format("RAM: %d", reading.getVirtualMemory().getUsed()));
            textPackets.setText(String.format("PACKETS: %d", reading.getNetwork().getPacketsReceived()));
        }
    }

    private class ExpandedView extends Parent implements ReadingHandler {

        private List<CanvasLineChart> lineCharts = new ArrayList<>();
        private Text title;

        ExpandedView(String name) {
            title = getUIFactoryService().newText(name, Color.WHITE, 14.0 * MONITOR_SCALE_RATIO);
            title.setTranslateX(APP_WIDTH / 2.0 - title.getLayoutBounds().getWidth() / 2.0);
            title.setTranslateY(50);

            lineCharts.add(new CanvasLineChart("CPU", APP_WIDTH / 1.5, APP_HEIGHT / 1.5, Color.RED, reading -> reading.getCpuUsage()));

            getChildren().addAll(title);

            lineCharts.forEach(getChildren()::add);
        }

        @Override
        public void onReading(Reading reading) {
            lineCharts.forEach(chart -> chart.onReading(reading));
        }
    }

    private static class CanvasLineChart extends VBox implements ReadingHandler {

        private static final int PIXELS_PER_UNIT = 10;
        private static final int MAX_ITEMS = APP_HEIGHT / PIXELS_PER_UNIT;

        private final String name;
        private final Color color;
        private final Function<Reading, Double> dataExtractor;

        private Deque<Double> buffer = new ArrayDeque<>(MAX_ITEMS);

        private double oldX = -1;
        private double oldY = -1;

        private GraphicsContext g;

        CanvasLineChart(String name, double width, double height, Color color, Function<Reading, Double> dataExtractor) {
            this.name = name;
            this.color = color;
            this.dataExtractor = dataExtractor;

            Canvas canvas = new Canvas(width, height);
            g = canvas.getGraphicsContext2D();

            setAlignment(Pos.TOP_CENTER);

            getChildren().addAll(canvas, getUIFactoryService().newText(name, Color.WHITE, 11.0 * MONITOR_SCALE_RATIO));
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
            g.clearRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

            g.setStroke(color);
            g.setLineWidth(2.5);

            buffer.forEach(y -> {
                if (oldY > -1) {
                    g.strokeLine(
                            oldX * PIXELS_PER_UNIT,
                            oldY * 6,
                            (oldX + 1) * PIXELS_PER_UNIT,
                            y * 6
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
