package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LocalGameDAO implements GameDAO{
    HashSet<GameData> gameDataSet = new HashSet<>();
    int gameID = 0;
    @Override
    public void clear() throws DataAccessException {
        gameDataSet.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        gameID++;
        gameDataSet.add(new GameData(gameID,null,null,gameName,new ChessGame()));
        return gameID;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }
}
