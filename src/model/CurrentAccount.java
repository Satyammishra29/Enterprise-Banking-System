package model;

/**
 * Current Account implementation
 */
public class CurrentAccount extends BankAccount {
    private static final double OVERDRAFT_LIMIT = -10000.0; // $10,000 overdraft limit

    public CurrentAccount(String accountNumber, String holderName, double initialBalance) {
        super(accountNumber, holderName, initialBalance, "CURRENT");
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (balance - amount >= OVERDRAFT_LIMIT) {
            balance -= amount;
            return true;
        } else {
            return false; // Exceeds overdraft limit
        }
    }

    public double getOverdraftLimit() { return OVERDRAFT_LIMIT; }

    public boolean isInOverdraft() {
        return balance < 0;
    }

    public double getAvailableBalance() {
        return balance - OVERDRAFT_LIMIT;
    }

    @Override
    public String toString() {
        return "CurrentAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", holderName='" + holderName + '\'' +
                ", balance=" + balance +
                ", overdraftLimit=" + OVERDRAFT_LIMIT +
                ", inOverdraft=" + isInOverdraft() +
                '}';
    }
}