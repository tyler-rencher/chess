package dataaccess;

import model.*;

import java.util.Collection;

public interface GameDAO {

    //clear User Data
    void clear() throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

}
