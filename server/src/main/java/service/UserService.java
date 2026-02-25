package service;
import dataaccess.DataAccessException;
import dataaccess.*;
import model.*;
import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Results.LoginResult;
import service.Results.RegisterResult;


public class UserService {
    private final UserDAO userDAO = new LocalUserDAO();
    private final AuthDAO authDAO = new LocalAuthDAO();
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{
        UserData userData = userDAO.getUser(registerRequest.username());
        if(userData != null){
            throw new DataAccessException("AlreadyTakenException");
        }
        UserData newUser = new UserData(registerRequest.username(),registerRequest.password(),registerRequest.email());
        userDAO.createUser(newUser);
        AuthData authData = authDAO.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(),authData.authToken());
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        AuthData userAuth;
        UserData userData = userDAO.getUser(loginRequest.username());
        if(!userData.password().equals(loginRequest.password())){
            throw new DataAccessException("AHHHH"); //This should be Unauthorized error
        } else{
            userAuth = authDAO.createAuth(loginRequest.username());
        }
        return new LoginResult(loginRequest.username(), userAuth.authToken());
    }
    //public void logout(LogoutRequest logoutRequest) {}
}