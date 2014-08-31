package nl.rabbink.datalogger.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public abstract class DAO<T> {

    public static final String JNDI_DATASOURCE = "java:comp/env/jdbc/datalogger";

    private DataSource ds;

    public DAO() {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup(JNDI_DATASOURCE);
        } catch (NamingException e) {
            // TODO refactor to proper logging
            e.printStackTrace();
        }
    }

    protected Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public abstract void insert(T t);

    public abstract List<? extends T> list();
    
    public abstract void update(T t);

    public abstract void delete(T t);
}
