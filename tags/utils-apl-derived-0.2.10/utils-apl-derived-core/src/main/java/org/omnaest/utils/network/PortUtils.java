/*******************************************************************************
 * Copyright 2013 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
