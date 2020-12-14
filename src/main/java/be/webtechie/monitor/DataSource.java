package be.webtechie.monitor;

import be.webtechie.monitor.queue.Reading;

@FunctionalInterface
public interface DataSource {

    Reading getReading();
}
