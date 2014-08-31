package nl.rabbink.datalogger.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rabbink.datalogger.dao.DAO;
import nl.rabbink.datalogger.model.Reading;
import nl.rabbink.datalogger.model.Result;
import nl.rabbink.datalogger.rest.SortOrder;
import nl.rabbink.datalogger.rest.TableColumn;

public class ReadingDAO extends DAO<Reading> {

    private static volatile ReadingDAO instance;

    public ReadingDAO() {
        super();
    }

    @Override
    public void insert(Reading reading) {
        try (Connection db = getConnection()) {
            try (PreparedStatement ps = db.prepareStatement("insert into reading (timestamp, value) values (?, ?)")) {
                ps.setTimestamp(1, new java.sql.Timestamp(reading.getTimestamp().getTime()));
                ps.setDouble(2, reading.getValue());

                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadingDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Reading> list() {
        List<Reading> readings = new ArrayList<>();
        try (Connection db = getConnection()) {
            db.setReadOnly(true);
            try (PreparedStatement ps = db.prepareStatement("select * from reading where timestamp > DATEADD('DAY',-1, NOW()) order by TIMESTAMP")) {
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    readings.add(map(resultSet));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadingDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return readings;
    }

    public Result list(int limit, int offset, TableColumn orderBy, SortOrder sortOrder) {
        Result<Reading> result = new Result();
        try (Connection db = getConnection()) {
            db.setReadOnly(true);
            db.setAutoCommit(false);

            try {
                try (PreparedStatement ps = db.prepareStatement("select count(*) as fullCount from reading")) {
                    System.out.println(ps);
                    long startTime = System.nanoTime();
                    ResultSet resultSet = ps.executeQuery();
                    long queryDuration = System.nanoTime() - startTime;
                    result.setQueryDuration(queryDuration);
                    while (resultSet.next()) {
                        result.setCount(resultSet.getInt("fullCount"));
                    }
                }

                try (PreparedStatement ps = db.prepareStatement("select * from reading order by " + orderBy.name() + " " + sortOrder.name() + " limit ? offset ?")) {
                    ps.setInt(1, limit);
                    ps.setInt(2, offset);
                    System.out.println(ps);
                    long startTime = System.nanoTime();
                    ResultSet resultSet = ps.executeQuery();
                    long queryDuration = System.nanoTime() - startTime;
                    result.setQueryDuration(queryDuration);
                    while (resultSet.next()) {
                        result.getValues().add(map(resultSet));
                    }
                }
                
                db.commit();
            } catch(SQLException e) {
                System.out.println("Transaction being rolled back");
                db.rollback();
                throw e;
            } finally {
                db.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadingDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Reading getEarliest() {
        Reading reading = null;
        try (Connection db = getConnection()) {
            db.setReadOnly(true);
            try (PreparedStatement ps = db.prepareStatement("select * from reading where timestamp = (select min(timestamp) from reading)")) {
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    reading = map(resultSet);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadingDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return reading;
    }

    @Override
    public void update(Reading t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Reading t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static Reading map(ResultSet resultSet) throws SQLException {
        Reading reading = new Reading();
        reading.setId(resultSet.getLong("id"));
        reading.setTimestamp(resultSet.getTimestamp("timestamp"));
        reading.setValue(resultSet.getDouble("value"));
        return reading;
    }

    public static ReadingDAO getInstance() {
        if (instance == null) {
            synchronized (ReadingDAO.class) {
                if (instance == null) {
                    instance = new ReadingDAO();
                }
            }
        }
        return instance;
    }
}
