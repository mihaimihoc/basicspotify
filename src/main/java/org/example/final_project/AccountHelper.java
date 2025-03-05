package org.example.final_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountHelper {

    int id;
    String username;
    String email;
    String password;
    String rank;

    public AccountHelper(int id) {
        this.id = id;
        this.username = "";
        this.email = "";
        this.password = "";
        this.rank = "";
    }

    public void loadUserData() {
        Helper helper = new Helper();
        String query = "SELECT username, email, password, rank FROM accounts WHERE id = ?";
        try (Connection connection = helper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                this.username = resultSet.getString("username");
                this.email = resultSet.getString("email");
                this.password = resultSet.getString("password");
                this.rank = resultSet.getString("rank");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRank() {
        return rank;
    }
}
