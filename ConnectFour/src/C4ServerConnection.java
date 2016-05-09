import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Server service that enables socket connection to a given host and port.
 * Also capable to receive and send messages over the protocol
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class C4ServerConnection {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * C4ServerConnection constructor, accepts a host and port as parameters to create a socket connection
     * @param host The server host
     * @param port The server port
     * @throws Exception
     */
    public C4ServerConnection(String host, int port) throws Exception {
        SocketAddress sock_addr = new InetSocketAddress(host, port);
        socket = new Socket();
        socket.connect(sock_addr);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Attempts to receive a message from the in socket stream until a message arrives or until the socket is closed.
     * @return the recived message
     * @throws IOException
     */
    public String receiveMessage () throws IOException {
        String received_message = null;
        while (received_message == null) {
            if (isSocketClosed()) {
                received_message = C4Messages.CONNECTION_CLOSED;
            } else {
                received_message = in.readLine();
            }
        }
        return received_message;
    }

    /**
     * Sends a given message to the socket out stream
     * @param message Message to be sent
     * @throws Exception
     */
    public void sendMessage (String message) throws Exception {
        out.println(message);
    }

    /**
     * Checks if the socket connection has closed
     * @return true if the connections closed, false otherwise
     */
    public boolean isSocketClosed () {
        return socket.isClosed();
    }

    /**
     * Closes the socket connection.
     * @throws IOException
     */
    public void closeConnection() throws IOException {
        socket.close();
    }
}
