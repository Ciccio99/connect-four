/**
 * Maintains common messages frequently used through the connect four game to
 * communicate with the server back and forth.
 *
 * @author Alberto Scicali
 * @version 0.1.0
 */
public class C4Messages {

    // Client --> Server Messages
    public static final String JOIN = "join";

    // Server --> Client Messages
    public static final String NUMBER = "number";
    public static final String NAME = "name";
    public static final String TURN = "turn";

    // Both Client --> Server & Server --> Client
    public static final String ADD = "add";
    public static final String CLEAR = "clear";

    // Internal client message
    public static final String CONNECTION_CLOSED = "connection_closed";
}
