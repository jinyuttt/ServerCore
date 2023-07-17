package DB;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class H2Util {
    public static final String DRIVER_CLASS = "org.h2.Driver";
    public static final String JDBC_URL = "jdbc:h2:D:/log/h2";
    public static final String USER = "root";
    public static final String PASSWORD = "root";
    static JdbcConnectionPool pool=null;
    static {

        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        JdbcConnectionPool pool = JdbcConnectionPool.create(JDBC_URL, USER, PASSWORD);
    }

    public static   Connection getConnect() throws SQLException {
       return pool.getConnection();
    }
}
