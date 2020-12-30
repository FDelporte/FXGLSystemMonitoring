package be.webtechie.monitor.data;

import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Defines the data structure for a single reading at a specific time from a single device.
 */
public final class Reading {

    // These values represent % and are in [0.0 .. 100.0] range

    @JsonbProperty("hostname")
    private String hostname;

    @JsonbProperty("ipAddress")
    private String ipAddress;

    @JsonbProperty("cpu")
    private double cpuUsage;

    @JsonbProperty("swap_memory")
    private SwapMemory swapMemory;

    @JsonbProperty("virtual_memory")
    private VirtualMemory virtualMemory;

    @JsonbProperty("network")
    private Network network;

    public Reading() {
        // NOP needed for JSON mapping
    }

    public Reading(double cpuUsage, long ramUsage, long networkReceived) {
        this.cpuUsage = cpuUsage;
        virtualMemory = new VirtualMemory();
        virtualMemory.setUsed(ramUsage);
        network = new Network();
        network.setPacketsReceived(networkReceived);
    }

    public void update(Reading reading) {
        cpuUsage = reading.getCpuUsage();
        swapMemory = reading.getSwapMemory();
        virtualMemory = reading.getVirtualMemory();
        network = reading.getNetwork();
    }

    public String getHostname() {
        return hostname;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public SwapMemory getSwapMemory() {
        return swapMemory;
    }

    public void setSwapMemory(SwapMemory swapMemory) {
        this.swapMemory = swapMemory;
    }

    public VirtualMemory getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(VirtualMemory virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * @return a new (effectively immutable) object that contains reading information for internal buffer
     */
    public Reading copy() {
        return new Reading(cpuUsage, virtualMemory.getUsed(), network.getPacketsReceived());
    }
}
