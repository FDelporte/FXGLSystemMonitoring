package be.webtechie.monitor;

import be.webtechie.monitor.data.Reading;
import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

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
        collapsedView.onReading(reading);
        expandedView.onReading(reading);
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
        private Text textDisk = new Text("DISK: %");

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
            textDisk.setFill(Color.WHITE);

            VBox box = new VBox(5, textCPU, textRAM, textDisk);
            box.setTranslateX(10);
            box.setTranslateY(titleName.getTranslateY() + 20);
            box.setTranslateY(titleIpAddress.getTranslateY() + 40);

            getChildren().addAll(titleName, titleIpAddress, box);
        }

        @Override
        public void onReading(Reading reading) {
            textCPU.setText(String.format("CPU: %.2f %s", reading.getCpuUsage(), "%"));
            textRAM.setText(String.format("RAM: %d", reading.getVirtualMemory().getUsed()));
            textDisk.setText(String.format("PACKETS: %d", reading.getNetwork().getPacketsReceived()));
        }
    }

    private class ExpandedView extends Parent implements ReadingHandler {
        private Text title;

        ExpandedView(String name) {
            title = getUIFactoryService().newText(name, Color.WHITE, 14.0 * MONITOR_SCALE_RATIO);
            title.setTranslateX(APP_WIDTH / 2.0 - title.getLayoutBounds().getWidth() / 2.0);
            title.setTranslateY(50);

            LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());

            XYChart.Series<Number, Number> data = new XYChart.Series<>();
            data.setName("Example: Dynamic CPU data. Two more charts / controls can be added here");

            data.getData().add(new XYChart.Data<>(1, 25));
            data.getData().add(new XYChart.Data<>(2, 55));
            data.getData().add(new XYChart.Data<>(3, 33));
            data.getData().add(new XYChart.Data<>(4, 66));
            data.getData().add(new XYChart.Data<>(5, 89));

            lineChart.getData().add(data);

            lineChart.setTranslateX(20);
            lineChart.setTranslateY(title.getTranslateY() + 80);

            getChildren().addAll(title, lineChart);
        }

        @Override
        public void onReading(Reading reading) {
            // TODO
        }
    }
}
