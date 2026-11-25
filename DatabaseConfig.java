package com.inventory.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;


public class DatabaseConfig {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int    DEFAULT_PORT = 3307;
    private static final String DEFAULT_DB_NAME = "coffeeshop_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "12345"; // XAMPP default: no password

    private static String host;
    private static int    port;
    private static String dbName;
    private static String user;
    private static String password;
    private static boolean useSSL;
    private static boolean allowPublicKeyRetrieval;
    private static String timezone;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        Properties props = new Properties();

        
        File jarDir = null;
        try {
            File jarFile = new File(
                DatabaseConfig.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            jarDir = jarFile.isDirectory() ? jarFile : jarFile.getParentFile();
        } catch (URISyntaxException | SecurityException e) {
           
        }

        java.util.List<File> candidates = new java.util.ArrayList<>();

        
        if (jarDir != null) {
            candidates.add(new File(jarDir, "config/db.properties"));
        }
        
        candidates.add(new File("config/db.properties"));
        candidates.add(new File("db.properties"));
        if (jarDir != null) {
            candidates.add(new File(jarDir, "db.properties"));
        }

        boolean loaded = false;
        for (File f : candidates) {
            if (f.exists() && f.isFile()) {
                try (InputStream in = new FileInputStream(f)) {
                    props.load(in);
                    System.out.println("[DatabaseConfig] Loaded config from: " + f.getAbsolutePath());
                    loaded = true;
                    break;
                } catch (IOException e) {
                    System.err.println("[DatabaseConfig] Warning: could not read " + f + ": " + e.getMessage());
                }
            }
        }

        if (!loaded) {
            System.err.println("[DatabaseConfig] No db.properties found — using built-in defaults (empty password).");
        }

        host     = props.getProperty("db.host", DEFAULT_HOST).trim();
        try {
            port = Integer.parseInt(props.getProperty("db.port", String.valueOf(DEFAULT_PORT)).trim());
        } catch (NumberFormatException e) {
            port = DEFAULT_PORT;
        }
        dbName   = props.getProperty("db.name", DEFAULT_DB_NAME).trim();
        user     = props.getProperty("db.user", DEFAULT_USER).trim();
        password = props.getProperty("db.password", DEFAULT_PASSWORD);
        if (password != null) password = password.trim();

        useSSL                 = Boolean.parseBoolean(props.getProperty("db.useSSL", "false").trim());
        allowPublicKeyRetrieval= Boolean.parseBoolean(props.getProperty("db.allowPublicKeyRetrieval", "true").trim());
        timezone               = props.getProperty("db.timezone", "UTC").trim();
    }

    public static String getHost()   { return host; }
    public static int    getPort()   { return port; }
    public static void   setPort(int p) { port = p; }
    public static String getDbName() { return dbName; }
    public static void   setDbName(String n) { dbName = n; }
    public static String getUser()   { return user; }
    public static String getPassword() { return password; }
    public static void   setPassword(String p) { password = (p == null) ? "" : p.trim(); }

    public static String getJdbcUrl() {
        return getJdbcUrlFor(host, port, dbName);
    }

    public static String getJdbcUrlForPort(int customPort) {
        return getJdbcUrlFor(host, customPort, dbName);
    }

    public static String getJdbcUrlFor(String h, int p, String db) {
        return String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=%b&allowPublicKeyRetrieval=%b&serverTimezone=%s",
            h, p, db, useSSL, allowPublicKeyRetrieval, timezone
        );
    }
}
