import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Consumer;

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

    private int[] winnerLine;

    public C4Model () {
        gameBoard = new int[ROWS][COLS];
        winnerLine = null;
    }

    public void addPlayerToken (int playerNum, int r, int c) {
        gameBoard[r][c] = playerNum;
        winnerLine = winCheck(playerNum, r, c);
    }

    private int[] winCheck (int playerNum, int row, int col) {
        if (playerNum == 1) {
            return checkForPlayerOneWin(row, col);
        } else {
            return checkForPlayerTwoWin(row, col);
        }
    }

    private int[] checkForPlayerOneWin (int row, int col) {
        ArrayList<winMarker> aux = new ArrayList<>();
        // Vertical Check
        for (int r = row - 3; r <= row + 3; r++) {
            if (hasPlayer1Marker(r, col)) {
                aux.add(new winMarker(r, col));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        // Diagonal top-left to bot-Right Check
        for (int i = - 3; i <= 3; i++) {
            if (hasPlayer1Marker(row + i, col + i)) {
                aux.add(new winMarker(row + i, col + i));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        // Diagonal top-right to bot-left Check
        for (int i = -3; i <= 3; i++) {
            if (hasPlayer1Marker(row + i, col - i)) {
                aux.add(new winMarker(row + i, col - i));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        // Vertical Check
        for (int c = col - 3; c <= row + 3; c++) {
            if (hasPlayer1Marker(row, c)) {
                aux.add(new winMarker(row, c));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        return null;
    }

    private int[] checkForPlayerTwoWin (int row, int col) {
        ArrayList<winMarker> aux = new ArrayList<>();
        // Vertical Check
        for (int r = row - 3; r <= row + 3; r++) {
            if (hasPlayer2Marker(r, col)) {
                aux.add(new winMarker(r, col));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        // Diagonal top-left to bot-Right Check
        for (int i = - 3; i <= 3; i++) {
            if (hasPlayer2Marker(row + i, col + i)) {
                aux.add(new winMarker(row + i, col + i));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        // Diagonal top-right to bot-left Check
        for (int i = -3; i <= 3; i++) {
            if (hasPlayer2Marker(row + i, col - i)) {
                aux.add(new winMarker(row + i, col - i));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        // Vertical Check
        for (int c = col - 3; c <= row + 3; c++) {
            if (hasPlayer2Marker(row, c)) {
                aux.add(new winMarker(row, c));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        return null;
    }

    public void clearBoard () {
        gameBoard = new int[ROWS][COLS];
        winnerLine = null;
    }

    public boolean checkForBoardFill() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (gameBoard[r][c] == 0) {
                    return false;
                }
            }
        }
        return true;
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
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) {
            return false;
        } else if (gameBoard[r][c] == 1) {
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
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) {
            return false;
        } else if (gameBoard[r][c] == 2) return true;
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
        return winnerLine;
    }

    private class winMarker {
        public int r;
        public int c;

        public winMarker (int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}
