package com.yourcompany.ems.services;

import com.yourcompany.ems.database.UserDAO;
import com.yourcompany.ems.database.DatabaseConnection;
import com.yourcompany.ems.models.User;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        UserDAO tempUserDAO = null;
        try {
            Connection connection = DatabaseConnection.getConnection();
            tempUserDAO = new UserDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.userDAO = tempUserDAO;
    }

    public User login(String username, String password) {
        if (userDAO == null) {
            System.err.println("UserDAO not initialized due to connection error.");
            return null;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                User.setCurrentUser(user);
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User getLoggedInUser() {
        return User.getCurrentUser();
    }
}

