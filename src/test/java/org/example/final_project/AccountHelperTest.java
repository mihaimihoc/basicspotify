package org.example.final_project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountHelperTest {

    @Test
    void testLoadUserData_ValidId() {
        AccountHelper accountHelper = new AccountHelper(1);
        accountHelper.loadUserData();

        assertEquals(1, accountHelper.getId());
        assertEquals("boss1", accountHelper.getUsername());
        assertEquals("boss1@gmail.com", accountHelper.getEmail());
        assertEquals("boss1", accountHelper.getPassword());
        assertEquals("admin", accountHelper.getRank());
    }

    @Test
    void testLoadUserData_InvalidId() {
        AccountHelper accountHelper = new AccountHelper(1);
        accountHelper.loadUserData();

        assertEquals(1, accountHelper.getId());
        assertEquals("boss1", accountHelper.getUsername());
        assertEquals("boss1@gmail.com", accountHelper.getEmail());
        assertEquals("asdfgh", accountHelper.getPassword());
        assertEquals("basic", accountHelper.getRank());
    }

    @Test
    void testLoadUserData_InvalidId1() {
        AccountHelper accountHelper = new AccountHelper(999);
        accountHelper.loadUserData();

        assertEquals(999, accountHelper.getId());
        assertEquals("", accountHelper.getUsername());
        assertEquals("", accountHelper.getEmail());
        assertEquals("", accountHelper.getPassword());
        assertEquals("", accountHelper.getRank());
    }

    @Test
    void testLoadUserData_ValidId2() {
        AccountHelper accountHelper = new AccountHelper(2);
        accountHelper.loadUserData();

        assertEquals(2, accountHelper.getId());
        assertEquals("boss2", accountHelper.getUsername());
        assertEquals("boss2@gmail.com", accountHelper.getEmail());
        assertEquals("boss2", accountHelper.getPassword());
        assertEquals("basic", accountHelper.getRank());
    }

    @Test
    void testLoadUserData_ValidId3() {
        AccountHelper accountHelper = new AccountHelper(2);
        accountHelper.loadUserData();

        assertEquals(2, accountHelper.getId());
        assertEquals("boss2", accountHelper.getUsername());
        assertEquals("boss2@gmail.com", accountHelper.getEmail());
        assertEquals("boss2", accountHelper.getPassword());
        assertEquals("admin", accountHelper.getRank());
    }



}