package org.example.final_project;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;


import static org.junit.jupiter.api.Assertions.*;
class HelperTest {

    @Test
    public void testValidateLogin_NotSuccess() {
        Helper helper = new Helper();
        int result = helper.validateLogin("test@gmail.com", "password");
        assertEquals(1, result);
    }

    @Test
    public void testValidateLogin_Success() {
        Helper helper = new Helper();
        int result = helper.validateLogin("boss1@gmail.com", "boss1");
        assertEquals(1, result);
    }

    @Test
    public void testIsEmailOrUsernameTakenNotTaken() {
        Helper helper = new Helper();
        assertTrue(helper.isEmailOrUsernameTaken("test@gmail.com", "password"));
    }

    @Test
    public void testIsEmailOrUsernameTaken() {
        Helper helper = new Helper();
        assertTrue(helper.isEmailOrUsernameTaken("boss1@gmail.com", "boss1"));
    }

    @Test
    public void testGetDurationFromMp3Fail() {
        Integer duration = Helper.getDurationFromMp3("/path/to/song.mp3");
        assertNotNull(duration);
        assertTrue(duration > 0);
    }

    @Test
    public void testGetDurationFromMp3() {
        Integer duration = Helper.getDurationFromMp3("C:\\Users\\mihai\\IdeaProjects\\final_project\\songs\\CANDYBOII x RAVA - Kylie Jenner (Official Video).mp3");
        assertNotNull(duration);
        assertTrue(duration > 0);
    }

    @Test
    public void testSongExistsFail() throws SQLException {
        boolean result = Helper.songExists("Artist", "Song Title", 210);
        assertTrue(result);
    }

    @Test
    public void testSongExists() throws SQLException {
        boolean result = Helper.songExists("candyboii", "Kylie Jenner", 208);
        assertTrue(result);
    }

    @Test
    public void testHasEmptyPlaylistForUserFail() {
        Helper helper = new Helper();
        boolean result = helper.hasEmptyPlaylistForUser(1);
        assertFalse(result);
    }

    @Test
    public void testHasEmptyPlaylistForUser() {
        Helper helper = new Helper();
        boolean result = helper.hasEmptyPlaylistForUser(2);
        assertFalse(result);
    }


    @Test
    public void testGetSongTitleFromDBfail() {
        String title = Helper.getSongTitleFromDB("/path/to/song.mp3");
        assertNotNull(title);
        assertEquals("Kylie Jenner", title);
    }

    @Test
    public void testGetSongTitleFromDB() {
        String title = Helper.getSongTitleFromDB("C:\\Users\\mihai\\IdeaProjects\\final_project\\songs\\CANDYBOII x RAVA - Kylie Jenner (Official Video).mp3");
        assertNotNull(title);
        assertEquals("Kylie Jenner", title);
    }



    @Test
    public void testGetSongArtistFromDBfail() {
        String artist = Helper.getSongArtistFromDB("/path/to/song.mp3");
        assertNotNull(artist);
        assertEquals("candyboii", artist);
    }

    @Test
    public void testGetSongArtistFromDB() {
        String artist = Helper.getSongArtistFromDB("C:\\Users\\mihai\\IdeaProjects\\final_project\\songs\\CANDYBOII x RAVA - Kylie Jenner (Official Video).mp3");
        assertNotNull(artist);
        assertEquals("candyboii", artist);
    }
}
  
