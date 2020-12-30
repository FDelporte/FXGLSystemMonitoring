package be.webtechie.monitor.queue;

import be.webtechie.monitor.data.Reading;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Optional;
import java.util.Set;

public class ClientCallback implements MqttCallback {

    private final Set<Reading> readings;
    private final Jsonb jsonb;

    public ClientCallback(Set<Reading> readings) {
        this.readings = readings;
        jsonb = JsonbBuilder.create();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        String data = new String(mqttMessage.getPayload());
        System.out.println("Message received:\n\t" + data);

        try {
            Reading reading = jsonb.fromJson(data, Reading.class);
            Optional<Reading> existing = readings.stream()
                    .filter(r -> r.getIpAddress().equals(reading.getIpAddress()))
                    .findAny();

            if (existing.isPresent()) {
                existing.get().update(reading);
            } else {
                readings.add(reading);
            }
        } catch (Exception ex) {
            System.err.println("Error while parsing received data: " + ex.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery complete");
    }
}