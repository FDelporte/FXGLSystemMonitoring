package be.webtechie.monitor;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import static be.webtechie.monitor.Config.*;
import static com.almasb.fxgl.dsl.FXGL.addUINode;

public class MonitorApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);
        settings.setTitle("FXGL System Monitor");
        settings.setVersion("1.0-SNAPSHOT");
    }

    @Override
    protected void initGame() {
        addMockData();
    }

    private void addMockData() {
        for (int i = 0; i < 10; i++) {
            var x = (i % NUM_MONITORS_PER_ROW) * MONITOR_WIDTH;
            var y = (i / NUM_MONITORS_PER_ROW) * MONITOR_HEIGHT;

            addMonitor("Device-" + i, () -> new Reading(), x, y);
        }
    }

    private void addMonitor(String name, DataSource dataSource, double x, double y) {
        var monitor = new MonitorView(name, dataSource);

        addUINode(monitor, x, y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
