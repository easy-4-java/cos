// Copyright (C) 1998-2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.oreilly.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * A superclass for HTTP servlets that wish to accept raw socket 
 * connections.  DaemonHttpServlet 
 * starts listening for client requests in its &lt;tt&gt;init()&lt;/tt&gt; method
 * and stops listening in its &lt;tt&gt;destroy()&lt;/tt&gt; method.  In between,
 * for every connection it receives, it calls the abstract 
 * &lt;tt&gt;handleClient(Socket client)&lt;/tt&gt; method.  This method should
 * be implemented by the servlet subclassing DaemonHttpServlet.
 * The port on which the servlet is to listen is determined by the 
 * &lt;tt&gt;getSocketPort()&lt;/tt&gt; method.
 *
 * @see com.oreilly.servlet.RemoteDaemonHttpServlet
 *
 * @author &lt;b&gt;Jason Hunter&lt;/b&gt;, Copyright &#169; 1998
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @version 1.0, 98/09/18
 */
public abstract class DaemonHttpServlet extends HttpServlet {

  /**
   * The default listening port (1313)
   */
  protected int DEFAULT_PORT = 1313;
  private Thread daemonThread;

  /**
   * Begins a thread listening for socket connections.  Subclasses
   * that override this method must be sure to first call 
   * &lt;tt&gt;super.init(config)&lt;/tt&gt;.
   * 
   * @param config the servlet config
   * @exception ServletException if a servlet exception occurs
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    try {
      daemonThread = new Daemon(this);
      daemonThread.start();
    }
    catch (Exception e) {
      log("Problem starting socket server daemon thread" +
          e.getClass().getName() + ": " + e.getMessage());
    }
  }

  /**
   * Returns the socket port on which the servlet will listen.
   * A servlet can change the port in three ways: by using the 
   * &lt;tt&gt;socketPort&lt;/tt&gt; init parameter, by setting the &lt;tt&gt;DEFAULT_PORT&lt;/tt&gt;
   * variable before calling &lt;tt&gt;super.init()&lt;/tt&gt;, or by overriding this
   * method's implementation.
   *
   * @return the port number on which to listen
   */
  protected int getSocketPort() {
    try { return Integer.parseInt(getInitParameter("socketPort")); }
    catch (NumberFormatException e) { return DEFAULT_PORT; }
  }

  /**
   * Handles a new socket connection.  Subclasses must define this method.
   *
   * @param client the client socket
   */
  abstract public void handleClient(Socket client);

  /**
   * Halts the thread listening for socket connections.  Subclasses
   * that override this method must be sure to first call 
   * &lt;tt&gt;super.destroy()&lt;/tt&gt;.
   */
  public void destroy() {
    try {
      daemonThread.interrupt();
      daemonThread = null;
    }
    catch (Exception e) {
      log("Problem stopping server socket daemon thread: " +
          e.getClass().getName() + ": " + e.getMessage());
    }
  }
}

// This work is broken into a helper class so that subclasses of
// DaemonHttpServlet can define their own run() method without problems.

class Daemon extends Thread {

  private ServerSocket serverSocket;
  private DaemonHttpServlet servlet;

  public Daemon(DaemonHttpServlet servlet) {
    this.servlet = servlet;
  }

  public void run() {
    try {
      // Create a server socket to accept connections
      serverSocket = new ServerSocket(servlet.getSocketPort());
    }
    catch (Exception e) {
      servlet.log("Problem establishing server socket: " +
                  e.getClass().getName() + ": " + e.getMessage());
      return;
    }

    try {
      while (true) {
        // As each connection comes in, call the servlet's handleClient().
        // Note this method is blocking.  It's the servlet's responsibility
        // to spawn a handler thread for long-running connections.
        try {
          servlet.handleClient(serverSocket.accept());
        }
        catch (IOException ioe) {
          servlet.log("Problem accepting client's socket connection: " +
                      ioe.getClass().getName() + ": " + ioe.getMessage());
        }
      }
    }
    catch (ThreadDeath e) {
      // When the thread is killed, close the server socket
      try {
        serverSocket.close();
      }
      catch (IOException ioe) {
        servlet.log("Problem closing server socket: " +
                    ioe.getClass().getName() + ": " + ioe.getMessage());
      }
    }
  }
}
