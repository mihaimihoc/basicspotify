package org.example.final_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HelloController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        Helper helper = new Helper();
        int userId = helper.validateLogin(email, password);

        if (userId != -1) {
            messageLabel.setText("Login successful!");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("currentUser"))) {
                writer.write(String.valueOf(userId));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage currentStage = (Stage) passwordField.getScene().getWindow();
            Helper.transition(currentStage, "main-menu.fxml");
        } else {
            messageLabel.setText("Invalid credentials. Please try again.");
        }
    }


    @FXML
    protected void onRegisterButtonClick() {
        Stage currentStage = (Stage) passwordField.getScene().getWindow();
        Helper.transition(currentStage, "register.fxml");
    }
}