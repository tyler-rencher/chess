package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;

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
    public void createUser(UserData userData) {
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        String json = new Gson().toJson(pet);
        int id = executeUpdate(statement, pet.name(), pet.type(), json);
        return new Pet(id, pet.name(), pet.type());
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
