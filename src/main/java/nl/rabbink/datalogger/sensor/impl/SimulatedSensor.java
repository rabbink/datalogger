package nl.rabbink.datalogger.sensor.impl;

import nl.rabbink.datalogger.sensor.Sensor;
import java.io.IOException;
import java.util.Calendar;

public enum SimulatedSensor implements Sensor {

    INSTANCE;

    private static final int PERIOD = 60 * 24; // one day in minutes
    private static final double PEAK = 22.0; // max. simulate value

    @Override
    public double readTemperature() throws IOException {
        // Current minute of day
        Calendar calendar = Calendar.getInstance();
        
        return calculateValue(calendar);
    }

    /**
     * 
     * @param calendar
     * @return a value between 0.0 and PEAK
     */
    public static double calculateValue(Calendar calendar) {
        int minuteOfDay = calculateMinuteOfDay(calendar); 
        
        if (minuteOfDay < 0 || minuteOfDay > PERIOD) {
            throw new IllegalArgumentException("minuteOfDay: " + minuteOfDay + " should be between 0 and " + PERIOD);
        }
        return Math.sin(minuteOfDay * Math.PI / PERIOD) * PEAK;
    }
    
    private static int calculateMinuteOfDay(Calendar calendar) {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfDay = (hourOfDay * 60) + calendar.get(Calendar.MINUTE);
        
        return minuteOfDay;
    }

}
