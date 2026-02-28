package dataaccess;

import model.*;

public interface AuthDAO {

    //clear User Data
    void clear() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuthData(String authToken);

    void deleteAuth(String authToken) throws DataAccessException;

    public String findAuthTokenFromUsername(String username);
}
