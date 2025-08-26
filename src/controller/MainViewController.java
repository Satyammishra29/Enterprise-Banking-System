package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.BankAccount;
import controller.BankController;

public class MainViewController {
    @FXML private TextField nameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField initialBalanceField;
    @FXML private ComboBox<String> accountTypeBox;
    @FXML private Button createAccountBtn;
    @FXML private TextField transAccountNumberField;
    @FXML private TextField amountField;
    @FXML private Button depositBtn;
    @FXML private Button withdrawBtn;
    @FXML private TableView<BankAccount> accountsTable;
    @FXML private TableColumn<BankAccount, String> colAccountNumber;
    @FXML private TableColumn<BankAccount, String> colHolderName;
    @FXML private TableColumn<BankAccount, String> colAccountType;
    @FXML private TableColumn<BankAccount, Double> colBalance;

    private final BankController bankController = new BankController();
    private final ObservableList<BankAccount> accountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set ComboBox items
        accountTypeBox.setItems(FXCollections.observableArrayList("Savings", "Current"));

        // Set up TableView columns
        colAccountNumber.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAccountNumber()));
        colHolderName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getHolderName()));
        colAccountType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAccountType()));
        colBalance.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getBalance()));
        accountsTable.setItems(accountList);

        // Button actions
        createAccountBtn.setOnAction(this::handleCreateAccount);
        depositBtn.setOnAction(this::handleDeposit);
        withdrawBtn.setOnAction(this::handleWithdraw);
    }

    private void handleCreateAccount(ActionEvent event) {
        String name = nameField.getText();
        String accNum = accountNumberField.getText();
        String type = accountTypeBox.getValue();
        double initialBalance;
        try {
            initialBalance = Double.parseDouble(initialBalanceField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid initial balance.");
            return;
        }
        if (name.isEmpty() || accNum.isEmpty() || type == null) {
            showAlert("Please fill all account fields.");
            return;
        }
        if (bankController.findAccount(accNum) != null) {
            showAlert("Account number already exists.");
            return;
        }
        BankAccount acc = bankController.createAccount(type, accNum, name, initialBalance);
        accountList.add(acc);
        clearAccountFields();
    }

    private void handleDeposit(ActionEvent event) {
        String accNum = transAccountNumberField.getText();
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid amount.");
            return;
        }
        if (!bankController.deposit(accNum, amount)) {
            showAlert("Deposit failed. Check account number.");
        } else {
            refreshTable();
        }
        clearTransactionFields();
    }

    private void handleWithdraw(ActionEvent event) {
        String accNum = transAccountNumberField.getText();
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid amount.");
            return;
        }
        if (!bankController.withdraw(accNum, amount)) {
            showAlert("Withdraw failed. Check account number or insufficient funds.");
        } else {
            refreshTable();
        }
        clearTransactionFields();
    }

    private void refreshTable() {
        accountList.setAll(bankController.getAccounts());
    }

    private void clearAccountFields() {
        nameField.clear();
        accountNumberField.clear();
        initialBalanceField.clear();
        accountTypeBox.getSelectionModel().clearSelection();
    }

    private void clearTransactionFields() {
        transAccountNumberField.clear();
        amountField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
