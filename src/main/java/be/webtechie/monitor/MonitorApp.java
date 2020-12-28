package be.webtechie.monitor;

import be.webtechie.monitor.data.Reading;
import be.webtechie.monitor.queue.QueueClient;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.webtechie.monitor.Config.*;
import static com.almasb.fxgl.dsl.FXGL.addUINode;
import static com.almasb.fxgl.dsl.FXGL.run;

public class MonitorApp extends GameApplication {

    private static final String TOPIC_NAME = "topic/statsCollector";

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
        queueClient = new QueueClient("192.168.0.223", TOPIC_NAME);
        monitors = new ArrayList<>();

        // connect in a bg thread, so we can load the app quicker
        // getExecutor().startAsync(() -> queueClient.initConnection());

        if (!queueClient.isConnected()) {
            addMockData();
        }

        run(() -> {
            for (Reading reading : queueClient.getReadings()) {
                Optional<MonitorView> existingMonitorView = monitors.stream()
                        .filter(m -> m.getIpAddress().equals(reading.getIpAddress()))
                        .findFirst();
                if (existingMonitorView.isPresent()) {
                    existingMonitorView.get().onReading(reading);
                } else {
                    MonitorView newMonitorView = addMonitor(reading.getHostname(), reading.getIpAddress());
                    newMonitorView.onReading(reading);
                }
            }
        }, DATA_UPDATE_FREQUENCY);
    }

    private void addMockData() {
        /*
        TODO

        for (int i = 0; i < 10; i++) {
            addMonitor(
                    "Device-" + i,
                    new DataSource() {
                        private double t = FXGLMath.random(0.5, 1500000.0);

                        @Override
                        public Reading getReading() {
                            t += 0.00016;

                            return new Reading(
                                    noise1D(t * 7) * 90,
                                    (long) (noise1D((t + 1000) * 2) * 40),
                                    (long) (noise1D((t + 3000) * 3) * 75));
                        }
                    },
                    x,
                    y
            );
        }
        */
    }

    private MonitorView addMonitor(String name, String ipAddress) {
        var monitor = new MonitorView(name, ipAddress);
        monitors.add(monitor);
        var x = ((monitors.size() - 1) % NUM_MONITORS_PER_ROW) * MONITOR_WIDTH;
        var y = ((monitors.size() - 1) / NUM_MONITORS_PER_ROW) * MONITOR_HEIGHT;
        addUINode(monitor, x, y);
        return monitor;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
