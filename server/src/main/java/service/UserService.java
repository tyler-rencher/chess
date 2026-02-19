package service;
import dataaccess.DataAccessException;
import dataaccess.*;
import model.*;


public class UserService {
    private final UserDAO userDAO = new LocalUserDAO();
    private final AuthDAO authDAO = new LocalAuthDAO();
    //public RegisterResult register(RegisterRequest registerRequest) {}
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