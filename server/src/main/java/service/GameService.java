package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.Requests.CreateGameRequest;
import service.Requests.LogoutRequest;
import service.Requests.RegisterRequest;
import service.Results.CreateGameResult;
import service.Results.RegisterResult;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public RegisterResult listGames(RegisterRequest registerRequest) throws DataAccessException, AlreadyTakenException {
        if(isNull(registerRequest.username())||isNull(registerRequest.password())||isNull(registerRequest.email())){
            throw new UserNotFoundException("Error: bad request");
        }
        UserData userData = userDAO.getUser(registerRequest.username());
        if(userData != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData newUser = new UserData(registerRequest.username(),registerRequest.password(),registerRequest.email());
        userDAO.createUser(newUser);
        AuthData authData = authDAO.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(),authData.authToken());
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
    public void joinGame(LogoutRequest logoutRequest) throws DataAccessException, UnauthorizedException {
        AuthData userAuth = authDAO.getAuthData(logoutRequest.authToken());
        if(userAuth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        authDAO.deleteAuth(logoutRequest.authToken());
    }

    public String getAuthToken(String username){
        return authDAO.findAuthTokenFromUsername(username);
    }

    private boolean isNull(String item){
        return item == null;
    }
}
