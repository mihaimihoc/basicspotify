package org.example.final_project;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;

public class AddSongController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField artistField;

    @FXML
    private TextField albumField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField featuresField;

    @FXML
    private Label uploadStatusLabel;

    @FXML
    private Label messageLabel;



    private String filePath;
    private Integer duration;

    @FXML
    protected void onUploadFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog((Stage) titleField.getScene().getWindow());

        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
            duration = Helper.getDurationFromMp3(filePath);
            uploadStatusLabel.setText("File uploaded: " + selectedFile.getName());
        } else {
            uploadStatusLabel.setText("No file selected.");
        }
    }

    @FXML
    protected void onAddSongClick() {
        String title = titleField.getText();
        String artist = artistField.getText();
        String album = albumField.getText();
        String genre = genreField.getText();
        String features = featuresField.getText();

        if (title.isEmpty() || artist.isEmpty() || album.isEmpty() || filePath == null || duration == null || genre.isEmpty()) {
            messageLabel.setText("Please fill in all required fields.");
            return;
        }

        try {
            if (Helper.songExists(artist, title, duration)) {
                messageLabel.setText("This song already exists in the database.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error checking song existence. Please try again.");
            return;
        }

        if (Helper.addSongToDatabase(title, artist, album, genre, duration, filePath, features)) {
            messageLabel.setText("Song added successfully!");
            clearFields();
        } else {
            messageLabel.setText("Error adding song. Please try again.");
        }
    }

    @FXML
    protected void onBackButtonClick() {
        Helper.transition((Stage) titleField.getScene().getWindow(), "main-menu.fxml");
    }

    private void clearFields() {
        titleField.clear();
        artistField.clear();
        albumField.clear();
        genreField.clear();
        featuresField.clear();
        filePath = null;
        duration = null;
        uploadStatusLabel.setText("");
    }
}
