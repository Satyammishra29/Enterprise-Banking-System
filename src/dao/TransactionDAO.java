package dao;

import model.banking.Transaction;
import util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transaction operations
 */
public class TransactionDAO {
    
    /**
     * Create a new transaction
     */
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, from_account_number, to_account_number, amount, transaction_type, description, performed_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getFromAccountNumber());
            pstmt.setString(3, transaction.getToAccountNumber());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getTransactionType().name());
            pstmt.setString(6, transaction.getDescription());
            pstmt.setString(7, transaction.getPerformedBy());
            pstmt.setString(8, transaction.getStatus().name());
            
            int affectedRows = pstmt.executeUpdate();
            conn.commit();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating transaction: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get all transactions for an account
     */
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account_number = ? OR to_account_number = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    /**
     * Get transaction by ID
     */
    public Transaction getTransactionById(String transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Update transaction status
     */
    public boolean updateTransactionStatus(String transactionId, Transaction.TransactionStatus status) {
        String sql = "UPDATE transactions SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            pstmt.setString(2, transactionId);
            
            int affectedRows = pstmt.executeUpdate();
            conn.commit();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating transaction status: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get transaction summary statistics
     */
    public TransactionSummary getTransactionSummary() {
        String sql = "SELECT COUNT(*) as total_transactions, SUM(amount) as total_amount FROM transactions WHERE status = 'COMPLETED'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return new TransactionSummary(
                    rs.getInt("total_transactions"),
                    rs.getDouble("total_amount")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction summary: " + e.getMessage());
        }
        return new TransactionSummary(0, 0.0);
    }
    
    /**
     * Map database result set to Transaction object
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setTransactionId(rs.getString("transaction_id"));
        transaction.setFromAccountNumber(rs.getString("from_account_number"));
        transaction.setToAccountNumber(rs.getString("to_account_number"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setTransactionType(Transaction.TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setDescription(rs.getString("description"));
        transaction.setPerformedBy(rs.getString("performed_by"));
        transaction.setStatus(Transaction.TransactionStatus.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return transaction;
    }
    
    /**
     * Inner class for transaction summary data
     */
    public static class TransactionSummary {
        private final int totalTransactions;
        private final double totalAmount;
        
        public TransactionSummary(int totalTransactions, double totalAmount) {
            this.totalTransactions = totalTransactions;
            this.totalAmount = totalAmount;
        }
        
        public int getTotalTransactions() { return totalTransactions; }
        public double getTotalAmount() { return totalAmount; }
    }
}
