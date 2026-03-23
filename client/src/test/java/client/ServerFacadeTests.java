package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import model.requests.*;
import model.results.CreateGameResult;
import model.results.ListGamesResult;
import model.results.LoginResult;
import model.results.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;
import java.util.HashSet;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static  String authToken;
    private static  String authToken2;

    @BeforeAll
    public static void init() {
        server = new Server();

        var port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
        try{
            String[] params = {"q", "q", "q"};
            authToken = serverFacade.registerUser(params);
            String[] params2 = {"l", "l", "l"};
            authToken2 = serverFacade.registerUser(params2);
        } catch(Exception e){
            fail("Fail in Setup");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPositiveTest(){
        try {
            String[] params = {"t", "t", "t"};
            assertDoesNotThrow(() -> serverFacade.registerUser(params));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void registerNegativeTest(){
        try {
            String[] params = {"q", "q", "q"};
            assertThrows(ResponseException.class,
                    () -> serverFacade.registerUser(params));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void loginPositiveTest() {
        try {
            String[] params = {"t", "t"};
            assertDoesNotThrow(() -> serverFacade.loginUser(params));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void loginNegativeTest(){
        try {
            String[] params = {"e", "e"};
            assertThrows(ResponseException.class,
                    () -> serverFacade.loginUser(params));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void logoutPositiveTest() {
        try{
            assertDoesNotThrow(() -> serverFacade.logoutUser(authToken));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void logoutNegativeTest() {
        try {
            assertThrows(ResponseException.class,
                    () -> serverFacade.logoutUser(null));
        } catch(Exception e){
            fail();
        }
    }




    @Test
    public void createPositiveTest() {
        try{
            int number = serverFacade.createGame(authToken2,"testGame");
            assertEquals(1,number);
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void createNegativeTest() {
        try{
            assertThrows(ResponseException.class,
                    () -> serverFacade.createGame(authToken2,null));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void listPositiveTest() {
        try{
            Collection<GameData> listActual = serverFacade.listGames(authToken2);
            Collection<GameData> listExpected = new HashSet<>();
            GameData game = new GameData(1,null,null,"testGame",new ChessGame());
            listExpected.add(game);
            assertEquals(listExpected,listActual);
        } catch(Exception e){
            fail();
        }
    }
    @Test
    public void listNegativeTest() {
        try{
            assertThrows(ResponseException.class, () -> serverFacade.listGames(null));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void joinPositiveTest() {
        try{
            serverFacade.joinGame(authToken2, ChessGame.TeamColor.WHITE,1);
            Collection<GameData> listActual = serverFacade.listGames(authToken2);
            Collection<GameData> listExpected = new HashSet<>();
            GameData game = new GameData(1,"l",null,"testGame",new ChessGame());
            listExpected.add(game);
            assertEquals(listExpected,listActual);
        } catch(Exception e){
            fail();
        }
    }
    @Test
    public void joinNegativeTest() {
        try{
            assertThrows(ResponseException.class,
                    () -> serverFacade.joinGame(authToken2, ChessGame.TeamColor.WHITE,1));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    public void clearTest() {
        try{
            assertDoesNotThrow(() -> serverFacade.clear());
        } catch(Exception e){
            fail();
        }
    }

}
