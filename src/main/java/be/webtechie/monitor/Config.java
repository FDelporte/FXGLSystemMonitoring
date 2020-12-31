package be.webtechie.monitor;

import javafx.util.Duration;

public final class Config {

    public static final int APP_WIDTH = 1280;

    // * 9 / 16 gives the HD ratio
    public static final int APP_HEIGHT = APP_WIDTH * 9 / 16;

    public static final int NUM_MONITORS_PER_ROW = 4;

    public static final double MONITOR_WIDTH = 1.0 * APP_WIDTH / NUM_MONITORS_PER_ROW;

    // * 9 / 16 gives the HD ratio
    public static final double MONITOR_HEIGHT = MONITOR_WIDTH * 9 / 16;

    public static final double MONITOR_SCALE_RATIO = APP_WIDTH / MONITOR_WIDTH;

    public static final Duration ANIMATION_DURATION = Duration.seconds(0.16);

    /**
     * How often to read from the data source to pull latest info.
     */
    public static final Duration DATA_UPDATE_FREQUENCY = Duration.seconds(1.0);
}
