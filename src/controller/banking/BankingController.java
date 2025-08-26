package controller.banking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.BankAccount;
import model.SavingsAccount;
import model.CurrentAccount;
import dao.BankAccountDAO;
import util.SecurityUtil;
import java.util.Random;

/**
 * Controller for banking operations using database
 */
public class BankingController {
    
    @FXML private TextField accountNumberField;
    @FXML private TextField holderNameField;
    @FXML private TextField initialBalanceField;
    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private Button createAccountButton;
    
    @FXML private TextField depositAccountField;
    @FXML private TextField depositAmountField;
    @FXML private Button depositButton;
    
    @FXML private TextField withdrawAccountField;
    @FXML private TextField withdrawAmountField;
    @FXML private Button withdrawButton;
    
    @FXML private TableView<BankAccount> accountsTable;
    @FXML private TableColumn<BankAccount, String> accountNumberColumn;
    @FXML private TableColumn<BankAccount, String> holderNameColumn;
    @FXML private TableColumn<BankAccount, String> accountTypeColumn;
    @FXML private TableColumn<BankAccount, Double> balanceColumn;
    
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final ObservableList<BankAccount> accountList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupAccountTypeComboBox();
        setupAccountsTable();
        setupButtonActions();
        refreshAccountsTable();
    }
    
    private void setupAccountTypeComboBox() {
        accountTypeComboBox.getItems().addAll("SAVINGS", "CURRENT");
        accountTypeComboBox.setValue("SAVINGS");
    }
    
    private void setupAccountsTable() {
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        holderNameColumn.setCellValueFactory(new PropertyValueFactory<>("holderName"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        
        accountsTable.setItems(accountList);
    }
    
    private void setupButtonActions() {
        createAccountButton.setOnAction(this::handleCreateAccount);
        depositButton.setOnAction(this::handleDeposit);
        withdrawButton.setOnAction(this::handleWithdraw);
    }
    
    private void handleCreateAccount(ActionEvent event) {
        try {
            String accountNumber = accountNumberField.getText().trim();
            String holderName = holderNameField.getText().trim();
            String accountType = accountTypeComboBox.getValue();
            String balanceText = initialBalanceField.getText().trim();
            
            if (accountNumber.isEmpty() || holderName.isEmpty() || balanceText.isEmpty()) {
                showAlert("Please fill in all fields", Alert.AlertType.WARNING);
                return;
            }
            
            double initialBalance = Double.parseDouble(balanceText);
            if (initialBalance < 0) {
                showAlert("Initial balance cannot be negative", Alert.AlertType.ERROR);
                return;
            }
            
            // Create account object
            BankAccount account;
            if ("SAVINGS".equals(accountType)) {
                account = new SavingsAccount(accountNumber, holderName, initialBalance);
            } else {
                account = new CurrentAccount(accountNumber, holderName, initialBalance);
            }
            
            // Save to database
            boolean success = bankAccountDAO.createAccount(account);
            if (success) {
                showAlert("Account created successfully!", Alert.AlertType.INFORMATION);
                clearCreateAccountFields();
                refreshAccountsTable();
            } else {
                showAlert("Failed to create account", Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid balance amount", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error creating account: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void handleDeposit(ActionEvent event) {
        try {
            String accountNumber = depositAccountField.getText().trim();
            String amountText = depositAmountField.getText().trim();
            
            if (accountNumber.isEmpty() || amountText.isEmpty()) {
                showAlert("Please fill in all fields", Alert.AlertType.WARNING);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert("Deposit amount must be positive", Alert.AlertType.ERROR);
                return;
            }
            
            // Get account from database
            BankAccount account = bankAccountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                showAlert("Account not found", Alert.AlertType.ERROR);
                return;
            }
            
            // Perform deposit
            account.deposit(amount);
            
            // Update database
            boolean success = bankAccountDAO.updateBalance(accountNumber, account.getBalance());
            if (success) {
                showAlert("Deposit successful! New balance: $" + String.format("%.2f", account.getBalance()), Alert.AlertType.INFORMATION);
                clearDepositFields();
                refreshAccountsTable();
            } else {
                showAlert("Failed to update account balance", Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid amount", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error processing deposit: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void handleWithdraw(ActionEvent event) {
        try {
            String accountNumber = withdrawAccountField.getText().trim();
            String amountText = withdrawAmountField.getText().trim();
            
            if (accountNumber.isEmpty() || amountText.isEmpty()) {
                showAlert("Please fill in all fields", Alert.AlertType.WARNING);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert("Withdrawal amount must be positive", Alert.AlertType.ERROR);
                return;
            }
            
            // Get account from database
            BankAccount account = bankAccountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                showAlert("Account not found", Alert.AlertType.ERROR);
                return;
            }
            
            // Perform withdrawal
            boolean success = account.withdraw(amount);
            if (success) {
                // Update database
                boolean dbSuccess = bankAccountDAO.updateBalance(accountNumber, account.getBalance());
                if (dbSuccess) {
                    showAlert("Withdrawal successful! New balance: $" + String.format("%.2f", account.getBalance()), Alert.AlertType.INFORMATION);
                    clearWithdrawFields();
                    refreshAccountsTable();
                } else {
                    showAlert("Failed to update account balance", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Withdrawal failed. Insufficient funds or below minimum balance.", Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid amount", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error processing withdrawal: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void refreshAccountsTable() {
        try {
            accountList.clear();
            accountList.addAll(bankAccountDAO.getAllAccounts());
        } catch (Exception e) {
            showAlert("Error loading accounts: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void clearCreateAccountFields() {
        accountNumberField.clear();
        holderNameField.clear();
        initialBalanceField.clear();
        accountTypeComboBox.setValue("SAVINGS");
    }
    
    private void clearDepositFields() {
        depositAccountField.clear();
        depositAmountField.clear();
    }
    
    private void clearWithdrawFields() {
        withdrawAccountField.clear();
        withdrawAmountField.clear();
    }
    
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Banking System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
