package dataaccess;

import model.*;

import java.util.Collection;

public interface AuthDAO {

    //clear User Data
    void clear() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuthData(String authToken);

    void deleteAuth(String authToken) throws DataAccessException;

    String findAuthTokenFromUsername(String username);
    Collection<String> findAuthTokenCollectionFromUsername(String username);
}
