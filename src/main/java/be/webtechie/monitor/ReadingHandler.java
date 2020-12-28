package be.webtechie.monitor;

import be.webtechie.monitor.data.Reading;

public interface ReadingHandler {
    void onReading(Reading reading);
}
