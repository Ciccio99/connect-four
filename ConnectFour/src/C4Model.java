import java.util.ArrayList;

/**
 * Main connect four model that maintains the current state of the game board.
 *
 * @author  Alan Kaminsky
 * @version 13-Oct-2014
 */
public class C4Model implements C4BoardIntf {

    /**
     * Number of rows.
     */
    private static final int ROWS = 6;

    /**
     * Number of columns.
     */
    private static final int COLS = 7;

    private int gameBoard[][];

    private int[] winnerLine;

    /**
     * Constructor for C4Model, instantiating a new game board.
     */
    public C4Model () {
        gameBoard = new int[ROWS][COLS];
        winnerLine = null;
    }

    /**
     * Adds a player token on the game board
     * @param playerNum Player number
     * @param r Row
     * @param c Column
     */
    public void addPlayerToken (int playerNum, int r, int c) {
        gameBoard[r][c] = playerNum;
        winnerLine = checkForWin(playerNum, r, c);
    }

    /**
     * Checks if the given player has entered a win condition
     * @param playerNum Player number
     * @param row Row
     * @param col Column
     * @return If win condition, an array with the row and columns of the first game peice and last game piece
     *          that makes up the winning line. If NOT a win condition, return null
     */
    private int[] checkForWin (int playerNum, int row, int col) {
        ArrayList<winMarker> aux = new ArrayList<>();
        // Vertical Check
        for (int r = row - 3; r <= row + 3; r++) {
            if (hasPlayerMarker(playerNum, r, col)) {
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
            if (hasPlayerMarker(playerNum, row + i, col + i)) {
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
            if (hasPlayerMarker(playerNum, row + i, col - i)) {
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
            if (hasPlayerMarker(playerNum, row, c)) {
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

    /**
     * Clears the board of game pieces for a new games and resets the winning game line
     */
    public void clearBoard () {
        gameBoard = new int[ROWS][COLS];
        winnerLine = null;
    }

    /**
     * Checks if the entire board has been filled up, preventing players from continuing the game
     * @return true if filled, false otherwise
     */
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
     * Checks if the given row/column location has a piece belonging to the specified player
     * @param playerNum Player Number
     * @param r Row
     * @param c Column
     * @return true if location has the specified player piece, false otherwise
     */
    private boolean hasPlayerMarker (int playerNum, int r, int c) {
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) {
            return false;
        } else if (gameBoard[r][c] == playerNum) {
            return true;
        }
        return false;
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

    /**
     * Marker class that hold row and column locations.
     * Used to store possible win condition lines
     */
    private class winMarker {
        public int r;
        public int c;

        public winMarker (int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}
