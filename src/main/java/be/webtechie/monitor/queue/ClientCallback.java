package be.webtechie.monitor.queue;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ClientCallback implements MqttCallback {

    private final ObservableList<Reading> queueItems;

    public ClientCallback(ObservableList<Reading> queueItems) {
        this.queueItems = queueItems;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        System.out.println("Message received:\n\t" + new String(mqttMessage.getPayload()));

        Platform.runLater(() -> {
            // TODO get data from queue message
            // mqttMessage.getPayload()
            queueItems.add(new Reading());
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery complete");
    }
}