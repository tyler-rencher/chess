package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDAO{

    public MySQLAuthDAO() throws DataAccessException {
        DatabaseManager.configureDatabase(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken),
              INDEX(username)
            )
            """
    };

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE authData";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException{
        var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();
        DatabaseManager.executeUpdate(statement, authToken, username);
        return new AuthData(authToken,username);
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM authData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        var statement = "DELETE FROM authData WHERE authToken = ?;";
        if(authToken == null){
            throw new DataAccessException("Error provided authToken is null");
        }
        DatabaseManager.executeUpdate(statement, authToken);
    }

    public String findAuthTokenFromUsername(String username){
        return null;
    }
    public Collection<String> findAuthTokenCollectionFromUsername(String username){
        HashSet<String> authTokens = new HashSet<>();
        return authTokens;
    }
}
