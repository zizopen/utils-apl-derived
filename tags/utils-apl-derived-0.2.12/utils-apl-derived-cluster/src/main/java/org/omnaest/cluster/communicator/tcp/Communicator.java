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
package org.omnaest.cluster.communicator.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.streams.InputStreamDecorator;
import org.omnaest.utils.streams.OutputStreamDecorator;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.map.MapUtils;

public abstract class Communicator
{
  protected InputStream      inputStream;
  protected OutputStream     outputStream;
  protected ExceptionHandler exceptionHandler;
  
  protected static class Ack implements Serializable
  {
    private static final long serialVersionUID = 715608256487114671L;
  }
  
  protected static class Ping implements Serializable
  {
    private static final long serialVersionUID = 8444739578580533017L;
  }
  
  private static class Text implements Serializable
  {
    private static final long serialVersionUID = 1789997395429604693L;
    private String            text;
    
    private Text()
    {
      super();
    }
    
    private Text( String text )
    {
      super();
      this.text = text;
    }
    
    String getText()
    {
      return this.text;
    }
  }
  
  /**
   * @see Receiver#forType()
   * @see Receiver#receive(Object)
   * @author Omnaest
   * @param <E>
   */
  public static interface Receiver<E>
  {
    /**
     * Returns the type, the {@link Receiver} should be noticed upon
     * 
     * @return
     */
    public Class<E> forType();
    
    /**
     * Gets the incoming element
     * 
     * @param element
     */
    public void receive( E element );
  }
  
  protected Communicator( InputStream inputStream, OutputStream outputStream, ExceptionHandler exceptionHandler )
  {
    super();
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.exceptionHandler = exceptionHandler;
  }
  
  protected Communicator()
  {
    super();
  }
  
  public void send( Serializable serializable )
  {
    SerializationUtils.serialize( serializable, new OutputStreamDecorator( this.outputStream )
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void close() throws IOException
      {
        super.flush();
      }
    } );
  }
  
  public void sendText( String text )
  {
    this.send( new Text( text ) );
  }
  
  public void sendPing()
  {
    this.send( new Ping() );
  }
  
  public void sendAck()
  {
    this.send( new Ack() );
  }
  
  public boolean receiveAck()
  {
    return this.receive() instanceof Ack;
  }
  
  public boolean receivePing()
  {
    return this.receive() instanceof Ping;
  }
  
  /**
   * Receives an {@link Object}
   * 
   * @return
   */
  public Object receive()
  {
    return SerializationUtils.deserialize( new InputStreamDecorator( this.inputStream )
    {
      private static final long serialVersionUID = 7520075650773405070L;
      
      @Override
      public void close() throws IOException
      {
      }
    } );
  }
  
  public String receiveText()
  {
    return ( (Text) this.receive() ).getText();
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void receive( Receiver<?>... receivers )
  {
    if ( receivers != null )
    {
      final Map<Class<?>, List<Receiver<?>>> typeToReceiverMap = MapUtils.valueOfMultiple( new KeyExtractor<Class<?>, Receiver<?>>()
                                                                                           {
                                                                                             private static final long serialVersionUID = -8359502049083140581L;
                                                                                             
                                                                                             @Override
                                                                                             public Class<?> extractKey( Receiver<?> receiver )
                                                                                             {
                                                                                               return receiver.forType();
                                                                                             }
                                                                                           }, receivers );
      
      Object object = this.receive();
      if ( object != null )
      {
        Class<? extends Object> objectType = object.getClass();
        for ( Class<?> type : typeToReceiverMap.keySet() )
        {
          if ( type != null && type.isAssignableFrom( objectType ) )
          {
            List<Receiver<?>> receiverList = typeToReceiverMap.get( type );
            if ( receiverList != null )
            {
              for ( Receiver receiver : receiverList )
              {
                try
                {
                  receiver.receive( type.cast( object ) );
                }
                catch ( Exception e )
                {
                  this.handleExcpetion( e );
                }
              }
            }
          }
        }
      }
      
    }
  }
  
  protected void close()
  {
    if ( this.inputStream != null )
    {
      try
      {
        this.inputStream.close();
      }
      catch ( IOException e )
      {
        handleExcpetion( e );
      }
    }
    if ( this.outputStream != null )
    {
      try
      {
        this.outputStream.close();
      }
      catch ( IOException e )
      {
        handleExcpetion( e );
      }
    }
  }
  
  protected void handleExcpetion( Exception e )
  {
    if ( this.exceptionHandler != null )
    {
      this.exceptionHandler.handleException( e );
    }
  }
}
