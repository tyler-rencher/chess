package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDAO{
    HashSet<GameData> gameDataSet = new HashSet<>();
    int gameID = 0;

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` varchar(256) NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameID),
              INDEX(gameName)
            )
            """
    };

    @Override
    public void clear() {
        gameDataSet.clear();
    }

    @Override
    public int createGame(String gameName){
        gameID++;
        gameDataSet.add(new GameData(gameID,null,null,gameName,new ChessGame()));
        return gameID;
    }

    @Override
    public void updateGame(GameData gameData) {
        for (GameData game : gameDataSet) {
            if (game.gameID() == gameData.gameID()) {
                gameDataSet.remove(game);
                gameDataSet.add(gameData);
            }
        }
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData game : gameDataSet) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return gameDataSet;
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
