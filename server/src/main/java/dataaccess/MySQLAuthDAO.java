package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLAuthDAO implements AuthDAO{

    HashSet<AuthData> authDataSet = new HashSet<>();

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
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
        var statement = "TRUNCATE userData";
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(String username){
        AuthData tempAuthData = new AuthData(UUID.randomUUID().toString(),username);
        authDataSet.add(tempAuthData);
        return tempAuthData;
    }

    @Override
    public AuthData getAuthData(String authToken) {
        for (AuthData auth : authDataSet) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        for (AuthData auth : authDataSet) {
            if (auth.authToken().equals(authToken)) {
                authDataSet.remove(auth);
                break;
            }
        }
    }

    public String findAuthTokenFromUsername(String username){
        for (AuthData auth : authDataSet) {
            if (auth.username().equals(username)) {
                return auth.authToken();
            }
        }
        return null;
    }
    public Collection<String> findAuthTokenCollectionFromUsername(String username){
        HashSet<String> authTokens = new HashSet<>();
        for (AuthData auth : authDataSet) {
            if (auth.username().equals(username)) {
                authTokens.add(auth.authToken());
            }
        }
        return authTokens;
    }

    private String executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                            ps.setNull(i + 1, NULL);
                        }
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getString(1);
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
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
