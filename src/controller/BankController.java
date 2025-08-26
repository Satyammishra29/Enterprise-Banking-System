package controller;

import model.BankAccount;
import model.SavingsAccount;
import model.CurrentAccount;
import java.util.ArrayList;
import java.util.List;

public class BankController {
    private final List<BankAccount> accounts = new ArrayList<>();

    public List<BankAccount> getAccounts() { return accounts; }

    public BankAccount createAccount(String type, String accountNumber, String holderName, double initialBalance) {
        BankAccount account;
        if ("Savings".equalsIgnoreCase(type)) {
            account = new SavingsAccount(accountNumber, holderName, initialBalance);
        } else if ("Current".equalsIgnoreCase(type)) {
            account = new CurrentAccount(accountNumber, holderName, initialBalance);
        } else {
            throw new IllegalArgumentException("Invalid account type");
        }
        accounts.add(account);
        return account;
    }

    public boolean deposit(String accountNumber, double amount) {
        BankAccount acc = findAccount(accountNumber);
        if (acc != null) {
            acc.deposit(amount);
            return true;
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount) {
        BankAccount acc = findAccount(accountNumber);
        if (acc != null) {
            return acc.withdraw(amount);
        }
        return false;
    }

    public BankAccount findAccount(String accountNumber) {
        for (BankAccount acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return acc;
            }
        }
        return null;
    }
}
