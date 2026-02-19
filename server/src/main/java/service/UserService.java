package service;
import dataaccess.DataAccessException;
import dataaccess.*;
import model.*;

import javax.xml.crypto.Data;


public class UserService {
    private final UserDAO userDAO = new LocalUserDAO();
    private final AuthDAO authDAO = new LocalAuthDAO();
    //public RegisterResult register(RegisterRequest registerRequest) {}
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        UserData userData = userDAO.getUser(loginRequest.username());
        if(!userData.password().equals(loginRequest.password())){
            throw new DataAccessException("AHHHH");
        } else{
            authDAO.createAuth(loginRequest.username());
        }
    }
    //public void logout(LogoutRequest logoutRequest) {}
}