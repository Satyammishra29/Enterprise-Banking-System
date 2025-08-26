package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simple database test class to verify connectivity and basic operations
 */
public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing Database Connection...");
        
        try {
            // Test basic connection
            Connection conn = DatabaseConfig.getConnection();
            System.out.println("✅ Database connection successful!");
            
            // Test basic query
            testBasicQuery(conn);
            
            // Test table existence
            testTableExistence(conn);
            
            // Close connection
            DatabaseConfig.closeConnection();
            System.out.println("✅ Database test completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("❌ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test basic SQL query execution
     */
    private static void testBasicQuery(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT version()");
            if (rs.next()) {
                String version = rs.getString(1);
                System.out.println("✅ PostgreSQL Version: " + version);
            }
        }
    }
    
    /**
     * Test if required tables exist
     */
    private static void testTableExistence(Connection conn) throws SQLException {
        String[] requiredTables = {
            "users", "bank_accounts", "transactions", 
            "audit_logs", "customers", "system_settings"
        };
        
        try (Statement stmt = conn.createStatement()) {
            for (String tableName : requiredTables) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = '" + tableName + "')"
                );
                if (rs.next() && rs.getBoolean(1)) {
                    System.out.println("✅ Table '" + tableName + "' exists");
                } else {
                    System.out.println("❌ Table '" + tableName + "' missing");
                }
            }
        }
    }
}
