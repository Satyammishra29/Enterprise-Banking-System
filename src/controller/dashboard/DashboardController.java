package controller.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.auth.Role;
import model.auth.User;

import java.io.IOException;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private BorderPane mainContainer;
    @FXML private VBox sidebar;
    @FXML private Label userInfoLabel;
    @FXML private Label roleLabel;
    @FXML private Button dashboardBtn;
    @FXML private Button accountsBtn;
    @FXML private Button transactionsBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;

    private String currentUsername;
    private Role currentRole;

    @FXML
    public void initialize() {
        setupSidebarNavigation();
        setupUserInfo();
    }

    public void setCurrentUser(String username, Role role) {
        this.currentUsername = username;
        this.currentRole = role;
        updateUserInfo();
        setupRoleBasedAccess();
    }

    private void setupSidebarNavigation() {
        dashboardBtn.setOnAction(e -> loadDashboard());
        accountsBtn.setOnAction(e -> loadAccounts());
        transactionsBtn.setOnAction(e -> loadTransactions());
        reportsBtn.setOnAction(e -> loadReports());
        settingsBtn.setOnAction(e -> loadSettings());
        logoutBtn.setOnAction(e -> handleLogout());
    }

    private void setupUserInfo() {
        userInfoLabel.setText("Welcome, User");
        roleLabel.setText("Role: Guest");
    }

    private void updateUserInfo() {
        if (currentUsername != null) {
            userInfoLabel.setText("Welcome, " + currentUsername);
        }
        if (currentRole != null) {
            roleLabel.setText("Role: " + currentRole.getDisplayName());
        }
    }

    private void setupRoleBasedAccess() {
        if (currentRole == Role.AUDITOR) {
            // Auditors can only view reports and transactions
            accountsBtn.setDisable(true);
            transactionsBtn.setDisable(true);
            settingsBtn.setDisable(true);
        } else if (currentRole == Role.CASHIER) {
            // Cashiers can handle accounts and transactions
            reportsBtn.setDisable(true);
            settingsBtn.setDisable(true);
        }
        // Admins have access to everything
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard/DashboardContent.fxml"));
            Parent content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            showError("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/banking/AccountView.fxml"));
            Parent content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            showError("Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/banking/TransactionView.fxml"));
            Parent content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            showError("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reports/ReportView.fxml"));
            Parent content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            showError("Error loading reports: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/settings/SettingsView.fxml"));
            Parent content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            showError("Error loading settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be returned to the login screen.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Return to login screen
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/auth/LoginView.fxml"));
                    Parent loginRoot = loader.load();
                    
                    Stage currentStage = (Stage) mainContainer.getScene().getWindow();
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle("Enterprise Banking System - Login");
                    currentStage.setMaximized(false);
                    currentStage.setWidth(400);
                    currentStage.setHeight(500);
                } catch (IOException e) {
                    showError("Error returning to login: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
