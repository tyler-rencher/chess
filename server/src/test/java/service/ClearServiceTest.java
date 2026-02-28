package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

public class ClearServiceTest {
    private static UserService userService;
    private static RegisterRequest registerTest;
    private static String authTokenTest;

    @BeforeAll
    public static void setUp()  {

    }
    @BeforeEach
    public void beforeEach(){
        try {
            userService = new UserService(new LocalUserDAO(), new LocalAuthDAO());
            registerTest = new RegisterRequest("ut","pt","et");
            userService.register(registerTest);
            authTokenTest = userService.getAuthToken("ut");
        } catch(Exception e){
            fail("Exception in Before Block");
        }
    }

    @Test
    public void clearPositiveTest() {
        try {
            LogoutRequest request = new LogoutRequest(authTokenTest);
            assertDoesNotThrow(() -> userService.logout(request));
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

}