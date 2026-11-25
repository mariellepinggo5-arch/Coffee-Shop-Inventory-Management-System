package com.inventory.dao;

import com.inventory.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static Integer activePort = null;
    private static String activePassword = null;
    private static String activeDbName = null;

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    private DBConnection() {}

        public static Connection getConnection() throws SQLException {
        if (activePort != null && activePassword != null && activeDbName != null) {
            try {
                return DriverManager.getConnection(
                    DatabaseConfig.getJdbcUrlFor("localhost", activePort, activeDbName),
                    DatabaseConfig.getUser(),
                    activePassword
                );
            } catch (SQLException e) {
                activePort = null;
                activePassword = null;
                activeDbName = null;
            }
        }

        int configuredPort = DatabaseConfig.getPort();
        int[] portsToTry = (configuredPort == 3307) ? new int[]{3307, 3306} : new int[]{3306, 3307};

        String configuredPass = DatabaseConfig.getPassword();
        String[] passwordsToTry;
        if (configuredPass == null || configuredPass.isEmpty()) {
            passwordsToTry = new String[]{""};
        } else {
            passwordsToTry = new String[]{configuredPass, ""};
        }

        String configuredDb = DatabaseConfig.getDbName();
        String[] dbsToTry = "coffeeshop_db".equalsIgnoreCase(configuredDb) 
            ? new String[]{"coffeeshop_db", "inventory_db"}
            : new String[]{configuredDb, "coffeeshop_db", "inventory_db"};

        SQLException lastException = null;

        for (int port : portsToTry) {
            for (String pass : passwordsToTry) {
                for (String db : dbsToTry) {
                    try {
                        String url = DatabaseConfig.getJdbcUrlFor("localhost", port, db);
                        Connection conn = DriverManager.getConnection(url, DatabaseConfig.getUser(), pass);

                      
                        activePort = port;
                        activePassword = pass;
                        activeDbName = db;

                        DatabaseConfig.setPort(port);
                        DatabaseConfig.setPassword(pass);
                        DatabaseConfig.setDbName(db);

                        return conn;
                    } catch (SQLException ex) {
                        lastException = ex;
                       
                    }
                }
            }
        }

        throw new SQLException(
            "Unable to connect to MySQL on localhost:3307 or 3306.\n" +
            "Please verify:\n" +
            "1. MySQL is started in XAMPP Control Panel.\n" +
            "2. Database '" + DatabaseConfig.getDbName() + "' exists.\n" +
            "3. Credentials in config/db.properties match your MySQL setup.\n\n" +
            "Root Error: " + (lastException != null ? lastException.getMessage() : "Connection failed"),
            lastException
        );
    }

   
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
