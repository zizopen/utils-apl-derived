/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.xml;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.w3c.dom.Node;

/**
 * Helper class for JAXB annotated classes.
 * 
 * @see XMLHelper
 * @author Omnaest
 */
public class JAXBXMLHelper
{
  /* ********************************************** Constants ********************************************** */
  final static public String ENCODING_UTF_8 = "utf-8";
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Stores a given JAXB annotated object to the given {@link OutputStream} using the {@link #ENCODING_UTF_8}
   * 
   * @see #storeObjectAsXML(Object, OutputStream, String)
   * @param object
   * @param outputStream
   */
  public static void storeObjectAsXML( Object object, OutputStream outputStream )
  {
    String encoding = ENCODING_UTF_8;
    JAXBXMLHelper.storeObjectAsXML( object, outputStream, encoding );
  }
  
  /**
   * @see #storeObjectAsXML(Object)
   * @param object
   * @param outputStream
   * @param exceptionHandler
   */
  public static void storeObjectAsXML( Object object, OutputStream outputStream, ExceptionHandler exceptionHandler )
  {
    String encoding = ENCODING_UTF_8;
    JAXBXMLHelper.storeObjectAsXML( object, outputStream, encoding, exceptionHandler );
  }
  
  /**
   * Stores a given JAXB annotated object to the given {@link OutputStream} using the given character encoding
   * 
   * @see #storeObjectAsXML(Object, OutputStream)
   * @param object
   * @param outputStream
   * @param encoding
   */
  public static void storeObjectAsXML( Object object, OutputStream outputStream, String encoding )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    storeObjectAsXML( object, outputStream, encoding, exceptionHandler );
  }
  
  /**
   * Stores a given JAXB annotated object to the given {@link OutputStream} using the given character encoding
   * 
   * @see #storeObjectAsXML(Object, OutputStream)
   * @param object
   * @param outputStream
   * @param encoding
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public static void storeObjectAsXML( Object object,
                                       OutputStream outputStream,
                                       String encoding,
                                       ExceptionHandler exceptionHandler )
  {
    // 
    try
    {
      //
      JAXBContext context = JAXBContext.newInstance( object.getClass() );
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      if ( encoding != null )
      {
        marshaller.setProperty( Marshaller.JAXB_ENCODING, encoding );
      }
      marshaller.marshal( object, outputStream );
      outputStream.flush();
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
  }
  
  /**
   * Stores the given objects as XML within the given {@link Appendable} using the {@link #ENCODING_UTF_8}
   * 
   * @param object
   * @param appendable
   */
  public static void storeObjectAsXML( Object object, Appendable appendable )
  {
    final String encoding = ENCODING_UTF_8;
    JAXBXMLHelper.storeObjectAsXML( object, appendable, encoding );
  }
  
  /**
   * Stores the given object as XML within the given {@link Appendable} using the given encoding. E.g as {@link Appendable} a
   * {@link StringBuilder} or {@link StringBuffer} can be used.
   * 
   * @param object
   * @param appendable
   * @param encoding
   */
  public static void storeObjectAsXML( Object object, Appendable appendable, String encoding )
  {
    //
    if ( object != null && appendable != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      
      //
      JAXBXMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding );
      
      //
      byteArrayContainer.writeTo( appendable, encoding );
    }
  }
  
  /**
   * Stores the given object as XML {@link String} using the {@link #ENCODING_UTF_8}
   * 
   * @see #storeObjectAsXML(Object, String)
   * @param object
   * @return
   */
  public static String storeObjectAsXML( Object object )
  {
    return JAXBXMLHelper.storeObjectAsXML( object, ENCODING_UTF_8 );
  }
  
  /**
   * Stores the given object as XML {@link String} using the {@link #ENCODING_UTF_8}
   * 
   * @see #storeObjectAsXML(Object, String)
   * @param object
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return
   */
  public static String storeObjectAsXML( Object object, ExceptionHandler exceptionHandler )
  {
    return JAXBXMLHelper.storeObjectAsXML( object, ENCODING_UTF_8, exceptionHandler );
  }
  
  /**
   * Stores the given object as XML {@link String} using the given encoding.
   * 
   * @see #storeObjectAsXML(Object, OutputStream)
   * @see #storeObjectAsXML(Object)
   * @param object
   * @param encoding
   * @return
   */
  public static String storeObjectAsXML( Object object, String encoding )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    JAXBXMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding );
    
    //
    return byteArrayContainer.toString( encoding );
  }
  
  /**
   * @see #storeObjectAsXML(Object, String)
   * @param object
   * @param encoding
   * @param exceptionHandler
   * @return
   */
  public static String storeObjectAsXML( Object object, String encoding, ExceptionHandler exceptionHandler )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    JAXBXMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding, exceptionHandler );
    
    //
    return byteArrayContainer.toString( encoding );
  }
  
  /**
   * Loads an object from the given class type from an {@link InputStream}. The class has to have JAXB annotations.
   * 
   * @param <E>
   * @param inputStream
   * @param typeClazz
   * @return
   */
  public static <E> E loadObjectFromXML( InputStream inputStream, Class<E> typeClazz )
  {
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromXML( inputStream, typeClazz, exceptionHandler );
  }
  
  /**
   * Loads an object of the given class type from an {@link InputStream}. The class has to have JAXB annotations.
   * 
   * @param <E>
   * @param inputStream
   * @param type
   * @param exceptionHandler
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E loadObjectFromXML( InputStream inputStream, Class<E> type, ExceptionHandler exceptionHandler )
  {
    //
    E retval = null;
    
    //
    try
    {
      //
      final JAXBContext context = JAXBContext.newInstance( type );
      final Unmarshaller um = context.createUnmarshaller();
      
      //
      retval = (E) um.unmarshal( inputStream );
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    //
    return retval;
  }
  
  /**
   * Similar to {@link #loadObjectFromNode(Node, Class, ExceptionHandler)} but ignoring {@link Exception}s
   * 
   * @param <E>
   * @param node
   *          {@link Node}
   * @param type
   * @return
   */
  public static <E> E loadObjectFromNode( Node node, Class<E> type )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromNode( node, type, exceptionHandler );
  }
  
  /**
   * Loads an object of the given class type from an {@link Node}. The class has to have JAXB annotations.
   * 
   * @param <E>
   * @param node
   *          {@link Node}
   * @param type
   * @param exceptionHandler
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E loadObjectFromNode( Node node, Class<E> type, ExceptionHandler exceptionHandler )
  {
    //
    E retval = null;
    
    //
    try
    {
      //
      final JAXBContext context = JAXBContext.newInstance( type );
      final Unmarshaller um = context.createUnmarshaller();
      
      //
      retval = (E) um.unmarshal( node );
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Loads an {@link Object} from a {@link CharSequence} which contains valid xml text content. The given {@link Class} type
   * specifies the root object type.
   * 
   * @param charSequence
   * @param type
   * @return
   */
  public static <E> E loadObjectFromXML( CharSequence charSequence, Class<E> type )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromXML( charSequence, type, exceptionHandler );
  }
  
  /**
   * Loads an {@link Object} from a {@link CharSequence} which contains valid xml text content. The given {@link Class} type
   * specifies the root object type.
   * 
   * @param charSequence
   * @param type
   * @param exceptionHandler
   * @return
   */
  public static <E> E loadObjectFromXML( CharSequence charSequence, Class<E> type, ExceptionHandler exceptionHandler )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( charSequence );
    
    //
    return JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), type, exceptionHandler );
  }
  
  /**
   * @param xmlContent
   * @param typeClazz
   * @return
   */
  public static <E> E loadObjectFromXML( String xmlContent, Class<E> typeClazz )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromXML( xmlContent, typeClazz, exceptionHandler );
  }
  
  /**
   * @param xmlContent
   * @param typeClazz
   * @param exceptionHandler
   * @return
   */
  public static <E> E loadObjectFromXML( String xmlContent, Class<E> typeClazz, ExceptionHandler exceptionHandler )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( xmlContent );
    
    //
    return JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), typeClazz, exceptionHandler );
  }
  
  /**
   * Clones a given {@link Object} using {@link #storeObjectAsXML(Object)} and {@link #loadObjectFromXML(CharSequence, Class)}
   * 
   * @param object
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E cloneObject( E object )
  {
    //
    E retval = null;
    
    //
    if ( object != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      
      //
      JAXBXMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream() );
      
      //
      retval = (E) JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), object.getClass() );
    }
    
    //
    return retval;
  }
}
