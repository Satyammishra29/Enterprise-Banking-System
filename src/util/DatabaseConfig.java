package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration and connection management utility
 */
public class DatabaseConfig {
    
    // Database connection properties
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/enterprise_banking";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "satyam@1"; // Change this to your actual password
    
    // Connection pool settings
    private static final int MAX_CONNECTIONS = 10;
    private static final int TIMEOUT = 30;
    
    private static Connection connection = null;
    
    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load PostgreSQL driver
                Class.forName("org.postgresql.Driver");
                
                // Create connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                
                // Set connection properties
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                
                System.out.println("Database connected successfully!");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                throw new SQLException("Database connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
    
    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connectivity
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection testConn = getConnection()) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get database URL
     * @return database URL string
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    /**
     * Get database user
     * @return database username
     */
    public static String getDatabaseUser() {
        return DB_USER;
    }
    
    /**
     * Load database properties from file (alternative configuration method)
     * @param configFile path to configuration file
     * @return Properties object
     */
    public static Properties loadProperties(String configFile) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
        }
        return props;
    }
}
