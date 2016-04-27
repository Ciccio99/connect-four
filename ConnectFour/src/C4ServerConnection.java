import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by alberto on 4/25/16.
 */
public class C4ServerConnection {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public C4ServerConnection(String host, int port) throws Exception {
        SocketAddress sock_addr = new InetSocketAddress(host, port);
        socket = new Socket();
        socket.connect(sock_addr);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String receiveMessage () throws IOException {
        String received_message = null;
        while (received_message == null) {
            if (isSocketClosed()) {
                System.out.println("CONNECTION HAS CLOSED!");
                received_message = C4Messages.CONNECTION_CLOSED;
            } else {
                received_message = in.readLine();
            }
        }
        System.out.println(received_message);
        return received_message;
    }

    public void sendMessage (String message) throws Exception {
        out.println(message);
    }

    public boolean isSocketClosed () {
        return socket.isClosed();
    }

    public void closeConnection() throws IOException {
        socket.close();
    }
}
