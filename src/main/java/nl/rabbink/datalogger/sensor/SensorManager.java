package nl.rabbink.datalogger.sensor;

import java.io.IOException;
import java.util.Date;

public class SensorManager {

    private final boolean simulated;

    public SensorManager(boolean simulated) {
        this.simulated = simulated;
    }

    public double currentTemperature() throws IOException {
        return SensorFactory.getSensor(simulated).readTemperature();
    }

}
