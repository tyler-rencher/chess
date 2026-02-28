package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class LocalGameDAO implements GameDAO{
    HashSet<GameData> gameDataSet = new HashSet<>();
    int gameID = 0;
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
}
