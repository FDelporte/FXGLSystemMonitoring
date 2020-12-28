package be.webtechie.monitor.queue;

import be.webtechie.monitor.data.Reading;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadingTest {

    @Test
    void testFromJson() {
        String json = "{"
                + "\"hostname\":\"raspberrypi\", "
                + "\"ipAddress\":\"192.168.0.223\", "
                + "\"cpu\":\"6.8\", "
                + "\"swap_memory\": {   \"total\":\"123456\",    \"used\":\"789456\",   \"free\":\"369852\",   \"percent\":\"85.6\"  }, "
                + "\"virtual_memory\": {   \"total\":\"8258498560\",   \"available\":\"7948976128\",   \"used\":\"62726144\",   \"free\":\"8025878528\",   \"percent\":\"3.7\" }, "
                + "\"network\": {   \"bytes_sent\":\"1036466\",   \"bytes_received\":\"735638\",   \"packets_sent\":\"3941\",   \"packets_received\":\"4916\" } "
                + "}";

        Jsonb jsonb = JsonbBuilder.create();

        Reading reading = jsonb.fromJson(json, Reading.class);
        System.out.println("Created reading " + reading);

        assertAll(
                () -> assertEquals("raspberrypi", reading.getHostname()),
                () -> assertEquals("192.168.0.223", reading.getIpAddress()),
                () -> assertEquals(6.8, reading.getCpuUsage(), 0.1),
                () -> assertEquals(123456, reading.getSwapMemory().getTotal()),
                () -> assertEquals(789456, reading.getSwapMemory().getUsed()),
                () -> assertEquals(369852, reading.getSwapMemory().getFree()),
                () -> assertEquals(85.6, reading.getSwapMemory().getPercent(), 0.1),
                () -> assertEquals(8258498560L, reading.getVirtualMemory().getTotal()),
                () -> assertEquals(7948976128L, reading.getVirtualMemory().getAvailable()),
                () -> assertEquals(62726144, reading.getVirtualMemory().getUsed()),
                () -> assertEquals(8025878528L, reading.getVirtualMemory().getFree()),
                () -> assertEquals(3.7, reading.getVirtualMemory().getPercent(), 0.1),
                () -> assertEquals(1036466, reading.getNetwork().getBytesSent()),
                () -> assertEquals(735638, reading.getNetwork().getBytesReceived()),
                () -> assertEquals(3941, reading.getNetwork().getPacketsSent()),
                () -> assertEquals(4916, reading.getNetwork().getPacketsReceived())
        );
    }
}
