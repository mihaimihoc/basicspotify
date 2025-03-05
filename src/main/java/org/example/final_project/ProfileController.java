package org.example.final_project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label rankLabel;

    @FXML
    private Label playlistsCountLabel;

    @FXML
    private Button backButton;

    private AccountHelper accountHelper;


    public void initialize() {
        readUserIdFromFile();
        if (accountHelper != null) {
            loadUserData();
            loadPlaylistsCount();
        }
    }

    private void readUserIdFromFile() {
        try {
            File currentUserFile = new File("currentUser");
            if (!currentUserFile.exists()) {
                throw new IOException("Current user file not found.");
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(currentUserFile))) {
                String userId = reader.readLine();
                int userIdd = Integer.parseInt(userId);
                accountHelper = new AccountHelper(userIdd);
                accountHelper.loadUserData();
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        usernameLabel.setText("username: " + accountHelper.getUsername());
        emailLabel.setText("email: " + accountHelper.getEmail());
        rankLabel.setText("rank: " + accountHelper.getRank());
    }

    private void loadPlaylistsCount() {
        String query = "SELECT COUNT(*) AS count FROM playlists WHERE account_id = ?";

        try (Connection connection = Helper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, accountHelper.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int playlistsCount = resultSet.getInt("count");
                playlistsCountLabel.setText("Number of playlists: " + playlistsCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBackButtonClick() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        Helper.transition(currentStage, "main-menu.fxml");
    }
}
