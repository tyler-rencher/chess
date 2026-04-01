package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.ArrayList;
import java.util.Collection;

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
            serverFacade.clear();
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
        try{
            serverFacade.clear();
        } catch(Exception e){
            fail("failure in After All Block");
        }
        server.stop();
    }

    @Test
    @Order(1)
    public void registerPositiveTest(){
        try {
            String[] params = {"t", "t", "t"};
            assertDoesNotThrow(() -> serverFacade.registerUser(params));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(2)
    public void registerNegativeTest(){
        try {
            String[] params = {"t", "t", "t"};
            assertThrows(ResponseException.class,
                    () -> serverFacade.registerUser(params));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(3)
    public void loginPositiveTest() {
        try {
            String[] params = {"t", "t"};
            assertDoesNotThrow(() -> serverFacade.loginUser(params));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(4)
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
    @Order(5)
    public void logoutPositiveTest() {
        try{
            String[] param = {"w", "w", "w"};
            String authToken3 = serverFacade.registerUser(param);
            assertDoesNotThrow(() -> serverFacade.logoutUser(authToken3));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(6)
    public void logoutNegativeTest() {
        try {
            assertThrows(ResponseException.class,
                    () -> serverFacade.logoutUser(authToken));
        } catch(Exception e){
            fail();
        }
    }




    @Test
    @Order(7)
    public void createPositiveTest() {
        try{
            int number = serverFacade.createGame(authToken2,"testGame");
            assertEquals(1,number);
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(8)
    public void createNegativeTest() {
        try{
            assertThrows(ResponseException.class,
                    () -> serverFacade.createGame(authToken2,null));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(9)
    public void listPositiveTest() {
        try{
            Collection<GameData> listActual = serverFacade.listGames(authToken2);
            Collection<GameData> listExpected = new ArrayList<>();
            GameData game = new GameData(1,"l",null,"testGame",new ChessGame());
            listExpected.add(game);
            assertEquals(listExpected,listActual);
        } catch(Exception e){
            fail();
        }
    }
    @Test
    @Order(10)
    public void listNegativeTest() {
        try{
            assertThrows(ResponseException.class, () -> serverFacade.listGames(null));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(11)
    public void joinPositiveTest() {
        try{
            serverFacade.joinGame(authToken2, ChessGame.TeamColor.WHITE,1);
            Collection<GameData> listActual = serverFacade.listGames(authToken2);
            Collection<GameData> listExpected = new ArrayList<>();
            GameData game = new GameData(1,"l",null,"testGame",new ChessGame());
            listExpected.add(game);
            assertEquals(listExpected,listActual);
        } catch(Exception e){
            fail();
        }
    }
    @Test
    @Order(12)
    public void joinNegativeTest() {
        try{
            assertThrows(ResponseException.class,
                    () -> serverFacade.joinGame(authToken2, ChessGame.TeamColor.WHITE,1));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(13)
    public void clearTest() {
        try{
            assertDoesNotThrow(() -> serverFacade.clear());
        } catch(Exception e){
            fail();
        }
    }

}
