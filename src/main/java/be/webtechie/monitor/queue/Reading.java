package be.webtechie.monitor.queue;

/**
 * Defines the data structure for a single reading at a specific time from a single device.
 */
public final class Reading {

    // these values represent % and are in [0.0 .. 100.0] range
    private final double cpuUsage;
    private final double ramUsage;
    private final double diskUsage;

    public Reading(double cpuUsage, double ramUsage, double diskUsage) {
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.diskUsage = diskUsage;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public double getRamUsage() {
        return ramUsage;
    }

    public double getDiskUsage() {
        return diskUsage;
    }
}
