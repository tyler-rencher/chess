package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface UserDAO {

    //clear User Data
    void clear() throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

}
