package be.webtechie.monitor;

import be.webtechie.monitor.data.Reading;
import be.webtechie.monitor.queue.QueueClient;
import be.webtechie.monitor.view.MonitorView;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.collections.FXCollections;
import javafx.scene.Cursor;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static be.webtechie.monitor.Config.*;
import static com.almasb.fxgl.core.math.FXGLMath.noise1D;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MonitorApp extends GameApplication {

    private static final String TOPIC_NAME = "topic/statsCollector";

    private final List<MonitorView> monitors = new ArrayList<>();

    private QueueClient queueClient;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);
        settings.setTitle("FXGL System Monitor");
        settings.setVersion("1.0-SNAPSHOT");
        settings.setGameMenuEnabled(false);
    }

    @Override
    protected void initGame() {
        getGameScene().setCursor(Cursor.DEFAULT);

        runOnce(() -> {
            var choiceBox = getUIFactoryService().newChoiceBox(
                    FXCollections.observableArrayList("192.168.0.223", "Mock Data")
            );
            choiceBox.getSelectionModel().selectFirst();

            var btnOK = getUIFactoryService().newButton("OK");
            btnOK.setOnAction(e -> {
                var result = choiceBox.getSelectionModel().getSelectedItem();
                if ("Mock Data".equals(result)) {
                    startWithMockData();
                } else {
                    getExecutor().startAsync(() -> startWithClient(result));
                }
            });

            getDialogService().showBox("Select mode", choiceBox, btnOK);
        }, Duration.seconds(0.01));
    }

    private void startWithClient(String ip) {
        queueClient = new QueueClient(ip, TOPIC_NAME);

        run(() -> {
            for (Reading reading : queueClient.getReadings()) {
                MonitorView monitorView = monitors.stream()
                        .filter(m -> m.getIpAddress().equals(reading.getIpAddress()))
                        .findFirst()
                        .orElseGet(() -> addMonitor(reading.getHostname(), reading.getIpAddress()));

                monitorView.onReading(reading);
            }
        }, DATA_UPDATE_FREQUENCY);
    }

    private void startWithMockData() {
        for (int i = 0; i < 10; i++) {
            addMonitor("Device-" + i, "192.100.255." + i);
        }

        run(() -> monitors.forEach(m -> {
            var t = random(0.5, 150000.0);

            var reading = new Reading(
                    noise1D(t * 7) * 100,
                    (long) (noise1D((t + 1000) * 2) * 40),
                    (long) (noise1D((t + 3000) * 3) * 75)
            );

            m.onReading(reading);
        }), DATA_UPDATE_FREQUENCY);
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
