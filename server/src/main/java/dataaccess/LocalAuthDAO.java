package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class LocalAuthDAO implements AuthDAO{

    HashSet<AuthData> authDataSet = new HashSet<>();
    @Override
    public void clear() throws DataAccessException {
        authDataSet.clear();
    }

    @Override
    public AuthData createAuth(String username){
        AuthData tempAuthData = new AuthData(UUID.randomUUID().toString(),username);
        authDataSet.add(tempAuthData);
        return tempAuthData;
    }

    @Override
    public AuthData getAuthData(String authToken) {
        for (AuthData auth : authDataSet) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        for (AuthData auth : authDataSet) {
            if (auth.authToken().equals(authToken)) {
                authDataSet.remove(auth);
                break;
            }
        }
    }
}
