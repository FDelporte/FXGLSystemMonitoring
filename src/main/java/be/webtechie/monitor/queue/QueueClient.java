package be.webtechie.monitor.queue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class QueueClient {

    private MqttClient client;

    private final ObservableList<Reading> queueItems = FXCollections.observableArrayList();

    private final String ipAddress;
    private final String topicName;

    public QueueClient(String ipAddress, String topicName) {
        this.ipAddress = ipAddress;
        this.topicName = topicName;
        initConnection();
    }

    public void initConnection() {
        if (!initConnection(ipAddress)) {
            System.err.println("Initializing connection failed");
        }
        connect();
        subscribe();
    }

    private boolean initConnection(String ipAddress) {
        try {
            client = new MqttClient("tcp://" + ipAddress + ":1883", MqttClient.generateClientId());
            return true;
        } catch (MqttException ex) {
            System.err.println("MqttException: " + ex.getMessage());
        }

        return false;
    }

    private void connect() {
        try {
            client.connect();
        } catch (MqttException ex) {
            System.err.println("MqttException: " + ex.getMessage());
        }
    }

    private void subscribe() {
        try {
            client.setCallback(new ClientCallback(queueItems));
            client.subscribe(topicName);
        } catch (MqttException ex) {
            System.err.println("Error while subscribing: " + ex.getMessage());
        }
    }

    public void sendMessage(String topic, String messageText) {
        if (!client.isConnected()) {
            System.err.println("The queue client is not connected!");
        }

        MqttMessage message = new MqttMessage();
        message.setPayload(messageText.getBytes());

        try {
            client.publish(topic, message);
        } catch (MqttException ex) {
            System.err.println("MqttException: " + ex.getMessage());
        }
    }

    public ObservableList<Reading> getQueueItems() {
        return queueItems;
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException ex) {
            System.err.println("MqttException: " + ex.getMessage());
        }
    }
}