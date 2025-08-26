package dao;

import model.auth.User;
import model.auth.Role;
import util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    
    /**
     * Authenticate user with username, password, and role
     */
    public User authenticateUser(String username, String password, Role role) {
        String sql = "SELECT * FROM users WHERE username = ? AND role = ? AND is_active = true";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, role.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // In a real application, you would verify the password hash here
                    // For now, we'll use the demo credentials
                    if (isValidDemoUser(username, password, role)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Create new user
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, email, role, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole().name());
            pstmt.setString(5, user.getFirstName());
            pstmt.setString(6, user.getLastName());
            
            int affectedRows = pstmt.executeUpdate();
            conn.commit();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update user last login
     */
    public boolean updateLastLogin(String username) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            int affectedRows = pstmt.executeUpdate();
            conn.commit();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Map database result set to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        return user;
    }
    
    /**
     * Check if demo user credentials are valid
     * In production, this would use proper password hashing
     */
    private boolean isValidDemoUser(String username, String password, Role role) {
        // Demo credentials for testing
        if ("admin".equals(username) && "admin123".equals(password) && role == Role.ADMIN) {
            return true;
        }
        if ("cashier".equals(username) && "cashier123".equals(password) && role == Role.CASHIER) {
            return true;
        }
        if ("auditor".equals(username) && "auditor123".equals(password) && role == Role.AUDITOR) {
            return true;
        }
        return false;
    }
}
