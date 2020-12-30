package be.webtechie.monitor.data;

import jakarta.json.bind.annotation.JsonbProperty;

public class Network {

    @JsonbProperty("bytes_sent")
    private double bytesSent;

    @JsonbProperty("bytes_received")
    private long bytesReceived;

    @JsonbProperty("packets_sent")
    private long packetsSent;

    @JsonbProperty("packets_received")
    private long packetsReceived;

    public Network() {
        // NOP needed for JSON mapping
    }

    public double getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(double bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public long getPacketsSent() {
        return packetsSent;
    }

    public void setPacketsSent(long packetsSent) {
        this.packetsSent = packetsSent;
    }

    public long getPacketsReceived() {
        return packetsReceived;
    }

    public void setPacketsReceived(long packetsReceived) {
        this.packetsReceived = packetsReceived;
    }
}
