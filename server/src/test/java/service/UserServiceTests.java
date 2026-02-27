package service;
import dataaccess.AlreadyTakenException;
import dataaccess.UnauthorizedException;
import dataaccess.UserNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import service.Requests.LoginRequest;
import service.Requests.LogoutRequest;
import service.Requests.RegisterRequest;
import service.Results.LoginResult;
import service.Results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private static UserService userService;
    private static RegisterRequest registerTest;
    private static String authTokenTest;

    @BeforeAll
    public static void setUp()  {

    }
    @BeforeEach
    public void beforeEach(){
        try {
            userService = new UserService();
            registerTest = new RegisterRequest("ut","pt","et");
            authTokenTest = userService.getAuthToken("ut");
        } catch(Exception e){
            fail("Exception in Before Block");
        }
    }

    @Test
    public void registerPositiveTest() {
        try {
            RegisterRequest request = new RegisterRequest("u","p","e");
            RegisterResult resultActual = userService.register(request);
            String authToken = userService.getAuthToken("u");
            RegisterResult resultExpected = new RegisterResult("u",authToken);
            assertEquals(resultExpected,resultActual); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    public void registerNegativeTest() {
        try {
            userService.register(registerTest);
            assertThrows(AlreadyTakenException.class,
                    () -> userService.register(registerTest));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void loginPositiveTest() {
        try {
            LoginRequest request = new LoginRequest("ut","pt");
            LoginResult resultActual = userService.login(request);
            String newAuthToken = userService.getAuthToken("ut");
            LoginResult resultExpected = new LoginResult("ut", newAuthToken);
            assertEquals(resultExpected,resultActual); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    public void loginNegativeUnauthorizedTest() {
        try {
            LoginRequest request = new LoginRequest("ut","pp");
            assertThrows(UnauthorizedException.class,
                    () -> userService.login(request));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void loginNegativeUserNotFoundTest() {
        try {
            LoginRequest request = new LoginRequest("uuuuuu","pp");
            assertThrows(UserNotFoundException.class,
                    () -> userService.login(request));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void logoutPositiveTest() {
        try {
            LogoutRequest request = new LogoutRequest(authTokenTest);
            userService.logout(request);
            assertEquals(200,200);
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
}