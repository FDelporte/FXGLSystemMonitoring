package be.webtechie.monitor.view;

import be.webtechie.monitor.data.Reading;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static be.webtechie.monitor.Config.MONITOR_HEIGHT;
import static be.webtechie.monitor.Config.MONITOR_WIDTH;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

class CollapsedView extends Parent implements ReadingHandler {

    private final List<LoadView> views = new ArrayList<>();

    CollapsedView(String name, String ipAddress) {
        Text titleName = getUIFactoryService().newText(name, Color.WHITE, 12.0);
        titleName.setTranslateX(MONITOR_WIDTH / 2.0 - titleName.getLayoutBounds().getWidth() / 2.0);
        titleName.setTranslateY(15);

        Text titleIpAddress = getUIFactoryService().newText(ipAddress, Color.WHITE, 12.0);
        titleIpAddress.setTranslateX(MONITOR_WIDTH / 2.0 - titleIpAddress.getLayoutBounds().getWidth() / 2.0);
        titleIpAddress.setTranslateY(30);

        views.add(new LoadView("CPU", Reading::getCpuUsage));
        views.add(new LoadView("RAM", r -> r.getVirtualMemory().getPercent()));

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
