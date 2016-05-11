import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class ViewProxy provides the network proxy for the view object in the Network
 * Go Game. The view proxy resides in the server program and communicates with
 * the client program.
 *
 * @author  Alan Kaminsky
 * @version 21-Jan-2010
 */
public class ViewProxy
{

// Hidden data members.

    public Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ViewListener viewListener;
    private int mySessionId;
    private SessionManager managerReference;

// Exported constructors.

    /**
     * Construct a new view proxy.
     *
     * @param  socket  Socket.
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public ViewProxy
    (Socket socket)
            throws IOException
    {
        this.socket = socket;
        out = new DataOutputStream (socket.getOutputStream());
        in = new DataInputStream (socket.getInputStream());
    }

// Exported operations.

    /**
     * Set the view listener object for this view proxy.
     *
     * @param  viewListener  View listener.
     */
    public void setViewListener
    (ViewListener viewListener)
    {
        if (this.viewListener == null)
        {
            this.viewListener = viewListener;
            new ReaderThread() .start();
        }
        else
        {
            this.viewListener = viewListener;
        }
    }

    public void setManagerReference (SessionManager manager) {
        this.managerReference = manager;
    }

    public void setSessionId (int id) {
        this.mySessionId = id;
    }


    /**
     * Report that a marker was placed on the Go board.
     * @param  playerNum  player id
     * @param  r      Row on which marker was placed.
     * @param  c      Column on which marker was placed.
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public void playerTokenAdded (int playerNum, int r, int c) throws IOException {
        out.writeByte ('A');
        out.writeByte (playerNum);
        out.writeByte (r);
        out.writeByte (c);
        out.flush();
    }

    /**
     * Informs player who's current turn it is
     * @oaram playerNum current player's number
     * */
    public void playerTurn (int playerNum) throws IOException {
        System.out.println ("MEOWMEOWMEO");
        out.writeByte ('T');
        out.writeByte (playerNum);
        out.flush();
    }

    /**
     * Informs the player what number ID they are
     * @param playerNum number of player
     * */
    public void informPlayerNumber (int playerNum) throws IOException {
        System.out.println("Informing player of number");
        out.writeByte('I');
        out.writeByte(playerNum);
        out.flush();
    }

    /**
     * Informs the player of the newly joined player
     * @param num player number.
     * @param name player name
     * */
    public void informNewPlayer (int num, String name) throws IOException {
        System.out.println("Informing of NEW PLAYER");

        out.writeByte('N');
        out.writeByte(num);
        out.writeUTF(name);
        out.flush();
    }

    /**
     * Report that the Go board was cleared.
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public void boardCleared () throws IOException {
        out.writeByte ('C');
        out.flush();
    }

    /**
     * Informs that a player has quit and the connections should disconnect
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     * */
    public void playerQuit () throws IOException {
        System.out.println("Other player left");
        out.writeByte('Q');
        out.flush();
        socket.close();
        managerReference.killSession(mySessionId);
    }


// Hidden helper classes.

    /**
     * Class ReaderThread receives messages from the network, decodes them, and
     * invokes the proper methods to process them.
     *
     * @author  Alan Kaminsky
     * @version 19-Jan-2010
     */
    private class ReaderThread
            extends Thread
    {
        public void run()
        {
            try
            {
                for (;;)
                {
                    if (!socket.isClosed()) {
                        String playerName;
                        int playerNum, c;
                        byte[] b = new byte[1];
                        in.read(b);
                        System.out.printf("WHAT THE BYTE READ %d\n", b[0]);
                        switch (b[0])
                        {
                            case 'J':
                                playerName = in.readUTF();
                                System.out.println(playerName);
                                viewListener.join (ViewProxy.this, playerName);
                                break;
                            case 'A':
                                playerNum = in.readByte();
                                c = in.readByte();
                                viewListener.addPlayerToken (playerNum, c);
                                break;
                            case 'C':
                                viewListener.clearBoard();
                                break;
                            case 0:
                                System.out.println("Dude left game");
                                viewListener.destroyGameSession();
                                socket.close();
                                break;
                            default:
                                System.err.println ("Bad message");
                                break;
                        }
                    } else {
                        break;
                    }
                }
            }
            catch (IOException exc)
            {
            }
            finally
            {
                try
                {
                    if (socket.isConnected()) {
                        socket.close();
                    }
                }
                catch (IOException exc)
                {
                }
            }
        }
    }

}