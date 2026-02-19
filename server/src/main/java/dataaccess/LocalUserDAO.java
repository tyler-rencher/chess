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

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userDataSet) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("You were the chosen One Anakin"); //this should probably be a User Not Found error
    }
}
