package be.webtechie.monitor.data;

import jakarta.json.bind.annotation.JsonbProperty;

public class SwapMemory {

    @JsonbProperty("total")
    private double total;

    @JsonbProperty("used")
    private long used;

    @JsonbProperty("free")
    private long free;

    @JsonbProperty("percent")
    private double percent;

    public SwapMemory() {
        // NOP needed for JSON mapping
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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
