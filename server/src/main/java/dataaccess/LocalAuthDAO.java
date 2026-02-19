package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;
import java.util.UUID;

public class LocalAuthDAO implements AuthDAO{

    HashSet<AuthData> authDataSet = new HashSet<>();
    @Override
    public void clear() throws DataAccessException {
        authDataSet.clear();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData tempAuthData = new AuthData(UUID.randomUUID().toString(),username);
        authDataSet.add(tempAuthData);
        return tempAuthData;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {

        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
