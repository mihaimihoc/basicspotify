package org.example.final_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label messageLabel;


    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");

        } else {
            Helper helper = new Helper();
            if (helper.isEmailOrUsernameTaken(email, username)) {
                messageLabel.setText("Email or username already in use.");
            } else if (helper.registerAccount(username, email, password)) {
                messageLabel.setText("Account created successfully!");
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                Helper.transition(currentStage, "hello-view.fxml");
            } else {
                messageLabel.setText("Error creating account. Try again.");
            }
        }
    }


    @FXML
    protected void onBackButtonClick() {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        Helper.transition(currentStage, "hello-view.fxml");
    }
}
