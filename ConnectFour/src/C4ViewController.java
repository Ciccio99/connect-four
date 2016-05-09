/**
 * View controller for the game which connects user actions on the UI with the main game controller
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class C4ViewController {

    C4Controller gameController;

    /**
     * Constructor for the C4ViewController
     * @param gameController The game controller
     */
    public C4ViewController (C4Controller gameController) {
        this.gameController = gameController;
    }

    /**
     * Informs the game controller to send a message to the server that a piece is to be added.
     * @param c Column
     * @throws Exception
     */
    public void addPiece (int c) throws Exception {
        if (gameController.getCurrGameState() == C4Controller.GameState.PLAYER_TURN) {
            gameController.sendGameMessage(C4Messages.ADD + " " + gameController.playerNum + " " + c);
        }
    }

    /**
     * Informs the game controller to send a message to the server that the board needs to be cleared.
     * @throws Exception
     */
    public void clearBoard () throws Exception {
        gameController.sendGameMessage(C4Messages.CLEAR);
    }
}
