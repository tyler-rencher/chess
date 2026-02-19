package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface AuthDAO {

    //clear User Data
    void clear() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

}
