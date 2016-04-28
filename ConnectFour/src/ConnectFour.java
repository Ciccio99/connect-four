import java.io.*;

/**
 * Main running class for the Connect Four game.
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class ConnectFour {

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

    public static void beginGame (C4Controller gameController) throws Exception {
        C4Model gameModel = new C4Model();
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
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
