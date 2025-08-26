package dao;

import model.BankAccount;
import model.SavingsAccount;
import model.CurrentAccount;
import util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bank Account operations
 */
public class BankAccountDAO {
    
    /**
     * Get all bank accounts
     */
    public List<BankAccount> getAllAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        String sql = "SELECT * FROM bank_accounts WHERE status = 'ACTIVE' ORDER BY account_number";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToBankAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all accounts: " + e.getMessage());
        }
        return accounts;
    }
    
    /**
     * Get account by account number
     */
    public BankAccount getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM bank_accounts WHERE account_number = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBankAccount(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting account: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Create new bank account
     */
    public boolean createAccount(BankAccount account) {
        String sql = "INSERT INTO bank_accounts (account_number, holder_name, account_type, balance, minimum_balance, interest_rate) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getHolderName());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            
            if (account instanceof SavingsAccount) {
                pstmt.setDouble(5, 500.0); // Minimum balance for savings
                pstmt.setDouble(6, 2.5);   // Interest rate for savings
            } else if (account instanceof CurrentAccount) {
                pstmt.setDouble(5, 0.0);   // No minimum balance for current
                pstmt.setDouble(6, 0.0);   // No interest for current
            } else {
                pstmt.setDouble(5, 0.0);
                pstmt.setDouble(6, 0.0);
            }
            
            int affectedRows = pstmt.executeUpdate();
            conn.commit();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update account balance
     */
    public boolean updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE bank_accounts SET balance = ?, updated_at = CURRENT_TIMESTAMP WHERE account_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            
            int affectedRows = pstmt.executeUpdate();
            conn.commit();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get account summary statistics
     */
    public AccountSummary getAccountSummary() {
        String sql = "SELECT COUNT(*) as total_accounts, SUM(balance) as total_balance FROM bank_accounts WHERE status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return new AccountSummary(
                    rs.getInt("total_accounts"),
                    rs.getDouble("total_balance")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting account summary: " + e.getMessage());
        }
        return new AccountSummary(0, 0.0);
    }
    
    /**
     * Map database result set to BankAccount object
     */
    private BankAccount mapResultSetToBankAccount(ResultSet rs) throws SQLException {
        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        String holderName = rs.getString("holder_name");
        double balance = rs.getDouble("balance");
        
        if ("SAVINGS".equals(accountType)) {
            return new SavingsAccount(accountNumber, holderName, balance);
        } else if ("CURRENT".equals(accountType)) {
            return new CurrentAccount(accountNumber, holderName, balance);
        } else {
            // Default to savings account
            return new SavingsAccount(accountNumber, holderName, balance);
        }
    }
    
    /**
     * Inner class for account summary data
     */
    public static class AccountSummary {
        private final int totalAccounts;
        private final double totalBalance;
        
        public AccountSummary(int totalAccounts, double totalBalance) {
            this.totalAccounts = totalAccounts;
            this.totalBalance = totalBalance;
        }
        
        public int getTotalAccounts() { return totalAccounts; }
        public double getTotalBalance() { return totalBalance; }
    }
}
