package service;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import model.requests.LoginRequest;
import model.requests.LogoutRequest;
import model.requests.RegisterRequest;
import model.results.LoginResult;
import model.results.RegisterResult;

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
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        UserData newUser = new UserData(registerRequest.username(),hashedPassword,registerRequest.email());
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
        if(!BCrypt.checkpw(loginRequest.password(), userData.password())){
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

    public String getUsernameFromAuthToken(String authToken) throws DataAccessException{
        if(authToken == null){
            throw new DataAccessException("Error: authToken null");
        }
        return authDAO.getAuthData(authToken).username();
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