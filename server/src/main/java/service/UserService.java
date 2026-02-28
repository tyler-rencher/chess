package service;
import dataaccess.DataAccessException;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;

import dataaccess.*;
import model.*;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.util.Collection;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, AlreadyTakenException{
        if(isNull(registerRequest.username())||isNull(registerRequest.password())||isNull(registerRequest.email())){
            throw new BadRequestException("Error: bad request");
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

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException, UnauthorizedException, BadRequestException {
        AuthData userAuth;
        if(isNull(loginRequest.username())||isNull(loginRequest.password())){
            throw new BadRequestException("Error: bad request");
        }
        UserData userData = userDAO.getUser(loginRequest.username());
        if(userData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if(!userData.password().equals(loginRequest.password())){
            throw new UnauthorizedException("Error: unauthorized");
        } else{
            userAuth = authDAO.createAuth(loginRequest.username());
        }
        return new LoginResult(loginRequest.username(), userAuth.authToken());
    }
    public void logout(LogoutRequest logoutRequest) throws DataAccessException, UnauthorizedException {
        AuthData userAuth = authDAO.getAuthData(logoutRequest.authToken());
        if(userAuth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        authDAO.deleteAuth(logoutRequest.authToken());
    }

    public String getAuthToken(String username){
        return authDAO.findAuthTokenFromUsername(username);
    }
    public Collection<String> getAuthTokenCollection(String username){
        return authDAO.findAuthTokenCollectionFromUsername(username);
    }

    private boolean isNull(String item){
        return item == null;
    }
}