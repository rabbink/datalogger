package nl.rabbink.datalogger.sensor;

import nl.rabbink.datalogger.sensor.impl.BMP180Sensor;
import nl.rabbink.datalogger.sensor.impl.SimulatedSensor;

public class SensorFactory {

    public static Sensor getSensor(boolean simulated) {
        if (simulated) {
            return SimulatedSensor.INSTANCE;
        } else {
            return BMP180Sensor.INSTANCE;
        }
    }

}
