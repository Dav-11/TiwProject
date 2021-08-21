package it.polimi.tiw.TiwProject.dao;

import it.polimi.tiw.TiwProject.beans.User;
import it.polimi.tiw.TiwProject.exception.BadLoginException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     *
     * @param email is the mail the user sent as input
     * @param password is the password the user sent as input
     * @return null if no user is found, else it returns the Bean of the user.
     * @throws SQLException if something went wrong
     * @throws BadLoginException if password is wrong
     */
    public User checkCredentials(String email, String password) throws SQLException, BadLoginException {

        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {

                if (!resultSet.isBeforeFirst()) {

                    return null; // no user found
                } else {

                    resultSet.next();
                    String passwordFromDb = resultSet.getString("password");
                    if (password.equals(passwordFromDb)){

                        return new User(
                                resultSet.getInt("id"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getString("email"),
                                resultSet.getString("username")
                        );
                    } else {

                        throw new BadLoginException("Wrong Password");
                    }
                }
            }
        }
    }

    public String getUserUsername(int userId) throws SQLException {

        String query = "SELECT username FROM user WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {

                if (!resultSet.isBeforeFirst()) {

                    return null; // no user found
                } else {

                    resultSet.next();
                    return resultSet.getString("username");
                }
            }
        }
    }
}
