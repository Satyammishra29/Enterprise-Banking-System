package dao;

import util.DatabaseConfig;
import java.sql.*;

/**
 * Simple test class to verify database integration
 */
public class DatabaseIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing Database Integration...");
        
        try {
            // Test database connection
            Connection conn = DatabaseConfig.getConnection();
            System.out.println("✅ Database connection successful!");
            
            // Test user authentication
            testUserAuthentication();
            
            // Test account operations
            testAccountOperations();
            
            // Close connection
            DatabaseConfig.closeConnection();
            System.out.println("✅ Database integration test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Database integration test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test user authentication from database
     */
    private static void testUserAuthentication() {
        System.out.println("\n🔐 Testing User Authentication...");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Test admin user
            String sql = "SELECT * FROM users WHERE username = 'admin' AND role = 'ADMIN'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                if (rs.next()) {
                    System.out.println("✅ Admin user found in database");
                    System.out.println("   Username: " + rs.getString("username"));
                    System.out.println("   Role: " + rs.getString("role"));
                    System.out.println("   Email: " + rs.getString("email"));
                } else {
                    System.out.println("❌ Admin user not found in database");
                }
            }
            
            // Test cashier user
            sql = "SELECT * FROM users WHERE username = 'cashier' AND role = 'CASHIER'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                if (rs.next()) {
                    System.out.println("✅ Cashier user found in database");
                } else {
                    System.out.println("❌ Cashier user not found in database");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error testing user authentication: " + e.getMessage());
        }
    }
    
    /**
     * Test account operations from database
     */
    private static void testAccountOperations() {
        System.out.println("\n🏦 Testing Account Operations...");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Test account summary
            String sql = "SELECT COUNT(*) as total_accounts, SUM(balance) as total_balance FROM bank_accounts WHERE status = 'ACTIVE'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                if (rs.next()) {
                    int totalAccounts = rs.getInt("total_accounts");
                    double totalBalance = rs.getDouble("total_balance");
                    
                    System.out.println("✅ Account summary retrieved from database");
                    System.out.println("   Total Accounts: " + totalAccounts);
                    System.out.println("   Total Balance: $" + String.format("%.2f", totalBalance));
                }
            }
            
            // Test sample accounts
            sql = "SELECT * FROM bank_accounts WHERE status = 'ACTIVE' ORDER BY account_number LIMIT 3";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                System.out.println("\n📋 Sample Accounts:");
                while (rs.next()) {
                    String accountNumber = rs.getString("account_number");
                    String holderName = rs.getString("holder_name");
                    String accountType = rs.getString("account_type");
                    double balance = rs.getDouble("balance");
                    
                    System.out.println("   " + accountNumber + " | " + holderName + " | " + accountType + " | $" + String.format("%.2f", balance));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error testing account operations: " + e.getMessage());
        }
    }
}
