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

import org.omnaest.utils.structure.container.ByteArrayContainer;

/**
 * Helper class for JAXB annotated classes.
 * 
 * @author Omnaest
 */
public class XMLHelper
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
    XMLHelper.storeObjectAsXML( object, outputStream, encoding );
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
      e.printStackTrace();
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
    XMLHelper.storeObjectAsXML( object, appendable, encoding );
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
      XMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding );
      
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
    return XMLHelper.storeObjectAsXML( object, ENCODING_UTF_8 );
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
    XMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding );
    
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
  @SuppressWarnings("unchecked")
  public static <E> E loadObjectFromXML( InputStream inputStream, Class<E> typeClazz )
  {
    //
    E retval = null;
    
    //
    try
    {
      JAXBContext context = JAXBContext.newInstance( typeClazz );
      Unmarshaller um = context.createUnmarshaller();
      retval = (E) um.unmarshal( inputStream );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    //
    return retval;
  }
  
  public static <E> E loadObjectFromXML( CharSequence charSequence, Class<E> typeClazz )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( charSequence );
    
    //
    return XMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), typeClazz );
  }
  
  public static <E> E loadObjectFromXML( String xmlContent, Class<E> typeClazz )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( xmlContent );
    
    //
    return XMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), typeClazz );
  }
  
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
      XMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream() );
      
      //
      retval = (E) XMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), object.getClass() );
    }
    
    //
    return retval;
  }
}
