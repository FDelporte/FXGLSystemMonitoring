package be.webtechie.monitor.queue;

/**
 * Defines the data structure for a single reading at a specific time from a single device.
 */
public final class Reading {

    // Raw data sent by Raspberry Pi
    // {
    //  "hostname":"raspberrypi",
    //  "ipAddress":"192.168.0.223",
    //  "cpu":"0.0",
    //  "swap_memory": {   "bytes_sent":"104853504",   "bytes_received":"0",   "packets_sent":"104853504",   "packets_received":"0.0" },
    //  "virtual_memory": {   "total":"8258498560",   "available":"7948976128",   "used":"62726144",   "free":"8025878528",   "percent":"3.7" },
    //  "network": {   "bytes_sent":"1036466",   "bytes_received":"735638",   "packets_sent":"3941",   "packets_received":"4916" }
    // }

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
