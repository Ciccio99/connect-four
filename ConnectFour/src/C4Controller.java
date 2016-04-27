import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by alberto on 4/25/16.
 */
public class C4Controller {

    public int playerNum = 0;
    public String playerName;
    public String enemyName;
    public GameState currGameState;
    private C4ServerConnection gameService;
    private C4ModelController modelController;

    // Various basic states for the game
    public enum GameState {
        WAITING, PLAYER_TURN, ENEMY_TURN, GAME_OVER
    }

    public C4Controller (String playerName, C4ServerConnection gameService) {
        this.playerName = playerName;
        this.gameService = gameService;
        this.currGameState = GameState.WAITING;
    }

    public void addModelController (C4ModelController modelController) {
        this.modelController = modelController;
    }

    public void joinGame () throws Exception {
        gameService.sendMessage(C4Messages.JOIN + " " + playerName);
        System.out.println(C4Messages.JOIN + " " + playerName);
        new ReaderThread() .start();
    }

    public GameState getCurrGameState () {
        return this.currGameState;
    }

    public GameState processMessage (String message) {
        System.out.println(message);
        String protocol[] = message.split(" ");
        String srvrMsg = protocol[0];

        // Number Message
        if (srvrMsg.equals(C4Messages.NUMBER)) {
            playerNum = Integer.parseInt(protocol[1]);
            if (playerNum == 1) {
                return GameState.WAITING;
            } else {
                return GameState.ENEMY_TURN;
            }
        }

        // Turn Message
        if (srvrMsg.equals(C4Messages.TURN)) {
            modelController.activateClearButton();
            int pNum = Integer.parseInt(protocol[1]);
            if (pNum == 0) {
                modelController.changeViewMessage("Game over");
                return GameState.GAME_OVER;
            } else if (pNum == playerNum) {
                modelController.changeViewMessage("Your turn");
                return GameState.PLAYER_TURN;
            } else {
                modelController.changeViewMessage(enemyName + "'s turn");
                return GameState.ENEMY_TURN;
            }
        }

        // Name message
        if (srvrMsg.equals(C4Messages.NAME)) {
            if (!protocol[2].equals(playerName)) {
                enemyName = protocol[2];
            }
            return GameState.WAITING;
        }

        // Add message
        if (srvrMsg.equals(C4Messages.ADD)) {
            modelController.addPiece(Integer.parseInt(protocol[1]), Integer.parseInt(protocol[2]), Integer.parseInt(protocol[3]));
            return GameState.WAITING;
        }

        // Clear message
        if (srvrMsg.equals(C4Messages.CLEAR)) {
            modelController.clearBoard();
            return GameState.GAME_OVER;
        }

        if (srvrMsg.equals(C4Messages.CONNECTION_CLOSED)) {
            System.exit(0);
        }

        System.err.println("Failed to process message: '" + message + "'");
        try {
            gameService.closeConnection();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.exit(1);
        return GameState.WAITING;
    }

    public String waitForGameMessage () throws IOException {
        return gameService.receiveMessage();
    }

    public void sendGameMessage (String message) throws Exception {
        gameService.sendMessage(message);
    }

    public boolean isGameConnected () {
        return !gameService.isSocketClosed();
    }

    private class ReaderThread extends Thread {
        public void run () {
            try {
                while (true) {
                    currGameState = processMessage(waitForGameMessage());
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
        }
    }
}


