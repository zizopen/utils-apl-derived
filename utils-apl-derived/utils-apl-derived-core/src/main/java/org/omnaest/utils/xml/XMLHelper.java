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

import java.util.Map;

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
 * @author Omnaest
 */
public class XMLHelper
{
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
  
}
