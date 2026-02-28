package dataaccess;

import model.*;

import java.util.Collection;

public interface GameDAO {

    //clear User Data
    void clear();

    int createGame(String gameName);

    void updateGame(GameData gameData);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

}
