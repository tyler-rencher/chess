package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserDAOTests {

    private static UserDAO userDAOSQL;
    private static UserData testUserData;
    private static UserData originalUser;

    @BeforeAll
    public static void setUp()  {
        testUserData = new UserData("u","p","e");
        originalUser = new UserData("o","o","o");

        try {
            userDAOSQL = new MySQLUserDAO();
            userDAOSQL.clear();
            userDAOSQL.createUser(originalUser);
        }catch(Exception e){
            fail("Exception in setUP");
        }
    }
    @BeforeEach
    public void beforeEach(){

    }

    @Test
    public void clearPositiveTest() {
        try {
            assertDoesNotThrow(() -> userDAOSQL.clear());
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

    @Test
    public void createUserPositiveTest() {
        try {
            assertDoesNotThrow(() -> userDAOSQL.createUser(testUserData));

        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

    @Test
    public void createUserNegativeTest() {
        try {
            assertThrows(DataAccessException.class,
                    () -> userDAOSQL.createUser(testUserData));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void getUserPositiveTest() {
        try {
            UserData resultActual = userDAOSQL.getUser(originalUser.username());
            assertEquals(originalUser,resultActual); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    public void getUserNegativeTest() {
        try {
            assertNull(userDAOSQL.getUser("NULL"));
        } catch(Exception e){
            fail();
        }
    }



}