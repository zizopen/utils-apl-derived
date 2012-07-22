/*******************************************************************************
 * Copyright 2012 Danny Kunz
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * {@link XMLHelper} for {@link XPath} or {@link Document} based helper methods.
 * 
 * @see JAXBXMLHelper
 * @see XMLIteratorFactory
 * @see XMLNestedMapConverter
 * @author Omnaest
 */
public class XMLHelper
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Configuration for an {@link TransformerFactory}
   * 
   * @see XMLHelper#transform(StreamSource, StreamSource, StreamResult, ExceptionHandler, XSLTransformerConfiguration)
   * @author Omnaest
   */
  public static class XSLTransformerConfiguration
  {
    private final Map<String, Object> attributeMap      = new HashMap<String, Object>();
    private final Map<String, String> outputPropertyMap = new HashMap<String, String>();
    private final Map<String, Object> parameterMap      = new HashMap<String, Object>();
    
    /**
     * @see TransformerFactory#setAttribute(String, Object)
     * @param key
     * @param value
     * @return this
     */
    public XSLTransformerConfiguration addAttribute( String key, Object value )
    {
      this.attributeMap.put( key, value );
      return this;
    }
    
    /**
     * @see Transformer#setParameter(String, Object)
     * @param key
     * @param value
     * @return this
     */
    public XSLTransformerConfiguration addParameter( String key, Object value )
    {
      this.parameterMap.put( key, value );
      return this;
    }
    
    /**
     * @see Transformer#setOutputProperty(String, String)
     * @param key
     * @param value
     * @return this
     */
    public XSLTransformerConfiguration addOutputProperty( String key, String value )
    {
      this.outputPropertyMap.put( key, value );
      return this;
    }
    
    protected Map<String, Object> getAttributeMap()
    {
      return this.attributeMap;
    }
    
    protected Map<String, String> getOutputPropertyMap()
    {
      return this.outputPropertyMap;
    }
    
    protected Map<String, Object> getParameterMap()
    {
      return this.parameterMap;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Similar to {@link #select(String, Node, ExceptionHandler)}
   * 
   * @param xPathExpression
   * @param node
   * @return
   */
  public static Node select( String xPathExpression, Node node )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return select( xPathExpression, node, exceptionHandler );
  }
  
  /**
   * Allows to select sub {@link Node}s by a given {@link XPath} expression
   * 
   * @param xPathExpression
   * @param node
   *          {@link Node}
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return
   */
  public static Node select( String xPathExpression, Node node, ExceptionHandler exceptionHandler )
  {
    //
    Node retval = null;
    
    //
    final XPath xPath = XPathFactory.newInstance().newXPath();
    try
    {
      retval = (Node) xPath.evaluate( xPathExpression, node, XPathConstants.NODE );
    }
    catch ( XPathExpressionException e )
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
   * Facade for {@link XMLNestedMapConverter#newMapFromXML(CharSequence)}
   * 
   * @param xmlContent
   * @return new nested {@link Map}
   */
  public static Map<String, Object> newMapFromXML( CharSequence xmlContent )
  {
    return new XMLNestedMapConverter().newMapFromXML( xmlContent );
  }
  
  /**
   * Uses the default {@link TransformerFactory} to transform the given xml {@link StreamSource} using the given xslt
   * {@link StreamSource} into the {@link StreamResult}
   * 
   * @param xslt
   *          {@link StreamSource}
   * @param xml
   *          {@link StreamSource}
   * @param result
   *          {@link StreamResult}
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @param xslTransformerConfiguration
   *          {@link XSLTransformerConfiguration}
   */
  public static void transform( StreamSource xslt,
                                StreamSource xml,
                                StreamResult result,
                                ExceptionHandler exceptionHandler,
                                XSLTransformerConfiguration xslTransformerConfiguration )
  {
    try
    {
      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      if ( xslTransformerConfiguration != null )
      {
        final Map<String, Object> attributeMap = xslTransformerConfiguration.getAttributeMap();
        if ( attributeMap != null )
        {
          for ( String name : attributeMap.keySet() )
          {
            final Object value = attributeMap.get( name );
            transformerFactory.setAttribute( name, value );
          }
        }
      }
      
      final Transformer transformer = transformerFactory.newTransformer( xslt );
      if ( xslTransformerConfiguration != null )
      {
        final Map<String, String> outputPropertyMap = xslTransformerConfiguration.getOutputPropertyMap();
        if ( outputPropertyMap != null )
        {
          for ( String name : outputPropertyMap.keySet() )
          {
            final String value = outputPropertyMap.get( name );
            transformer.setOutputProperty( name, value );
          }
        }
        
        final Map<String, Object> parameterMap = xslTransformerConfiguration.getParameterMap();
        if ( parameterMap != null )
        {
          for ( String name : parameterMap.keySet() )
          {
            final Object value = parameterMap.get( name );
            transformer.setParameter( name, value );
          }
        }
      }
      
      transformer.transform( xml, result );
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
  }
}
