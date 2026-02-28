package service;
import dataaccess.DataAccessException;
import dataaccess.AlreadyTakenException;
import dataaccess.UserNotFoundException;

import dataaccess.*;
import model.*;
import service.Requests.LoginRequest;
import service.Requests.LogoutRequest;
import service.Requests.RegisterRequest;
import service.Results.LoginResult;
import service.Results.RegisterResult;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, AlreadyTakenException{
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

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException, UnauthorizedException, UserNotFoundException {
        AuthData userAuth;
        if(isNull(loginRequest.username())||isNull(loginRequest.password())){
            throw new UserNotFoundException("Error: bad request");
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

    private boolean isNull(String item){
        return item == null;
    }
}