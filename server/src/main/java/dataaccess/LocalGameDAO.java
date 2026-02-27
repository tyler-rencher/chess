package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LocalGameDAO implements GameDAO{
    HashSet<AuthData> gameDataSet = new HashSet<>();
    @Override
    public void clear() throws DataAccessException {
        gameDataSet.clear();
    }

    @Override
    public void createGame(String gameName) throws DataAccessException {

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
