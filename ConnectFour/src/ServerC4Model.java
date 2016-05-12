import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
/**
 * Main connect four model that maintains the current state of the game board on the server.
 *
 * @author  Alberto Scicali
 */
public class ServerC4Model implements ViewListener {
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
    Player player1;
    Player player2;
    Player currPlayerTurn;
    private LinkedList<ViewProxy> viewProxies =
            new LinkedList<ViewProxy>();

    /**
     * Constructor for C4Model, instantiating a new game board.
     */
    public ServerC4Model () {
        gameBoard = new int[ROWS][COLS];
        winnerLine = null;
    }

    /**
     * Add the given model listener to this C4 model.
     * @param  proxy Model listener.
     */
    public synchronized void addProxy (ViewProxy proxy) {
        viewProxies.add (proxy);
    }

    /**
     * Instantiates new Player objects with the given player names and numbers
     * @param num the player number
     * @param name the player name
     * */
    public synchronized void addPlayer (int num, String name) {
        if (num == 1)
            player1 = new Player(name, num);
        else
            player2 = new Player(name, num);
    }

    /**
     * Initiates the game and informs the players with info about the opposite player and who's turn it is.
     * */
    public synchronized void initiateGame () {
        currPlayerTurn = player1;
        Iterator<ViewProxy> iter = viewProxies.iterator();
        while (iter.hasNext())
        {
            ViewProxy listener = iter.next();
            try
            {
                listener.informNewPlayer(player1.num, player1.name);
                listener.informNewPlayer(player2.num, player2.name);
                listener.playerTurn(player1.num);
            }
            catch (IOException exc)
            {
                // Client failed, stop reporting to it.
                iter.remove();
            }
        }
    }

    /**
     * Adds a player token on the game board
     * @param playerNum Player number
     * @param c Column
     */
    public synchronized void addPlayerToken (int playerNum, int c) {
        if (!hasWon() && !isColFull(c)) {
            int r;
            for (r = ROWS - 1; r > 0; r--) {
                if (gameBoard[r][c] == 0) break;
            }

            gameBoard[r][c] = playerNum;
            winnerLine = checkForWin(playerNum, r, c);
            Iterator<ViewProxy> iter = viewProxies.iterator();
            swapCurrentPlayerTurn();
            while (iter.hasNext())
            {
                ViewProxy listener = iter.next();
                try
                {
                    listener.playerTokenAdded(playerNum, r, c);
                    listener.playerTurn(currPlayerTurn.num);
                }
                catch (IOException exc)
                {
                    // Client failed, stop reporting to it.
                    iter.remove();
                }
            }
        }
    }

    /**
     * Checks if the given game board column is completely filled with token or not
     * @param c The column tot check
     * @return true if filled, false otherwise
     * */
    private boolean isColFull (int c) {
        for (int r = 0; r < ROWS; r++) {
            if (gameBoard[r][c] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Swap current players
     * */
    private synchronized void swapCurrentPlayerTurn () {
        if (currPlayerTurn.num == 1) {
            currPlayerTurn = player2;
        } else {
            currPlayerTurn = player1;
        }
    }

    /**
     * Checks if the given player has entered a win condition
     * @param playerNum Player number
     * @param row Row
     * @param col Column
     * @return If win condition, an array with the row and columns of the first game peice and last game piece
     *          that makes up the winning line. If NOT a win condition, return null
     */
    private synchronized int[] checkForWin (int playerNum, int row, int col) {
        ArrayList<ServerC4Model.winMarker> aux = new ArrayList<>();
        // Vertical Check
        for (int r = row - 3; r <= row + 3; r++) {
            if (hasPlayerMarker(playerNum, r, col)) {
                aux.add(new ServerC4Model.winMarker(r, col));
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
                aux.add(new ServerC4Model.winMarker(row + i, col + i));
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
                aux.add(new ServerC4Model.winMarker(row + i, col - i));
                if (aux.size() == 4) {
                    return new int[] {aux.get(0).r, aux.get(0).c, aux.get(3).r, aux.get(3).c};
                }
            } else {
                aux.clear();
            }
        }
        // Horizontal Check
        for (int c = col - 3; c <= row + 3; c++) {
            if (hasPlayerMarker(playerNum, row, c)) {
                aux.add(new ServerC4Model.winMarker(row, c));
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
     * Join the given session.
     *
     * @param  proxy    Reference to view proxy object.
     * @param  session  Session name.
     *
     * @exception IOException
     *     Thrown if an I/O error occurred.
     */
    public void join (ViewProxy proxy, String session) throws IOException {}

    /**
     * Clears the board of game pieces for a new games and resets the winning game line
     */
    public synchronized void clearBoard () {
        gameBoard = new int[ROWS][COLS];
        winnerLine = null;
        currPlayerTurn = player1;
        Iterator<ViewProxy> iter = viewProxies.iterator();
        while (iter.hasNext())
        {
            ViewProxy listener = iter.next();
            try
            {
                listener.boardCleared();
                listener.playerTurn(currPlayerTurn.num);
            }
            catch (IOException exc)
            {
                // Client failed, stop reporting to it.
                iter.remove();
            }
        }
    }

    /**
     * Informs the players the the game session is over and then closes the socket connection for this listener.
     * */
    public synchronized void destroyGameSession () {
        Iterator<ViewProxy> iter = viewProxies.iterator();
        while (iter.hasNext())
        {
            ViewProxy listener = iter.next();
            try
            {
                if (listener.socket.isConnected()) {
                    listener.playerQuit();
                }
            }
            catch (IOException exc)
            {
                // Client failed, stop reporting to it.
                iter.remove();
            }
        }
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
     * Determine if one player or the other has won; that is, has four markers
     * in a row horizontally, vertically, or diagonally. If so, an array of four
     * integers (r1, c1, r2, c2) is returned, where (r1, c1) is the row/column
     * of the first of the four markers and (r2, c2) is the row/column of the
     * last of the four markers. If neither player has won, null is returned.
     *
     * @return  Array of (r1, c1, r2, c2), or null.
     */
    public boolean hasWon() {
        if (winnerLine != null) {
            return true;
        }

        return false;
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

    /**
     * Player class
     * Holds all the information about player, including name and number
     * */
    private class Player {
        public String name;
        public int num;

        /**
         * Player object constructor
         * @param name Player namer
         * @param num player number
         * */
        public Player (String name, int num) {
            this.name = name;
            this.num = num;
        }
    }

}
