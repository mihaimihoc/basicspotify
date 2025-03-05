package org.example.final_project;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainMenuController {

    @FXML
    private VBox leftButtonContainer;

    @FXML
    private Button addSongButton;

    @FXML
    private VBox centerContainer;

    @FXML
    private Button playPauseButton;

    @FXML
    private Slider progressSlider;

    @FXML
    private Slider speedSlider;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Label totalDurationLabel;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private Button queueButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Label volumeIcon;

    private boolean shuffleEnabled = false;
    private List<String> shuffledQueue = new ArrayList<>();
    private double currentVolume = 0.5;




    @FXML
    private Button shuffleButton;



    @FXML
    private void showQueuePopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Queue");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER_LEFT);

        Label currentSongLabel = new Label("Current Song:");
        currentSongLabel.setStyle("-fx-font-weight: bold;");
        content.getChildren().add(currentSongLabel);

        if (currentSongIndex >= 0 && currentSongIndex < queue.size()) {
            String currentFilePath = queue.get(currentSongIndex);
            content.getChildren().add(new Label(formatSongDetails(currentFilePath)));
        } else {
            content.getChildren().add(new Label("No song currently playing."));
        }

        Label upcomingSongsLabel = new Label("\nUpcoming Songs:");
        upcomingSongsLabel.setStyle("-fx-font-weight: bold;");
        content.getChildren().add(upcomingSongsLabel);

        int maxUpcoming = Math.min(9, queue.size() - currentSongIndex - 1);
        if (maxUpcoming > 0) {
            for (int i = 1; i <= maxUpcoming; i++) {
                content.getChildren().add(new Label(formatSongDetails(queue.get(currentSongIndex + i))));
            }
        } else {
            content.getChildren().add(new Label("No upcoming songs."));
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        Scene popupScene = new Scene(scrollPane, 300, 400);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private String formatSongDetails(String filePath) {
        return Helper.getSongTitleFromDB(filePath) + " - " + Helper.getSongArtistFromDB(filePath);
    }






    private MediaPlayer mediaPlayer;
    private List<String> queue = new ArrayList<>(); // Global queue
    private List<String> currentPageQueue = new ArrayList<>(); // Tab-specific queue
    private int currentSongIndex = -1;
    private boolean isDragging = false;

    @FXML
    protected void onPrevButtonClick() {
        playPreviousSong();
    }

    @FXML
    protected void onNextButtonClick() {
        playNextSong();
    }

    @FXML
    private void resetSpeed() {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(1.0);
        }
        speedSlider.setValue(1.0);
    }



    AccountHelper accountHelper;
    Helper helper = new Helper();

    public void initialize() {
        try {
            File currentUserFile = new File("currentUser");
            if (!currentUserFile.exists()) {
                throw new IOException("Current user file not found.");
            }

            BufferedReader reader = new BufferedReader(new FileReader(currentUserFile));
            String userId = reader.readLine();
            reader.close();

            int userIdd = Integer.parseInt(userId);

            accountHelper = new AccountHelper(userIdd);
            accountHelper.loadUserData();


            addSongButton.setVisible("admin".equals(accountHelper.getRank()));

            displayPlaylists();

            progressSlider.setOnMousePressed(e -> isDragging = true);

            progressSlider.setOnMouseReleased(e -> {
                isDragging = false;
                if (mediaPlayer != null) {
                    mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
                }
            });

            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setRate(newVal.doubleValue());
                }
            });

            volumeSlider.setValue(currentVolume);

            if (mediaPlayer != null) {
                mediaPlayer.setVolume(currentVolume);
            }

            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                currentVolume = newVal.doubleValue();
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(currentVolume);
                }
            });



        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            addSongButton.setVisible(false);
        }
    }

    @FXML
    protected void onPlayPauseButtonClick() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("⏸");
        }
    }


    @FXML
    protected void onBackButtonClick() {
        fadeOutAndStopMedia();
        Stage currentStage = (Stage) addSongButton.getScene().getWindow();
        Helper.transition(currentStage, "hello-view.fxml");
    }

    @FXML
    protected void onAddSongButtonClick() {
        fadeOutAndStopMedia();
        Stage currentStage = (Stage) addSongButton.getScene().getWindow();
        Helper.transition(currentStage, "add-song-view.fxml");
    }

    @FXML
    protected void onViewProfileButtonClick() {
        fadeOutAndStopMedia();
        Stage currentStage = (Stage) addSongButton.getScene().getWindow();
        Helper.transition(currentStage, "profile-view.fxml");
    }


    private void fadeOutAndStopMedia() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(mediaPlayer.volumeProperty(), mediaPlayer.getVolume())),
                    new KeyFrame(Duration.seconds(1), new KeyValue(mediaPlayer.volumeProperty(), 0))
            );

            fadeOut.setOnFinished(event -> mediaPlayer.stop());
            fadeOut.play();
        }
    }



    public void displayPlaylists() {
        leftButtonContainer.getChildren().clear();


        Button songsButton = new Button("Songs");
        songsButton.setPrefWidth(200);
        songsButton.setOnAction(e -> displayAllSongs());
        leftButtonContainer.getChildren().add(songsButton);


        List<Map.Entry<Integer, String>> playlists = helper.getPlaylistsWithIdsForUser(accountHelper.getId());

        for (Map.Entry<Integer, String> playlist : playlists) {
            HBox playlistRow = new HBox(5);
            playlistRow.setAlignment(Pos.CENTER_LEFT);

            Button playlistButton = new Button(playlist.getValue());
            playlistButton.setPrefWidth(170);
            playlistButton.setOnAction(e -> openPlaylist(playlist.getKey()));

            Button renameButton = new Button("✎");
            renameButton.setOnAction(e -> showRenamePlaylistPopup(playlist.getKey(), playlist.getValue()));

            Button deleteButton = new Button("🗑️");
            deleteButton.setOnAction(e -> deletePlaylist(playlist.getKey(), playlistRow));

            playlistRow.getChildren().addAll(playlistButton, renameButton, deleteButton);
            leftButtonContainer.getChildren().add(playlistRow);
        }

        Button createPlaylistButton = new Button("Create Playlist");
        createPlaylistButton.setPrefWidth(200);
        createPlaylistButton.setOnAction(e -> {
            if (helper.hasEmptyPlaylistForUser(accountHelper.getId())) {
                showWarning("You already have an empty playlist. Please use it before creating a new one.");
            } else {
                createNewPlaylist();
            }
        });
        leftButtonContainer.getChildren().add(createPlaylistButton);
    }

    private void deletePlaylist(int playlistId, HBox playlistRow) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Playlist");
        confirmation.setHeaderText("Are you sure you want to delete this playlist?");
        confirmation.setContentText("This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection connection = Helper.getConnection()) {
                    String deleteSongsQuery = "DELETE FROM playlist_songs WHERE playlist_id = ?";
                    try (PreparedStatement deleteSongsStmt = connection.prepareStatement(deleteSongsQuery)) {
                        deleteSongsStmt.setInt(1, playlistId);
                        deleteSongsStmt.executeUpdate();
                    }

                    String deletePlaylistQuery = "DELETE FROM playlists WHERE playlist_id = ?";
                    try (PreparedStatement deletePlaylistStmt = connection.prepareStatement(deletePlaylistQuery)) {
                        deletePlaylistStmt.setInt(1, playlistId);
                        deletePlaylistStmt.executeUpdate();
                    }

                    leftButtonContainer.getChildren().remove(playlistRow);

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Playlist Deleted");
                    successAlert.setContentText("The playlist was successfully deleted.");
                    successAlert.show();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Could not delete playlist");
                    errorAlert.setContentText("An error occurred while deleting the playlist. Please try again.");
                    errorAlert.show();
                }
            }
        });
    }


    private void showRenamePlaylistPopup(int playlistId, String currentName) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Rename Playlist");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        Label promptLabel = new Label("Enter new name for playlist:");
        TextField nameField = new TextField(currentName);

        Button renameButton = new Button("Rename");
        renameButton.setOnAction(e -> {
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                helper.renamePlaylist(playlistId, newName);
                displayPlaylists();
                popupStage.close();
            }
        });

        layout.getChildren().addAll(promptLabel, nameField, renameButton);

        Scene scene = new Scene(layout, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }






    private void createNewPlaylist() {
        String newPlaylistName = "New Playlist";
        String query = "INSERT INTO playlists (name, account_id) VALUES (?, ?)";

        try (Connection connection = Helper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newPlaylistName);
            preparedStatement.setInt(2, accountHelper.getId());
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Playlist created successfully!");
                displayPlaylists();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayAllSongs() {
        centerContainer.getChildren().clear();

        currentPageQueue = new ArrayList<>();
        String query = "SELECT song_id, title, artist, album, genre, duration, file_path, features FROM songs";

        try (Connection connection = Helper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int songId = resultSet.getInt("song_id");
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String album = resultSet.getString("album");
                String genre = resultSet.getString("genre");
                int duration = resultSet.getInt("duration");
                String filePath = resultSet.getString("file_path");
                String features = resultSet.getString("features");

                currentPageQueue.add(filePath);

                HBox songRow = new HBox(10);
                songRow.setAlignment(Pos.CENTER_LEFT);
                if (!features.isEmpty()) {
                    artist = artist + "," + features;
                }
                Label songLabel = new Label(
                        String.format("Title: %s | Artist: %s | Album: %s | Genre: %s | Duration: %d sec",
                                title, artist, album, genre, duration));
                Button playButton = new Button("Play");
                Button addToPlaylistButton = new Button("Add to Playlist");

                playButton.setOnAction(e -> {
                    shuffleButton.setStyle("");
                    shuffleEnabled = false;
                    updateQueueFromCurrentPage();
                    playSong(filePath);
                });

                addToPlaylistButton.setOnAction(e -> openAddToPlaylistPopup(songId));

                songRow.getChildren().addAll(playButton, songLabel, addToPlaylistButton);

                centerContainer.getChildren().add(songRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openPlaylist(int playlistId) {
        centerContainer.getChildren().clear();
        currentPageQueue = new ArrayList<>();

        String query = """
        SELECT s.song_id, s.title, s.artist, s.album, s.genre, s.duration, s.file_path, s.features\s
        FROM songs s
        INNER JOIN playlist_songs ps ON s.song_id = ps.song_id
        WHERE ps.playlist_id = ?
        ORDER BY ps.song_order;
        """;


        try (Connection connection = Helper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playlistId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int songId = resultSet.getInt("song_id");
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String album = resultSet.getString("album");
                String genre = resultSet.getString("genre");
                int duration = resultSet.getInt("duration");
                String filePath = resultSet.getString("file_path");
                String features = resultSet.getString("features");

                currentPageQueue.add(filePath);

                HBox songRow = new HBox(10);
                songRow.setAlignment(Pos.CENTER_LEFT);
                if (!features.isEmpty()) { artist = artist + "," + features; }

                Label songLabel = new Label(
                        String.format("Title: %s | Artist: %s | Album: %s | Genre: %s | Duration: %d sec",
                                title, artist, album, genre, duration));

                Button playButton = new Button("Play");
                Button deleteButton = new Button("Delete");

                playButton.setOnAction(e -> {
                    shuffleButton.setStyle("");
                    shuffleEnabled = false;
                    updateQueueFromCurrentPage();
                    playSong(filePath);
                });

                deleteButton.setOnAction(e -> deleteSongFromPlaylist(songId, playlistId));

                songRow.getChildren().addAll(playButton, songLabel, deleteButton);

                centerContainer.getChildren().add(songRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAddToPlaylistPopup(int songId) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add to Playlist");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        Label instructionLabel = new Label("Select playlists to add this song:");
        vbox.getChildren().add(instructionLabel);

        String query = "SELECT playlist_id, name FROM playlists";
        try (Connection connection = Helper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<CheckBox> checkBoxes = new ArrayList<>();

            while (resultSet.next()) {
                int playlistId = resultSet.getInt("playlist_id");
                String playlistName = resultSet.getString("name");

                CheckBox checkBox = new CheckBox(playlistName);
                checkBox.setUserData(playlistId);
                checkBoxes.add(checkBox);
                vbox.getChildren().add(checkBox);
            }

            Button submitButton = new Button("Add to Selected Playlists");
            submitButton.setOnAction(e -> {
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        int playlistId = (int) checkBox.getUserData();
                        addSongToPlaylist(songId, playlistId);
                    }
                }
                popupStage.close();
            });

            vbox.getChildren().add(submitButton);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void addSongToPlaylist(int songId, int playlistId) {
        String checkQuery = "SELECT COUNT(*) FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        try (Connection connection = Helper.getConnection();
             PreparedStatement checkPreparedStatement = connection.prepareStatement(checkQuery)) {

            checkPreparedStatement.setInt(1, playlistId);
            checkPreparedStatement.setInt(2, songId);

            ResultSet resultSet = checkPreparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return;
            }

            String query = "INSERT INTO playlist_songs (playlist_id, song_id, song_order) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                preparedStatement.setInt(2, songId);
                preparedStatement.setInt(3, getNextSongOrder(playlistId));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private int getNextSongOrder(int playlistId) {
        String query = "SELECT MAX(song_order) FROM playlist_songs WHERE playlist_id = ?";
        try (Connection connection = Helper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, playlistId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 100000;
    }

    private void deleteSongFromPlaylist(int songId, int playlistId) {
        String queryDelete = "DELETE FROM playlist_songs WHERE song_id = ? AND playlist_id = ?";
        String queryUpdateOrder = "UPDATE playlist_songs SET song_order = song_order - 1 " +
                "WHERE playlist_id = ? AND song_order > ?";

        try (Connection connection = Helper.getConnection();
             PreparedStatement preparedStatementDelete = connection.prepareStatement(queryDelete);
             PreparedStatement preparedStatementUpdate = connection.prepareStatement(queryUpdateOrder)) {

            String queryGetOrder = "SELECT song_order FROM playlist_songs WHERE song_id = ? AND playlist_id = ?";
            try (PreparedStatement preparedStatementGetOrder = connection.prepareStatement(queryGetOrder)) {
                preparedStatementGetOrder.setInt(1, songId);
                preparedStatementGetOrder.setInt(2, playlistId);

                ResultSet resultSet = preparedStatementGetOrder.executeQuery();
                if (resultSet.next()) {
                    int songOrderToDelete = resultSet.getInt("song_order");


                    preparedStatementUpdate.setInt(1, playlistId);
                    preparedStatementUpdate.setInt(2, songOrderToDelete);


                    preparedStatementUpdate.executeUpdate();


                    preparedStatementDelete.setInt(1, songId);
                    preparedStatementDelete.setInt(2, playlistId);

                    int rowsAffected = preparedStatementDelete.executeUpdate();
                    if (rowsAffected > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Song successfully deleted from the playlist.");
                        alert.showAndWait();
                        openPlaylist(playlistId);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to delete the song from the playlist.");
                        alert.showAndWait();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void playSong(String filePath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media = new Media(new File(filePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setVolume(currentVolume);

        titleLabel.setText(Helper.getSongTitleFromDB(filePath));
        artistLabel.setText(Helper.getSongArtistFromDB(filePath));


        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!isDragging) {
                progressSlider.setValue(newTime.toSeconds());
            }
            currentTimeLabel.setText(Helper.formatDuration(newTime));
        });

        mediaPlayer.setOnReady(() -> {
            progressSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
            progressSlider.setValue(0);
            speedSlider.setValue(1.0);
            isDragging = false;


            totalDurationLabel.setText(Helper.formatDuration(mediaPlayer.getMedia().getDuration()));

            mediaPlayer.play();
            playPauseButton.setText("⏸");


            setCurrentSongIndex(filePath);
        });

        mediaPlayer.setOnEndOfMedia(this::playNextSong);
    }

    private void setCurrentSongIndex(String filePath) {
        currentSongIndex = queue.indexOf(filePath);
    }

    private void playNextSong() {
        if (queue == null || queue.isEmpty()) return;

        currentSongIndex = (currentSongIndex + 1) % queue.size();
        playSong(queue.get(currentSongIndex));
    }

    private void playPreviousSong() {
        if (queue == null || queue.isEmpty()) return;

        currentSongIndex = (currentSongIndex - 1 + queue.size()) % queue.size();
        playSong(queue.get(currentSongIndex));
    }

    private void updateQueueFromCurrentPage() {
        queue = new ArrayList<>(currentPageQueue);
    }

    @FXML
    private void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;

        if (shuffleEnabled) {
            if (!queue.isEmpty()) {
                shuffledQueue = new ArrayList<>(queue);
                String currentSong = queue.get(currentSongIndex);
                shuffledQueue.remove(currentSong);
                Collections.shuffle(shuffledQueue);
                shuffledQueue.add(0, currentSong);

                List<String> tempQueue = queue;
                queue = shuffledQueue;
                shuffledQueue = tempQueue;

                currentSongIndex = 0;
                }
        } else {
            if (!shuffledQueue.isEmpty()) {

                String currentSong = queue.get(currentSongIndex);
                List<String> tempQueue = queue;
                queue = shuffledQueue;
                shuffledQueue = tempQueue;

                currentSongIndex = queue.indexOf(currentSong);


            }
        }


        shuffleButton.setStyle(shuffleEnabled ? "-fx-background-color: #4CAF50;" : "");

    }







}
