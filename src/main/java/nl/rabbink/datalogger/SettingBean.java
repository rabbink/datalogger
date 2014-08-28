package nl.rabbink.datalogger;

import nl.rabbink.datalogger.dao.impl.ReadingDAO;
import nl.rabbink.datalogger.model.Reading;
import nl.rabbink.datalogger.sensor.impl.SimulatedSensor;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean
public class SettingBean implements Serializable {

    @ManagedProperty("#{future}")
    private ScheduledFuture<?> future;

//    @ManagedProperty("#{sensormanager}")
    //    private SensorManager sensorManager;
    //    @Resource(name = "jdbc/datalogger")
    //    DataSource ds;
    private String timePeriod;

    private int capturePeriod = 15;

    public SettingBean() {

    }

    public void generateReadings() {
        ReadingDAO dao = ReadingDAO.getInstance();
        Reading earliestReading = dao.getEarliest();

        Calendar calendar = Calendar.getInstance();

        Date timestamp;

        if (earliestReading == null) {
            timestamp = new Date();
        } else {
            timestamp = earliestReading.getTimestamp();
        }

        calendar.setTime(timestamp);
        calendar.set(Calendar.SECOND, 0);

        TimePeriod lookup = TimePeriod.lookup(timePeriod);

        switch (lookup) {
            case DAY:
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, -1);
                break;
            case YEAR:
                calendar.add(Calendar.YEAR, -1);
                break;
            default:
                throw new IllegalArgumentException("Not supported TimePeriod: " + lookup.name());
        }

        // Calculate first read
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % capturePeriod;
        int startMinutes = unroundedMinutes + (capturePeriod - mod);
        calendar.set(Calendar.MINUTE, startMinutes);

        Reading newReading;
        while (true) {
            calendar.add(Calendar.MINUTE, capturePeriod);
            if (calendar.getTimeInMillis() < timestamp.getTime()) {
                double value = SimulatedSensor.calculateValue(calendar);
                newReading = new Reading(calendar.getTime(), value);
                dao.insert(newReading);
            } else {
                break;
            }
        }
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public int getCapturePeriod() {
        return capturePeriod;
    }

    public void setCapturePeriod(int capturePeriod) {
        this.capturePeriod = capturePeriod;
    }

    public Date getNextCapture() {
        long delay = future.getDelay(TimeUnit.MILLISECONDS);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, (int)delay);
        
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        
        return calendar.getTime();
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    enum TimePeriod {

        DAY, MONTH, YEAR;

        public static TimePeriod lookup(String name) {
            for (TimePeriod timePeriod : TimePeriod.values()) {
                if (timePeriod.name().equalsIgnoreCase(name)) {
                    return timePeriod;
                }
            }

            throw new IllegalArgumentException("Unknown TimePeriod: " + name);
        }
    }

}
