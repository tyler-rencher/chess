package service;
import dataaccess.AlreadyTakenException;
import dataaccess.UnauthorizedException;
import dataaccess.UserNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Results.LoginResult;
import service.Results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private static UserService userService;

    @BeforeAll
    public static void setUp()  {
        userService = new UserService();
    }

    @Test
    public void registerPositiveTest() {
        try {
            RegisterRequest request = new RegisterRequest("u","p","e");
            RegisterResult resultActual = userService.register(request);
            RegisterResult resultExpected = new RegisterResult("u",";lskjdf");
            assertEquals(resultExpected.username(),resultActual.username()); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    public void registerNegativeTest() {
        try {
            RegisterRequest request = new RegisterRequest("us","p","e");
            userService.register(request);
            assertThrows(AlreadyTakenException.class,
                    () -> userService.register(request));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void loginPositiveTest() {
        try {
            LoginRequest request = new LoginRequest("u","p");
            LoginResult resultActual = userService.login(request);
            LoginResult resultExpected = new LoginResult("u",";lskjdf");
            assertEquals(resultExpected.username(),resultActual.username()); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    public void loginNegativeUnauthorizedTest() {
        try {
            RegisterRequest testRegister = new RegisterRequest("ut","pt","et");
            userService.register(testRegister);
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
}