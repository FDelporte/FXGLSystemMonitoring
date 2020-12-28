package be.webtechie.monitor.data;

import jakarta.json.bind.annotation.JsonbProperty;

public class VirtualMemory {

    @JsonbProperty("total")
    private long total;

    @JsonbProperty("available")
    private long available;

    @JsonbProperty("used")
    private long used;

    @JsonbProperty("free")
    private long free;

    @JsonbProperty("percent")
    private double percent;

    public VirtualMemory() {
        // NOP needed for JSON mapping
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
