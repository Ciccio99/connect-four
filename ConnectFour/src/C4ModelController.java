/**
 * Connect four model controller, handles all external model manipulation calls.
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class C4ModelController {
    private C4Model gameModel;
    private C4UI gameView;

    /**
     * Constructor for C4ModelController, requires a game model to function
     * @param gameModel The game model
     */
    public C4ModelController (C4Model gameModel) {
        this.gameModel = gameModel;
    }

    /**
     * Attached a given C4UI component to this controller
     * @param gameView The game view
     */
    public void addGameView (C4UI gameView) {
        this.gameView = gameView;
    }

    /**
     * Informs the game model to add a piece to the game board
     * @param playerNum Player Number
     * @param r Row
     * @param c Column
     */
    public void addPiece (int playerNum, int r, int c) {
        gameModel.addPlayerToken(playerNum, r, c);
        gameView.repaintBoard();
    }

    /**
     * Informs the game model the clear the board of all pieces
     */
    public void clearBoard () {
        gameModel.clearBoard();
        gameView.repaintBoard();
    }

    /**
     * Takes in a message and informs the game view/UI to update the message box
     * @param message Message to send
     */
    public void changeViewMessage (String message) {
        gameView.changeMessage(message);
    }

    /**
     * Informs GameView to active the new game button
     */
    public void activateClearButton () {
        gameView.activateNewGameButton();
    }

    /**
     * Checks if the game board has filled up with no win condition
     * @return true if it has filled up, false otherwise
     */
    public boolean isBoardFull () {
        return gameModel.checkForBoardFill();
    }
}
