package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.Requests.CreateGameRequest;
import service.Requests.JoinGameRequest;
import service.Requests.ListGamesRequest;
import service.Results.CreateGameResult;
import service.Results.ListGamesResult;

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

        return new CreateGameResult(String.valueOf(gameID));
    }
    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException, UnauthorizedException, BadRequestException {
        AuthData userAuth = authDAO.getAuthData(joinGameRequest.authToken());
        if(isNull(joinGameRequest.playerColor())){
            throw new BadRequestException("Error: bad request");
        }
        if(userAuth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if(gameData == null){
            throw new BadRequestException("Error: bad request");
        }
        //authDAO.deleteAuth(logoutRequest.authToken());
    }

    public String getAuthToken(String username){
        return authDAO.findAuthTokenFromUsername(username);
    }

    private boolean isNull(String item){
        return item == null;
    }
}
