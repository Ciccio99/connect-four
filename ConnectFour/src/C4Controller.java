import java.io.IOException;

/**
 * Connect Four central controller that talks between most components.
 * Also the gateway controller to access read messages and to send messages.
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class C4Controller {

    public int playerNum = 0;
    private String playerName;
    private String enemyName;
    private GameState currGameState;
    private C4ServerConnection gameService;
    private C4ModelController modelController;


    // Various basic states for the game
    public enum GameState {
        WAITING, PLAYER_TURN, ENEMY_TURN, GAME_OVER
    }

    /**
     * Construction of the central game controller
     * @param playerName Player Name
     * @param gameService The Server Game Service
     */
    public C4Controller (String playerName, C4ServerConnection gameService) {
        this.playerName = playerName;
        this.gameService = gameService;
        this.currGameState = GameState.WAITING;
    }

    /**
     * Gives C4Controller access to the model controller
     * @param modelController The controller for the model
     */
    public void addModelController (C4ModelController modelController) {
        this.modelController = modelController;
    }

    /**
     * Initiates game communication with a server over a socket connection
     * @throws Exception
     */
    public void joinGame () throws Exception {
        gameService.out.writeByte('J');
        gameService.out.writeUTF(playerName);
        gameService.out.flush();
        //gameService.sendMessage(C4Messages.JOIN + " " + playerName);
        new ReaderThread() .start();
    }

    /**
     * Accessor method that returns the current game state
     * @return GameState enum
     */
    public GameState getCurrGameState () {
        return this.currGameState;
    }

    /**
     * Takes in a message and sends it to the server
     * @param message
     * @throws Exception
     */
    public void sendGameMessage (String message) throws Exception {
        gameService.sendMessage(message);
    }

    /*
    * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    * Message out functions
    * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    */

    public void addPlayerToken (int c) throws IOException {
        gameService.out.writeByte ('A');
        gameService.out.writeByte (this.playerNum);
        gameService.out.writeByte (c);
        gameService.out.flush();
    }

    public void clearBoard () throws IOException {
        gameService.out.writeByte ('C');
        gameService.out.flush();
    }

    /*
    * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    * Message processing functions
    * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    */

    /**
     * Processes a given message and initiates an action based on the received message.
     * If the message received is erroneous, an error message is printed and the programe exits.
     * @return GameState enum
     */
    private void processMessage () {
        try {
            while (true) {
                byte srvrMsg = gameService.in.readByte();
                int playerNum, r, c;
                String playerName;
                switch (srvrMsg) {
                    case 'I':
                        playerNum = gameService.in.readByte();
                        processNumberMessage(playerNum);
                        break;
                    case 'C':
                        processClearMessage();
                        break;
                    case 'T':
                        playerNum = gameService.in.readByte();
                        processTurnMessage(playerNum);
                        break;
                    case 'N':
                        playerNum = gameService.in.readByte();
                        playerName = gameService.in.readUTF();
                        processNameMessage(playerName);
                        break;
                    case 'A':
                        playerNum = gameService.in.readByte();
                        r = gameService.in.readByte();
                        c = gameService.in.readByte();
                        processAddMessage(playerNum, r, c);
                        break;
                    case 'Q':
                        gameService.socket.close();
                        System.exit(0);
                        break;
                    default:
                        System.err.println("Bad Message");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Connection Error: Terminating client session.");
        } finally {
            try {
                gameService.closeConnection();
            } catch (IOException e) {
                System.err.println("Close Connection Error: Remain calm and carry on.");
            }
        }
        System.exit(1);
    }

    // Message Processing Functions

    /**
     * Takes in a number, assigns it as the player number if none is assigned.
     * @param num Player Number
     * @return Waiting if you are the fist player, otherwise it is the opponents turn
     */
    private void processNumberMessage (int num) {
        if (playerNum == 0 ) {
            playerNum = num;
        }else if (num == 1) {
            currGameState = GameState.WAITING;
        }else {
            currGameState = GameState.ENEMY_TURN;
        }
    }

    /**
     * Activates the name game button on the UI, changes the message on the UI and returns a GameState
     *
     * @param num Player Number
     * @return GAME_OVER if turn number == 0, PLAYER_TURN if turn number == playerNumber, otherwise ENEMY_TURN
     */
    private void processTurnMessage (int num) {
        modelController.activateClearButton();
        if (num == 0 || modelController.isBoardFull()) {
            modelController.changeViewMessage("Game over");
            currGameState = GameState.GAME_OVER;
        } else if (num == playerNum) {
            modelController.changeViewMessage("Your turn");
            currGameState = GameState.PLAYER_TURN;
        } else {
            modelController.changeViewMessage(enemyName + "'s turn");
            currGameState = GameState.ENEMY_TURN;
        }
    }

    /**
     * Assigns the enemy name if the given name is not the player's
     * @param name Player Name
     * @return WAITING
     */
    private void processNameMessage (String name) {
        if (!name.equals(playerName)) {
            enemyName = name;
        }
        currGameState = GameState.WAITING;
    }

    /**
     * Tells a C4ModelController to add a game piece to the board
     * @param p player number
     * @param r row
     * @param c column
     * @return WAITING
     *
     */
    private void processAddMessage (int p, int r, int c) {
        modelController.addPiece(p, r, c);
        currGameState = GameState.WAITING;
    }

    /**
     * Tells a C4ModelController to clear the game board
     * @return GAME_OVER
     */
    private void processClearMessage () {
        modelController.clearBoard();
        currGameState = GameState.GAME_OVER;
    }

    /**
     * A thread class that continuously reads a socket's in stream for any new messages
     * from the connected server
     */
    private class ReaderThread extends Thread {
        public void run () {
            try {
                processMessage();
            } finally {
                try {
                    gameService.closeConnection();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }
}


