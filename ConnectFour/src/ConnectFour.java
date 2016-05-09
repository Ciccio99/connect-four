import java.io.*;

/**
 * Main running class for the Connect Four game. Orchestrates the initiation of the game and the necessary components.
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class ConnectFour {

    /**
     * Main method that starts the application
     *
     * @param args Host, Port, Player name
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 3) usage();
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String playerName = args[2];
        C4Model gameModel = new C4Model();
        C4ModelController modelController = new C4ModelController(gameModel);
        C4Controller gameController = new C4Controller(playerName, new C4ServerConnection(host, port));
        gameController.addModelController(modelController);

        C4ViewController viewController = new C4ViewController(gameController);

        C4UI gameView = new C4UI(gameModel, playerName, viewController);
        modelController.addGameView(gameView);
        beginGame(gameController);
    }

    /**
     * Initiates the start of the game by informing the game controller to join a server session.
     *
     * @param gameController The main game controller
     * @throws Exception
     */
    private static void beginGame (C4Controller gameController) throws Exception {
        gameController.joinGame();
    }

    /**
     * Print a usage message and exit.
     */
    private static void usage()
    {
        System.err.println ("Usage: java ConnectFour <host> <port> <playername>");
        System.exit (1);
    }
}
