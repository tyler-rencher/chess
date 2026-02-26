package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;

public class LocalUserDAO implements UserDAO{
    HashSet<UserData> userDataSet = new HashSet<>();

    @Override
    public void clear() throws DataAccessException {
        userDataSet.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        userDataSet.add(userData);
    }

    @Override
    public UserData getUser(String username){

        for (UserData user : userDataSet) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
