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

    /**
     * Checks if the socket connection is still open.
     * @return true if socket is open, false otherwise
     */
    public boolean isGameConnected () {
        return !gameService.isSocketClosed();
    }

    /**
     * Processes a given message and initiates an action based on the received message.
     * If the message received is erroneous, an error message is printed and the programe exits.
     * @return GameState enum
     */
    private GameState processMessage () {

        try {
            byte srvrMsg = gameService.in.readByte();
            int playerNum, r, c;
            String playerName;

            while (true) {
                switch (srvrMsg) {
                    case 'I':
                        playerNum = gameService.in.readByte();
                        System.out.printf("I've been informed of number! Number: %d\n", playerNum);
                        processNumberMessage(playerNum);
                        break;
                    case 'C':
                        System.out.println("Clear ya board");
                        processClearMessage();
                        break;
                    case 'T':
                        System.out.println("Who's Turn is it?");
                        playerNum = gameService.in.readByte();
                        processTurnMessage(playerNum);
                        break;
                    case 'N':
                        System.out.println("Number and name info");
                        playerNum = gameService.in.readByte();
                        playerName = gameService.in.readUTF();
                        processNameMessage(playerName);
                        break;
                    case 'A':
                        System.out.println("Adding piece");
                        playerNum = gameService.in.readByte();
                        r = gameService.in.readByte();
                        c = gameService.in.readByte();
                        processAddMessage(playerNum, r, c);
                        break;
                    default:
                        System.err.println("Bad Message");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                gameService.closeConnection();
            } catch (IOException e) {
                System.err.println(e);
            }
        }


////        // Number Message
////        if (srvrMsg.equals(C4Messages.NUMBER)) {
////            return processNumberMessage(Integer.parseInt(protocol[1]));
////        }
////
////        // Turn Message
////        if (srvrMsg.equals(C4Messages.TURN)) {
////            return processTurnMessage(Integer.parseInt(protocol[1]));
////        }
////
////        // Name message
////        if (srvrMsg.equals(C4Messages.NAME)) {
////            return processNameMessage(protocol[2]);
////        }
////
////        // Add message
////        if (srvrMsg.equals(C4Messages.ADD)) {
////            return processAddMessage(Integer.parseInt(protocol[1]), Integer.parseInt(protocol[2]), Integer.parseInt(protocol[3]));
////        }
////
////        // Clear message
////        if (srvrMsg.equals(C4Messages.CLEAR)) {
////            return processClearMessage();
////        }
////
////        if (srvrMsg.equals(C4Messages.CONNECTION_CLOSED)) {
////            System.exit(0);
////        }
////
////        System.err.println("Failed to process message: '" + message + "'");
////        try {
////            gameService.closeConnection();
////        } catch (IOException e) {
////            System.err.println(e.getMessage());
////        }
////        System.exit(1);
        return GameState.WAITING;
    }

    // Message Processing Functions

    /**
     * Takes in a number, assigns it as the player number if none is assigned.
     * @param num Player Number
     * @return Waiting if you are the fist player, otherwise it is the opponents turn
     */
    private GameState processNumberMessage (int num) {
        if (playerNum == 0 ) {
            playerNum = num;
        }
        if (num == 1) {
            return GameState.WAITING;
        }
        return GameState.ENEMY_TURN;
    }

    /**
     * Activates the name game button on the UI, changes the message on the UI and returns a GameState
     *
     * @param num Player Number
     * @return GAME_OVER if turn number == 0, PLAYER_TURN if turn number == playerNumber, otherwise ENEMY_TURN
     */
    private GameState processTurnMessage (int num) {
        modelController.activateClearButton();
        if (num == 0 || modelController.isBoardFull()) {
            modelController.changeViewMessage("Game over");
            return GameState.GAME_OVER;
        } else if (num == playerNum) {
            modelController.changeViewMessage("Your turn");
            return GameState.PLAYER_TURN;
        } else {
            modelController.changeViewMessage(enemyName + "'s turn");
            return GameState.ENEMY_TURN;
        }
    }

    /**
     * Assigns the enemy name if the given name is not the player's
     * @param name Player Name
     * @return WAITING
     */
    private GameState processNameMessage (String name) {
        if (!name.equals(playerName)) {
            enemyName = name;
        }
        return GameState.WAITING;
    }

    /**
     * Tells a C4ModelController to add a game piece to the board
     * @param p player number
     * @param r row
     * @param c column
     * @return WAITING
     *
     */
    private GameState processAddMessage (int p, int r, int c) {
        modelController.addPiece(p, r, c);
        return GameState.WAITING;
    }

    /**
     * Tells a C4ModelController to clear the game board
     * @return GAME_OVER
     */
    private GameState processClearMessage () {
        modelController.clearBoard();
        return GameState.GAME_OVER;
    }

    /**
     * A thread class that continuously reads a socket's in stream for any new messages
     * from the connected server
     */
    private class ReaderThread extends Thread {
        public void run () {
            try {
                while (isGameConnected()) {
                    currGameState = processMessage();
                }
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


