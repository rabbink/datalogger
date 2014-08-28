package nl.rabbink.datalogger;

import nl.rabbink.datalogger.dao.DAO;
import nl.rabbink.datalogger.dao.impl.ReadingDAO;
import nl.rabbink.datalogger.model.Reading;
import nl.rabbink.datalogger.sensor.SensorManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class AppContextListener implements ServletContextListener {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private boolean useSimulatedSensor = false;

    private int capturePeriod = 5;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        String useSimulatedSensorAsString = ctx.getInitParameter("USE_SIMULATED_SENSOR");
        if (useSimulatedSensorAsString != null) {
            useSimulatedSensor = Boolean.valueOf(useSimulatedSensorAsString);
        }

        final SensorManager sensorManager = new SensorManager(useSimulatedSensor);
        ctx.setAttribute("sensormanager", sensorManager);

        String capturePeriodAsString = ctx.getInitParameter("CAPTURE_PERIOD_IN_MINUTES");
        if (capturePeriodAsString != null) {
            try {
                capturePeriod = Integer.valueOf(capturePeriodAsString);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 5);
        calendar.clear(Calendar.MILLISECOND);

        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % capturePeriod;
        int startMinutes = unroundedMinutes + (capturePeriod - mod);

        calendar.set(Calendar.MINUTE, startMinutes);

        if (startMinutes == unroundedMinutes) {
            // Too late, schedule at next window
            calendar.add(Calendar.MINUTE, capturePeriod);
        }

        final DAO readingDAO = ReadingDAO.getInstance();
        
        final ScheduledFuture<?> future = executor.scheduleAtFixedRate(new Runnable() {
            
            @Override
            public void run() {
                try {
                    double currentTemperature = sensorManager.currentTemperature();
                    
                    Calendar calendar = Calendar.getInstance();
                    calendar.clear(Calendar.SECOND);
                    calendar.clear(Calendar.MILLISECOND);
                    readingDAO.insert(new Reading(new Date(calendar.getTimeInMillis()), currentTemperature));
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }, calendar.getTimeInMillis() - System.currentTimeMillis(), TimeUnit.MINUTES.toMillis(capturePeriod), TimeUnit.MILLISECONDS);
        
        // TODO refactor
        ctx.setAttribute("future", future);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        executor.shutdownNow();
        
        // Shutdown database
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(DAO.JNDI_DATASOURCE);
            try (Connection conn = ds.getConnection()) {
                try (Statement stat = conn.createStatement()) {
                    stat.execute("SHUTDOWN");
                    stat.close();
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        } catch (NamingException e) {
            // TODO refactor to proper logging
            e.printStackTrace();
        }
    }

}
