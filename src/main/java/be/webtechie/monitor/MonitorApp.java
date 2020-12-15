package be.webtechie.monitor;

import be.webtechie.monitor.queue.QueueClient;
import be.webtechie.monitor.queue.Reading;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;

import java.util.ArrayList;
import java.util.List;

import static be.webtechie.monitor.Config.*;
import static com.almasb.fxgl.core.math.FXGLMath.*;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MonitorApp extends GameApplication {

    private QueueClient queueClient;

    private List<MonitorView> monitors;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);
        settings.setTitle("FXGL System Monitor");
        settings.setVersion("1.0-SNAPSHOT");
    }

    @Override
    protected void initGame() {
        queueClient = new QueueClient("192.168.0.213", "deviceStates/state");
        monitors = new ArrayList<>();

        // connect in a bg thread, so we can load the app quicker
        //getExecutor().startAsync(() -> queueClient.initConnection());

        addMockData();

        run(() -> {
            monitors.forEach(MonitorView::onUpdate);
        }, DATA_UPDATE_FREQUENCY);
    }

    private void addMockData() {
        for (int i = 0; i < 10; i++) {
            var x = (i % NUM_MONITORS_PER_ROW) * MONITOR_WIDTH;
            var y = (i / NUM_MONITORS_PER_ROW) * MONITOR_HEIGHT;

            addMonitor(
                    "Device-" + i,
                    new DataSource() {
                        private double t = FXGLMath.random(0.5, 1500000.0);

                        @Override
                        public Reading getReading() {
                            t += 0.00016;

                            return new Reading(noise1D(t * 7) * 90, noise1D((t + 1000) * 2) * 40, noise1D((t + 3000) * 3) * 75);
                        }
                    },
                    x,
                    y
            );
        }
    }

    private void addMonitor(String name, DataSource dataSource, double x, double y) {
        var monitor = new MonitorView(queueClient, name, dataSource);

        monitors.add(monitor);
        addUINode(monitor, x, y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
