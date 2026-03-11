package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private static AuthDAO authDAOSQL;
    private static AuthData originalAuth;
    private static String testUsername;

    @BeforeAll
    public static void setUp()  {

        try {
            authDAOSQL = new MySQLAuthDAO();
            authDAOSQL.clear();
            originalAuth = authDAOSQL.createAuth("o");
            testUsername = "u";
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
            assertDoesNotThrow(() -> authDAOSQL.clear());
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

    @Test
    public void createAuthPositiveTest() {
        try {
            assertDoesNotThrow(() -> authDAOSQL.createAuth(testUsername));

        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

    @Test
    public void createAuthNegativeTest() {
        try {
            assertThrows(DataAccessException.class,
                    () -> authDAOSQL.createAuth(null));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void getAuthPositiveTest() {
        try {
            AuthData resultActual = authDAOSQL.getAuthData(originalAuth.authToken());
            assertEquals(originalAuth,resultActual); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    public void getAuthNegativeTest() {
        try {
            assertNull(authDAOSQL.getAuthData("NULL"));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void deleteAuthPositiveTest() {
        try {
            assertDoesNotThrow(() -> authDAOSQL.deleteAuth(originalAuth.authToken()));
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    public void deleteAuthNegativeTest() {
        try {
            assertThrows(DataAccessException.class,
                    () -> authDAOSQL.deleteAuth(null));
        } catch(Exception e){
            fail();
        }
    }



}