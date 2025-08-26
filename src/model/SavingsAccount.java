package model;

/**
 * Savings Account implementation
 */
public class SavingsAccount extends BankAccount {
    private static final double MIN_BALANCE = 500.0;
    private static final double INTEREST_RATE = 2.5; // 2.5% annual interest

    public SavingsAccount(String accountNumber, String holderName, double initialBalance) {
        super(accountNumber, holderName, initialBalance, "SAVINGS");
        
        // Ensure initial balance meets minimum requirement
        if (initialBalance < MIN_BALANCE) {
            throw new IllegalArgumentException("Initial balance must be at least $" + MIN_BALANCE);
        }
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
        
        if (balance - amount >= MIN_BALANCE) {
            balance -= amount;
            return true;
        } else {
            return false; // Insufficient funds to maintain minimum balance
        }
    }

    public double getMinimumBalance() { return MIN_BALANCE; }
    public double getInterestRate() { return INTEREST_RATE; }

    public double calculateInterest() {
        return balance * (INTEREST_RATE / 100.0);
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", holderName='" + holderName + '\'' +
                ", balance=" + balance +
                ", minimumBalance=" + MIN_BALANCE +
                ", interestRate=" + INTEREST_RATE + "%" +
                '}';
    }
}
