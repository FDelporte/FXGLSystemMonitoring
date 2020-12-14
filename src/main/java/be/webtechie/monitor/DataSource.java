package be.webtechie.monitor;

@FunctionalInterface
public interface DataSource {

    Reading getReading();
}
