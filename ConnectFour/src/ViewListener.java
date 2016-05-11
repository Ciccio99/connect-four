import java.io.IOException;

/**
 * Interface ViewListener specifies the interface for an object that is
 * triggered by events from the view object in the Network Go Game.
 *
 * @author  Alan Kaminsky
 * @version 07-Jan-2010
 */
public interface ViewListener
{

    /**
     * Join the given session.
     *
     * @param  proxy    Reference to view proxy object.
     * @param  session  Session name.
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public void join (ViewProxy proxy, String session) throws IOException;

    /**
     * Place a marker on the Go board.
     * @param  playerNum  Player Number
     * @param  r      Row on which to place the marker.
     * @param  c      Column on which to place the marker.
     *
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public void addPlayerToken (int playerNum, int c) throws IOException;

    /**
     * Clear the Go board.
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public void clearBoard() throws IOException;
}