package controller.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.auth.Role;
import model.auth.User;
import util.SecurityUtil;
import controller.dashboard.DashboardController;
import dao.UserDAO;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Populate role combo box
        roleComboBox.getItems().addAll(Role.values());
        roleComboBox.setValue(Role.ADMIN); // Default to admin
        
        // Set up login button action
        if (loginButton != null) {
            loginButton.setOnAction(e -> handleLogin());
        }
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        Role selectedRole = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showError("Please fill in all fields");
            return;
        }

        try {
            // Authenticate user from database
            User user = userDAO.authenticateUser(username, password, selectedRole);
            
            if (user != null) {
                // Update last login
                userDAO.updateLastLogin(username);
                
                // Open main application
                openMainApplication(username, selectedRole);
            } else {
                showError("Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openMainApplication(String username, Role role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard/DashboardView.fxml"));
            Parent root = loader.load();
            
            // Get the dashboard controller and set user info
            DashboardController dashboardController = loader.getController();
            if (dashboardController != null) {
                dashboardController.setCurrentUser(username, role);
            }
            
            // Create new stage for main application
            Stage mainStage = new Stage();
            mainStage.setTitle("Enterprise Banking System - " + username);
            mainStage.setScene(new Scene(root));
            mainStage.setMaximized(true);
            mainStage.show();
            
            // Close login window
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();
            
        } catch (IOException e) {
            showError("Error opening main application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
