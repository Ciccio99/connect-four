/**
 * The methods in this class are queried to
 * display the game board.
 *
 * @author  Alan Kaminsky
 * @version 13-Oct-2014
 */
public class C4Model implements C4BoardIntf {

    /**
     * Number of rows.
     */
    public static final int ROWS = 6;

    /**
     * Number of columns.
     */
    public static final int COLS = 7;

    private int gameBoard[][];

    public C4Model () {
        gameBoard = new int[ROWS][COLS];
    }

    public void addPlayerToken (int playerNum, int r, int c) {
        gameBoard[r][c] = playerNum;
    }

    public void clearBoard () {
        gameBoard = new int[ROWS][COLS];
    }

    /**
     * Determine if the given row and column contains player 1's marker.
     *
     * @param  r  Row.
     * @param  c  Column.
     *
     * @return  True if (r, c) contains player 1's marker, false otherwise.
     */
    public boolean hasPlayer1Marker (int r, int c) {
        if (gameBoard[r][c] == 1) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the given row and column contains player 2's marker.
     *
     * @param  r  Row.
     * @param  c  Column.
     *
     * @return  True if (r, c) contains player 2's marker, false otherwise.
     */
    public boolean hasPlayer2Marker (int r, int c) {
        if (gameBoard[r][c] == 2) return true;
        return false;
    }

    /**
     * Determine if one player or the other has won; that is, has four markers
     * in a row horizontally, vertically, or diagonally. If so, an array of four
     * integers (r1, c1, r2, c2) is returned, where (r1, c1) is the row/column
     * of the first of the four markers and (r2, c2) is the row/column of the
     * last of the four markers. If neither player has won, null is returned.
     *
     * @return  Array of (r1, c1, r2, c2), or null.
     */
    public int[] hasWon() {
        return null;
    }
}
