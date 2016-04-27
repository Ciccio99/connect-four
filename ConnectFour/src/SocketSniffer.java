//******************************************************************************
//
// File:    SocketSniffer.java
// Package: ---
// Unit:    Class SocketSniffer
//
// This Java source file is copyright (C) 2015 by Alan Kaminsky. All rights
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
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class SocketSniffer is a program that displays the data going back and forth
 * on a socket connection between client(s) and a server. The SocketSniffer
 * program assumes the protocol uses a textual encoding, where each message is a
 * text string terminated with a newline. The SocketSniffer program prints the
 * contents of each message on the console.
 * <P>
 * Usage: <TT>java SocketSniffer <I>serverhost</I> <I>serverport</I>
 * <I>snifferhost</I> <I>snifferport</I></TT>
 * <P>
 * The server is listening for connections to the given <I>serverhost</I> and
 * <I>serverport</I>. The SocketSniffer program is listening for connections to
 * the given <I>snifferhost</I> and <I>snifferport</I>. A client should connect
 * to the SocketSniffer rather than to the server. The SocketSniffer will then
 * connect to the server and forward all messages from the client to the server
 * and vice versa.
 *
 * @author  Alan Kaminsky
 * @version 08-Jul-2015
 */
public class SocketSniffer
{

    // Command line arguments.
    private static InetSocketAddress serverAddress;
    private static InetSocketAddress snifferAddress;

    /**
     * Class ForwardingThread forwards messages from a client to a server or
     * vice versa.
     *
     * @author  Alan Kaminsky
     * @version 08-Jul-2015
     */
    private static class ForwardingThread
            extends Thread
    {
        private static final int MAX = 1024;

        private Socket fromSocket;
        private Socket toSocket;
        private boolean fromClient;
        private InputStream in;
        private OutputStream out;
        private int clientPort;
        private byte[] buf;
        private int len;

        /**
         * Construct a new forwarding thread.
         *
         * @param  fromSocket  Socket from which to read bytes.
         * @param  toSocket    Socket to which to write bytes.
         * @param  fromClient  True if reading from client, false if reading
         *                     from server.
         */
        public ForwardingThread
        (Socket fromSocket,
         Socket toSocket,
         boolean fromClient)
                throws IOException
        {
            this.fromSocket = fromSocket;
            this.toSocket = toSocket;
            this.fromClient = fromClient;
            this.in = fromSocket.getInputStream();
            this.out = toSocket.getOutputStream();
            this.clientPort = (fromClient ? fromSocket : toSocket) .getPort();
            this.buf = new byte [MAX];
            this.len = 0;
        }

        /**
         * Run this forwarding thread.
         */
        public void run()
        {
            try
            {
                printMessage ("[connected]");
                int b;
                while ((b = in.read()) != -1)
                {
                    out.write (b);
                    if (len < MAX)
                        buf[len++] = (byte)b;
                    if (b == '\n')
                    {
                        out.flush();
                        printBuffer();
                        len = 0;
                    }
                }
                printMessage ("[closed]");
            }
            catch (Throwable exc)
            {
                if (! fromSocket.isClosed())
                    synchronized (System.out)
                    {
                        printMessage ("[exception]");
                        exc.printStackTrace (System.out);
                    }
            }
            finally
            {
                try { fromSocket.close(); } catch (IOException exc) { }
                try { toSocket.close(); } catch (IOException exc) { }
            }
        }

        /**
         * Print the given message.
         *
         * @param  msg  Message string.
         */
        private void printMessage
        (String msg)
        {
            synchronized (System.out)
            {
                System.out.printf ("%d %s %s%n",
                        clientPort, fromClient ? "-->" : "<--", msg);
            }
        }

        /**
         * Print the buffer as a string and in hex.
         */
        private void printBuffer()
        {
            synchronized (System.out)
            {
                System.out.printf ("%d %s %s    (hex",
                        clientPort, fromClient ? "-->" : "<--",
                        new String (buf, 0, len - 1));
                for (int i = 0; i < len; ++ i)
                    System.out.printf (" %02x", buf[i]);
                System.out.printf (")%n");
            }
        }
    }

    /**
     * Main program.
     */
    public static void main
    (String[] args)
            throws Exception
    {
        // Parse command line arguments.
        if (args.length != 4) usage();
        serverAddress = new InetSocketAddress
                (args[0], Integer.parseInt (args[1]));
        snifferAddress = new InetSocketAddress
                (args[2], Integer.parseInt (args[3]));

        // Listen for connections from clients.
        ServerSocket snifferSocket = new ServerSocket();
        snifferSocket.bind (snifferAddress);

        // Process each client connection.
        for (;;)
        {
            Socket clientSocket = snifferSocket.accept();
            Socket serverSocket = new Socket();
            serverSocket.connect (serverAddress);
            ForwardingThread c2s = new ForwardingThread
                    (clientSocket, serverSocket, true);
            ForwardingThread s2c = new ForwardingThread
                    (serverSocket, clientSocket, false);
            c2s.start();
            s2c.start();
        }
    }

    /**
     * Print a usage message and exit.
     */
    private static void usage()
    {
        System.err.println ("Usage: java SocketSniffer <serverhost> <serverport> <snifferhost> <snifferport>");
        System.exit (1);
    }

}
