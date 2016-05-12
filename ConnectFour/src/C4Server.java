import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class C4Server is the server main program for the Network C4 Game. The
 * command line arguments specify the host and port to which the server should
 * listen for connections.
 * <P>
 * Usage: java C4Server <I>host</I> <I>port</I>
 *
 * @author  Alan Kaminsky
 * @author Alberto Scicali
 */
public class C4Server
{

    /**
     * Main program.
     */
    public static void main (String[] args) throws Exception {
        if (args.length != 2) usage();
        String host = args[0];
        int port = Integer.parseInt (args[1]);

        ServerSocket serversocket = new ServerSocket();
        serversocket.bind (new InetSocketAddress (host, port));

        SessionManager manager = new SessionManager();

        while (true) {
            Socket socket = serversocket.accept();
            ViewProxy proxy = new ViewProxy (socket);
            proxy.setViewListener (manager);
        }
    }

    /**
     * Print a usage message and exit.
     */
    private static void usage()
    {
        System.err.println ("Usage: java C4Server <host> <port>");
        System.exit (1);
    }

}

