package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.ListGamesRequest;
import model.results.CreateGameResult;
import model.results.ListGamesResult;

import java.util.Objects;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException, UnauthorizedException {
        AuthData userAuth = authDAO.getAuthData(listGamesRequest.authToken());
        if(userAuth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException, UnauthorizedException, BadRequestException {
        AuthData userAuth = authDAO.getAuthData(createGameRequest.authToken());
        if(isNull(createGameRequest.gameName())){
            throw new BadRequestException("Error: bad request");
        }

        if(userAuth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        int gameID = gameDAO.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }
    public void joinGame(JoinGameRequest joinGameRequest)
            throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        AuthData userAuth = authDAO.getAuthData(joinGameRequest.authToken());
        if(joinGameRequest.playerColor() == null){
            throw new BadRequestException("Error: bad request");
        }
        if(userAuth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        int gameID = joinGameRequest.gameID();
        GameData gameData = gameDAO.getGame(gameID);
        if(gameData == null){
            throw new BadRequestException("Error: bad request");
        }
        ChessGame.TeamColor playerColor = joinGameRequest.playerColor();
        String gameName = gameData.gameName();
        String username = userAuth.username();
        if(playerColor == ChessGame.TeamColor.WHITE){
            if(gameData.whiteUsername() == null){
                GameData newGame = new GameData(gameID, username,gameData.blackUsername(),gameName,gameData.game());
                gameDAO.updateGame(newGame);
            } else{
                throw new AlreadyTakenException("Error: already taken");
            }
        }
        if(playerColor == ChessGame.TeamColor.BLACK){
            if(gameData.blackUsername() == null){
                GameData newGame = new GameData(gameID, gameData.whiteUsername(),username,gameName,gameData.game());
                gameDAO.updateGame(newGame);
            } else{
                throw new AlreadyTakenException("Error: already taken");
            }
        }

    }

    public ChessGame getChessGame(int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        return game.game();
    }

    public void updateGame(ChessGame game, int gameId) throws DataAccessException{
        GameData oldData = gameDAO.getGame(gameId);
        GameData newData = new GameData(gameId,oldData.whiteUsername(),oldData.blackUsername(),oldData.gameName(),game);
        gameDAO.updateGame(newData);
    }
    public void removeUserFromGame(int gameId, String username) throws DataAccessException{
        GameData oldData = gameDAO.getGame(gameId);
        String newWhite = oldData.whiteUsername();
        String newBlack = oldData.blackUsername();
        if(Objects.equals(newBlack, username)){
            newBlack = null;
        }
        if(Objects.equals(newWhite, username)){
            newWhite = null;
        }
        GameData newData = new GameData(gameId,newWhite,newBlack, oldData.gameName(),oldData.game());
        gameDAO.updateGame(newData);
    }
    public GameData getGameData(int gameId) throws DataAccessException{
        return gameDAO.getGame(gameId);
    }

    private boolean isNull(String item){
        return (item == null) || (item.isEmpty());
    }
}
