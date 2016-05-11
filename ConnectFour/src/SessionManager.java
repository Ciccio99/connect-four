//******************************************************************************
//
// File:    SessionManager.java
// Package: ---
// Unit:    Class SessionManager
//
// This Java source file is copyright (C) 2010 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 3 of the License, or (at your option) any
// later version.
//
// This Java source file is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You may obtain a copy of the GNU General Public License on the World Wide Web
// at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import java.io.IOException;
import java.util.HashMap;

/**
 * Class SessionManager maintains the sessions' model objects in the Network Go
 * Game server.
 *
 * @author  Alan Kaminsky
 * @version 21-Jan-2010
 */
public class SessionManager implements ViewListener {

// Hidden data members.
    private int waitingSessionId;
    private boolean sessionWaiting;
    private HashMap<Integer,ServerC4Model> sessions = new HashMap<Integer,ServerC4Model>();


// Exported constructors.

    /**
     * Construct a new session manager.
     */
    public SessionManager () {
        waitingSessionId = 0;
        sessionWaiting = false;
    }

// Exported operations.

    /**
     * Join the given session.
     *
     * @param  proxy    Reference to view proxy object.
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public synchronized void join(ViewProxy proxy, String playerName) throws IOException {
        int session;
        int playerNum;
        if (sessionWaiting) {
            session = waitingSessionId;
            sessionWaiting = false;
        } else {
            session = ++waitingSessionId;
            sessionWaiting = true;
        }
        ServerC4Model model = sessions.get (session);
        if (model == null)
        {
            model = new ServerC4Model();
            playerNum = 1;
            model.addPlayer(playerNum, playerName);
            sessions.put (session, model);
        } else {
            playerNum = 2;
            model.addPlayer(playerNum, playerName);
        }
        System.out.printf("Session begin filled: %d\n", session);
        proxy.informPlayerNumber(playerNum);
        model.addProxy (proxy);
        proxy.setViewListener (model);
        if (playerNum == 2) {
            System.out.printf("Game started!\n");
            model.initiateGame();
        }
    }

    /**
     * Place a marker on the Go board.
     *
     * @param  playerNum The number of the player adding the token.
     * @param  c      Column on which to place the marker.
     */
    public void addPlayerToken (int playerNum, int c) {}

    /**
     * Clear the Go board.
     */
    public void clearBoard() {}

}