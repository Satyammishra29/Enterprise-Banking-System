package model.auth;

/**
 * User roles for the banking system
 */
public enum Role {
    ADMIN("Administrator"),
    CASHIER("Cashier"),
    AUDITOR("Auditor");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
