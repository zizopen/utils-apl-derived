package org.omnaest.utils.network;

import java.net.ServerSocket;

public class PortUtils
{
  
  /**
   * Returns the next available random port
   * 
   * @return
   */
  public static int findNextAvailableRandomPort()
  {
    int retval = -1;
    
    while ( retval == -1 )
    {
      ServerSocket serverSocket = null;
      try
      {
        serverSocket = new ServerSocket();
        retval = serverSocket.getLocalPort();
        break;
      }
      catch ( Exception exception )
      {
      }
      finally
      {
        if ( serverSocket != null )
        {
          try
          {
            serverSocket.close();
          }
          catch ( Exception exception2 )
          {
          }
        }
      }
    }
    
    return retval;
  }
  
  /**
   * Returns the next available port within the given range
   * 
   * @param from
   * @param to
   * @return
   */
  public static int findNextAvailablePortInRange( int from, int to )
  {
    int retval = -1;
    
    for ( int port = from; port <= to; port++ )
    {
      ServerSocket serverSocket = null;
      try
      {
        serverSocket = new ServerSocket( port );
        retval = port;
        break;
      }
      catch ( Exception exception )
      {
      }
      finally
      {
        if ( serverSocket != null )
        {
          try
          {
            serverSocket.close();
          }
          catch ( Exception exception2 )
          {
          }
        }
      }
    }
    
    return retval;
  }
}
