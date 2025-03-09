BasicSpotify

The project is a playlist management application that allows users to organize and interact
with their music collection through a graphical user interface (GUI) built with JavaFX. The
main functionalities of the project include:

1. User Account Management
o Users can register, log in, and manage their profiles.
o Account data is stored and retrieved securely.

2. Playlist and Song Management
o Users can create, edit, and delete playlists.
o Songs can be added to and removed from playlists.
o Song metadata such as title, artist, and duration is displayed.

3. Graphical User Interface (GUI)
o Intuitive UI for easy navigation between different features.
o Forms for adding songs and managing playlists.
o JavaFX components for user interactions.

4. Data Persistence
o Songs, playlists, and user accounts are stored using a structured database.
o The application retrieves and updates stored data dynamically.

5. Search
o Users can search for songs or playlists based on what the ones created by
them.

6. Additional Utility Features
o Helpers for managing accounts and application logic.
o Controllers for handling UI interactions and business logic.
o Mp3 files contact being able to change volume, their speed and it even has a
queue
o Different ranks for people that add songs to the database
The project integrates these features to offer a seamless experience for users to manage
and explore their music collections effectively.

for database implementation the following commands could help, also i used xampp

CREATE DATABASE IF NOT EXISTS playlist_project;
USE playlist_project;

-- Table: accounts
CREATE TABLE accounts (
    id INT(11) PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL,
    rank VARCHAR(50) DEFAULT 'basic'
);

-- Table: playlists
CREATE TABLE playlists (
    playlist_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    account_id INT(11) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Table: songs
CREATE TABLE songs (
    song_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(100),
    album VARCHAR(100),
    genre VARCHAR(50),
    duration INT(11),
    file_path VARCHAR(500) NOT NULL,
    features CHAR(200)
);

-- Table: playlist_songs
CREATE TABLE playlist_songs (
    playlist_id INT(11) NOT NULL,
    song_id INT(11) NOT NULL,
    song_order INT(11) DEFAULT NULL,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);
