package controller.banking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.banking.Transaction;
import dao.TransactionDAO;
import dao.BankAccountDAO;
import model.BankAccount;
import java.util.List;

/**
 * Controller for transaction management
 */
public class TransactionController {
    
    @FXML private TextField fromAccountField;
    @FXML private TextField toAccountField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> transactionTypeBox;
    @FXML private TextField descriptionField;
    @FXML private Button executeBtn;
    
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> colTransactionId;
    @FXML private TableColumn<Transaction, String> colFromAccount;
    @FXML private TableColumn<Transaction, String> colToAccount;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colStatus;
    
    @FXML private Label statusLabel;
    
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTransactionTypeComboBox();
        setupTransactionsTable();
        setupButtonActions();
        refreshTransactionsTable();
    }
    
    private void setupTransactionTypeComboBox() {
        transactionTypeBox.getItems().addAll("DEPOSIT", "WITHDRAWAL", "TRANSFER");
        transactionTypeBox.setValue("DEPOSIT");
    }
    
    private void setupTransactionsTable() {
        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colFromAccount.setCellValueFactory(new PropertyValueFactory<>("fromAccountNumber"));
        colToAccount.setCellValueFactory(new PropertyValueFactory<>("toAccountNumber"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colType.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        transactionsTable.setItems(transactionList);
    }
    
    private void setupButtonActions() {
        executeBtn.setOnAction(this::handleExecuteTransaction);
    }
    
    private void handleExecuteTransaction(ActionEvent event) {
        try {
            String fromAccount = fromAccountField.getText().trim();
            String toAccount = toAccountField.getText().trim();
            String amountText = amountField.getText().trim();
            String transactionType = transactionTypeBox.getValue();
            String description = descriptionField.getText().trim();
            
            if (fromAccount.isEmpty() || amountText.isEmpty() || transactionType == null) {
                showStatus("Please fill in all required fields", "error");
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showStatus("Amount must be positive", "error");
                return;
            }
            
            // Validate accounts
            BankAccount fromBankAccount = bankAccountDAO.getAccountByNumber(fromAccount);
            if (fromBankAccount == null) {
                showStatus("From account not found", "error");
                return;
            }
            
            // Handle different transaction types
            boolean success = false;
            switch (transactionType) {
                case "DEPOSIT":
                    success = handleDeposit(fromAccount, amount, description);
                    break;
                case "WITHDRAWAL":
                    success = handleWithdrawal(fromAccount, amount, description);
                    break;
                case "TRANSFER":
                    if (toAccount.isEmpty()) {
                        showStatus("To account required for transfer", "error");
                        return;
                    }
                    BankAccount toBankAccount = bankAccountDAO.getAccountByNumber(toAccount);
                    if (toBankAccount == null) {
                        showStatus("To account not found", "error");
                        return;
                    }
                    success = handleTransfer(fromAccount, toAccount, amount, description);
                    break;
            }
            
            if (success) {
                showStatus("Transaction executed successfully!", "success");
                clearFields();
                refreshTransactionsTable();
            }
            
        } catch (NumberFormatException e) {
            showStatus("Please enter a valid amount", "error");
        } catch (Exception e) {
            showStatus("Error executing transaction: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }
    
    private boolean handleDeposit(String accountNumber, double amount, String description) {
        BankAccount account = bankAccountDAO.getAccountByNumber(accountNumber);
        if (account == null) return false;
        
        account.deposit(amount);
        boolean dbSuccess = bankAccountDAO.updateBalance(accountNumber, account.getBalance());
        
        if (dbSuccess) {
            // Create transaction record
            Transaction transaction = new Transaction("", accountNumber, amount, 
                Transaction.TransactionType.DEPOSIT, description, "System");
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    private boolean handleWithdrawal(String accountNumber, double amount, String description) {
        BankAccount account = bankAccountDAO.getAccountByNumber(accountNumber);
        if (account == null) return false;
        
        boolean withdrawSuccess = account.withdraw(amount);
        if (!withdrawSuccess) {
            showStatus("Insufficient funds or below minimum balance", "error");
            return false;
        }
        
        boolean dbSuccess = bankAccountDAO.updateBalance(accountNumber, account.getBalance());
        
        if (dbSuccess) {
            // Create transaction record
            Transaction transaction = new Transaction(accountNumber, "", amount, 
                Transaction.TransactionType.WITHDRAWAL, description, "System");
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    private boolean handleTransfer(String fromAccount, String toAccount, double amount, String description) {
        BankAccount fromBankAccount = bankAccountDAO.getAccountByNumber(fromAccount);
        BankAccount toBankAccount = bankAccountDAO.getAccountByNumber(toAccount);
        
        if (fromBankAccount == null || toBankAccount == null) return false;
        
        // Withdraw from source account
        boolean withdrawSuccess = fromBankAccount.withdraw(amount);
        if (!withdrawSuccess) {
            showStatus("Insufficient funds in source account", "error");
            return false;
        }
        
        // Deposit to destination account
        toBankAccount.deposit(amount);
        
        // Update both accounts in database
        boolean fromSuccess = bankAccountDAO.updateBalance(fromAccount, fromBankAccount.getBalance());
        boolean toSuccess = bankAccountDAO.updateBalance(toAccount, toBankAccount.getBalance());
        
        if (fromSuccess && toSuccess) {
            // Create transaction record
            Transaction transaction = new Transaction(fromAccount, toAccount, amount, 
                Transaction.TransactionType.TRANSFER, description, "System");
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    private void refreshTransactionsTable() {
        try {
            transactionList.clear();
            // For now, we'll just show a message that transactions are loaded
            // In a real application, you'd load actual transaction history
            showStatus("Transaction history loaded", "info");
        } catch (Exception e) {
            showStatus("Error loading transactions: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }
    
    private void clearFields() {
        fromAccountField.clear();
        toAccountField.clear();
        amountField.clear();
        descriptionField.clear();
        transactionTypeBox.setValue("DEPOSIT");
    }
    
    private void showStatus(String message, String type) {
        statusLabel.setText(message);
        switch (type) {
            case "success":
                statusLabel.setStyle("-fx-text-fill: #28a745; -fx-font-size: 14px;");
                break;
            case "error":
                statusLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 14px;");
                break;
            case "info":
                statusLabel.setStyle("-fx-text-fill: #17a2b8; -fx-font-size: 14px;");
                break;
            default:
                statusLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");
        }
    }
}
