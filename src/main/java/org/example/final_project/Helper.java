package org.example.final_project;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;




public class Helper {

    static {
        Logger logger = Logger.getLogger("org.jaudiotagger");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.SEVERE);
        logger.addHandler(consoleHandler);
    }

    private static final String URL = "jdbc:mysql://localhost:3306/playlist_project";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public int validateLogin(String email, String password) {
        String query = "SELECT id FROM accounts WHERE email = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    public boolean isEmailOrUsernameTaken(String email, String username) {
        String query = "SELECT * FROM accounts WHERE email = ? OR username = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerAccount(String username, String email, String password) {
        String query = "INSERT INTO accounts (username, email, password) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void transition(Stage currentStage, String fxmlFileName) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {

            Task<Parent> loadTask = new Task<>() {
                @Override
                protected Parent call() throws Exception {
                    FXMLLoader fxmlLoader = new FXMLLoader(Helper.class.getResource(fxmlFileName));
                    return fxmlLoader.load();
                }
            };


            loadTask.setOnSucceeded(workerEvent -> {
                Parent root = loadTask.getValue();
                currentStage.setScene(new Scene(root));
            });


            loadTask.setOnFailed(workerEvent -> {
                Throwable exception = loadTask.getException();
                exception.printStackTrace();

            });


            new Thread(loadTask).start();
        });

        pause.play();
    }


//    public List<String> getPlaylistsForUser(int accountId) {
//        List<String> playlists = new ArrayList<>();
//        String query = "SELECT name FROM playlists WHERE account_id = ?";
//        try (Connection connection = getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, accountId);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            while (resultSet.next()) {
//                playlists.add(resultSet.getString("name"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return playlists;
//    }

    public List<Map.Entry<Integer, String>> getPlaylistsWithIdsForUser(int accountId) {
        List<Map.Entry<Integer, String>> playlists = new ArrayList<>();
        String query = "SELECT playlist_id, name FROM playlists WHERE account_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, accountId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                playlists.add(Map.entry(resultSet.getInt("playlist_id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public static boolean addSongToDatabase(String title, String artist, String album, String genre, Integer duration, String filePath, String features) {
        String query = "INSERT INTO songs (title, artist, album, genre, duration, file_path, features) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, artist);
            preparedStatement.setString(3, album);
            preparedStatement.setString(4, genre);
            preparedStatement.setInt(5, duration);
            preparedStatement.setString(6, filePath);
            preparedStatement.setString(7, features);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Integer getDurationFromMp3(String filePath) {
        try {

            MP3File mp3File = new MP3File(filePath);
            return mp3File.getMP3AudioHeader().getTrackLength(); // "MM:SS"
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean songExists(String artist, String title, int duration) throws SQLException {
        String query = "SELECT COUNT(*) FROM songs WHERE artist = ? AND title = ? AND duration = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, artist);
            stmt.setString(2, title);
            stmt.setInt(3, duration);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public boolean hasEmptyPlaylistForUser(int userId) {
        List<Map.Entry<Integer, String>> playlists = getPlaylistsWithIdsForUser(userId);
        for (Map.Entry<Integer, String> playlist : playlists) {
            int playlistId = playlist.getKey();
            if (getSongsInPlaylist(playlistId).isEmpty()) {
                return true;
            }
        }
        return false;
    }



    public List<String> getSongsInPlaylist(int playlistId) {
        List<String> songs = new ArrayList<>();
        String query = "SELECT * FROM playlist_songs WHERE playlist_id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setInt(1, playlistId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                songs.add(resultSet.getString("song_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }


    public void renamePlaylist(int playlistId, String newName) {
        String query = "UPDATE playlists SET name = ? WHERE playlist_id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, newName);
            statement.setInt(2, playlistId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String getSongTitleFromDB(String filePath) {
        String title = "Unknown Title";
        String sql = "SELECT title FROM songs WHERE file_path = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, filePath);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                title = rs.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title;
    }

    public static String getSongArtistFromDB(String filePath) {
        String artist = "Unknown Artist";
        String sql = "SELECT artist FROM songs WHERE file_path = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, filePath);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                artist = rs.getString("artist");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artist;
    }





}


