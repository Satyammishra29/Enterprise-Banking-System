package model.audit;

import java.time.LocalDateTime;

public class AuditLog {
    private int id;
    private String action;
    private String details;
    private String username;
    private String ipAddress;
    private LocalDateTime timestamp;
    private boolean success;
    private String errorMessage;

    public AuditLog(String action, String details, String username, String ipAddress) {
        this.action = action;
        this.details = details;
        this.username = username;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }

    public AuditLog(String action, String details, String username, String ipAddress, String errorMessage) {
        this(action, details, username, ipAddress);
        this.success = false;
        this.errorMessage = errorMessage;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
