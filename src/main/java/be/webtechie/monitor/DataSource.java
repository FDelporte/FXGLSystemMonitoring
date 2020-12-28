package be.webtechie.monitor;

import be.webtechie.monitor.data.Reading;

@FunctionalInterface
public interface DataSource {

    Reading getReading();
}
