/**
 * Created by alberto on 4/26/16.
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
