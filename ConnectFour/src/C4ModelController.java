/**
 * Main running class for the Connect Four game.
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class C4ModelController {
    C4Model gameModel;
    C4UI gameView;

    public C4ModelController (C4Model gameModel) {
        this.gameModel = gameModel;
    }

    public void addGameView (C4UI gameView) {
        this.gameView = gameView;
    }

    public void addPiece (int playerNum, int r, int c) {
        gameModel.addPlayerToken(playerNum, r, c);
        gameView.repaintBoard();
    }

    public void clearBoard () {
        gameModel.clearBoard();

        gameView.repaintBoard();
    }

    public void changeViewMessage (String message) {
        gameView.changeMessage(message);
    }

    public void activateClearButton () {
        gameView.activateNewGameButton();
    }

    public boolean isBoardFull () {
        return gameModel.checkForBoardFill();
    }
}
