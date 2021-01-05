package be.webtechie.monitor.view;

import be.webtechie.monitor.ReadingHandler;
import be.webtechie.monitor.data.Reading;
import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static be.webtechie.monitor.Config.*;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

public class MonitorView extends Parent implements ReadingHandler {

    private final String ipAddress;

    private boolean isExpanded = false;
    private boolean isAnimating = false;

    private final Rectangle bg;

    private double tX;
    private double tY;

    private final CollapsedView collapsedView;
    private final ExpandedView expandedView;

    public MonitorView(String name, String ipAddress) {
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

}
