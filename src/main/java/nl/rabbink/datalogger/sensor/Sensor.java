package nl.rabbink.datalogger.sensor;

import java.io.IOException;

public interface Sensor {

    double readTemperature() throws IOException;

}
