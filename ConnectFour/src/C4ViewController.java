/**
 * Created by alberto on 4/26/16.
 */
public class C4ViewController {

    C4Controller gameController;

    public C4ViewController (C4Controller gameController) {
        this.gameController = gameController;
    }

    public void addPiece (int c) throws Exception {
        if (gameController.getCurrGameState() == C4Controller.GameState.PLAYER_TURN) {
            gameController.sendGameMessage(C4Messages.ADD + " " + gameController.playerNum + " " + c);
        }
    }

    public void clearBoard () throws Exception {
        gameController.sendGameMessage(C4Messages.CLEAR);
    }
}
