/**
 * Denise Moran, 1367222
 * CSS 430, Spring 2017
 * Homework 3, Exercise 4.18
 *
 * Modify the socket-based date server (Figure 3.26) in Chapter 3 so that the server services each client
 * request using a thread pool (Silberschatz, Operating Systems Concepts 8E, pg 183).
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class DateServer
{
    private static final int POOL_SIZE = 10; // defines the number of available threads

    /**
     * Default DateServer constructor
     *
     * Creates a thread pool and waits for a client to connect, then services the
     * client with one of the available pools.
     */
    public DateServer()
    {
        try
        {
            // declare variables for server and pool
            ServerSocket sock = new ServerSocket(6013);
            ArrayList<Session> sessions  = new ArrayList<Session>();
            ArrayList<Thread> myThreads = new ArrayList<Thread>();

            int round = 0;
            // now listen for connections
            while (true)
            {
                // initialize POOL_SIZE threads at a time
                for (int i = 0; i < POOL_SIZE; i++)
                {
                    sessions.add (0, new Session());                   // insert session to index 0
                    myThreads.add(0, new Thread(sessions.get(0)));     // create thread, dont start
                }

                // uses up POOL_SIZE threads
                for (int i = 0; i < POOL_SIZE; i++)
                {
                    System.out.println("Session " + round +  i);
                    Socket client = sock.accept();                          // connect to client
                    sessions.get(i).setClient(client);                      // pass client to session thread
                    myThreads.get(i).start();
                }

                // clears out threads
                sessions.clear();
                myThreads.clear();
                round++;
            }
        }
        catch (IOException ioe)
        {
            System.err.println("Error starting " + ioe);
        }
    }

    /**
     * Session class
     *
     * Holds the data and code when each thread is run.
     */
    public class Session implements Runnable
    {
        Socket client;          // client accessing server

        /**
         * Set client
         *
         * Modifies the client instance variable.
         *
         * @param client The client accessing the server for given session
         */
        public void setClient(Socket client)
        {
            this.client = client;
        }

        /**
         * Run
         *
         * Required method to implement Runnable interface. Does the servicing work of each
         * thread, which prints the date to the client's standard output.
         */
        public void run()
        {
            try
            {
                System.out.print("Working ... ");
                PrintWriter pout = new PrintWriter(client.getOutputStream(), true);
                // write the Date to the socket
                pout.println(new java.util.Date().toString());
                // close the socket and resume
                // listening for connections
                client.close();
                System.out.println("Done");
            }
            catch (IOException ioe)
            {
                System.err.println(ioe);
            }
        }
    }

    /**
     * Main method
     *
     * Creates an instance of the Date Server
     *
     * @param args Command line arguments
     */
    public static void main(String[] args)
    {
        DateServer ds = new DateServer();
    }
}