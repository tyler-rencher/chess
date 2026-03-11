package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{

    public MySQLUserDAO() throws DataAccessException {
        DatabaseManager.configureDatabase(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userData (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            )
            """
    };

    @Override
    public void clear() throws DataAccessException{
        var statement = "TRUNCATE userData";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        DatabaseManager.executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM userData WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var passwordHashed = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, passwordHashed,email);
    }

}
