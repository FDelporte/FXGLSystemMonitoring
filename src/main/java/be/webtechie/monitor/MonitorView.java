package be.webtechie.monitor;

import be.webtechie.monitor.queue.QueueClient;
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

public class MonitorView extends Parent {

    private QueueClient queueClient;
    private String name;
    private DataSource dataSource;

    private boolean isExpanded = false;
    private boolean isAnimating = false;

    private Rectangle bg;

    private double tX;
    private double tY;

    private CollapsedView collapsedView;
    private ExpandedView expandedView;

    public MonitorView(QueueClient queueClient, String name, DataSource dataSource) {
        this.queueClient = queueClient;
        this.name = name;
        this.dataSource = dataSource;

        collapsedView = new CollapsedView(name);
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

        //setCache(true);
        //setCacheHint(CacheHint.);

        getChildren().addAll(bg, collapsedView);
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

    private class CollapsedView extends Parent {
        private Text title;

        CollapsedView(String name) {
            title = getUIFactoryService().newText(name, Color.WHITE, 14.0);
            title.setTranslateX(MONITOR_WIDTH / 2.0 - title.getLayoutBounds().getWidth() / 2.0);
            title.setTranslateY(15);

            Text textCPU = new Text("CPU: %");
            textCPU.setFill(Color.WHITE);

            Text textRAM = new Text("RAM: %");
            textRAM.setFill(Color.WHITE);

            Text textDisk = new Text("DISK: %");
            textDisk.setFill(Color.WHITE);

            VBox box = new VBox(5, textCPU, textRAM, textDisk);
            box.setTranslateX(10);
            box.setTranslateY(title.getTranslateY() + 30);

            getChildren().addAll(title, box);
        }
    }

    private class ExpandedView extends Parent {
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
    }
}
