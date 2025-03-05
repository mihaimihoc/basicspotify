basic music gui app using a database

for database implementation the following commands could help, also i used xampp

CREATE DATABASE IF NOT EXISTS playlist_db;
USE playlist_db;

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
