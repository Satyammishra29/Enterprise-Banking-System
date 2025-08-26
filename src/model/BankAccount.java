package model;

/**
 * Abstract base class for bank accounts
 */
public abstract class BankAccount {
    protected String accountNumber;
    protected String holderName;
    protected double balance;
    protected String accountType;

    public BankAccount(String accountNumber, String holderName, double initialBalance, String accountType) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.accountType = accountType;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }

    // Abstract methods that must be implemented by subclasses
    public abstract void deposit(double amount);
    public abstract boolean withdraw(double amount);

    // Common methods
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", holderName='" + holderName + '\'' +
                ", balance=" + balance +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}
