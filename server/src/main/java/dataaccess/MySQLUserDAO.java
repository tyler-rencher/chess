package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySQLUserDAO implements UserDAO{
    HashSet<UserData> userDataSet = new HashSet<>();

    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(username)
            )
            """
    };

    @Override
    public void clear() {
        userDataSet.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        int id = executeUpdate(statement, userData.username(), hashedPassword, userData.email());
    }

    @Override
    public UserData getUser(String username){
        for (UserData user : userDataSet) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private int executeUpdate(String statement, UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(statement)) {
                stmt.setString(1, userData.username());
                stmt.setString(2, userData.password());
                stmt.setString(3, userData.email());

                if(stmt.executeUpdate() == 1) {
                    System.out.println("Updated userData " + userData.getId());
                } else {
                    System.out.println(
                            "Failed to update book " + book.getId());
                }
            } catch(SQLException ex) {
                // ERROR
                throw DataAccessException("UR MOM GAY");
            }

        }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
