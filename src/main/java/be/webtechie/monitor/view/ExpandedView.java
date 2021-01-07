package be.webtechie.monitor.view;

import be.webtechie.monitor.data.Reading;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static be.webtechie.monitor.Config.*;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

class ExpandedView extends Parent implements ReadingHandler {

    private final List<CanvasLineChart> lineCharts = new ArrayList<>();

    ExpandedView(String name) {
        Text title = getUIFactoryService().newText(name, Color.WHITE, 14.0 * MONITOR_SCALE_RATIO);
        title.setTranslateX(APP_WIDTH / 2.0 - title.getLayoutBounds().getWidth() / 2.0);
        title.setTranslateY(50);

        var chart = new CanvasLineChart("CPU", APP_WIDTH / 2.5, APP_HEIGHT / 2.5, Color.RED, Reading::getCpuUsage);
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
