package org.omnaest.utils.structure.hierarchy.nodemap;

import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;

public class NodeMapXMLHandler
{
  private ExceptionHandler exceptionHandler;
  private String           encoding = "utf-8";
  
  /**
   * @param rootTagName
   * @param nodeMap
   * @return
   */
  public String convertToXML( String rootTagName, NodeMap<String, Map<String, String>> nodeMap )
  {
    String retval = null;
    
    if ( nodeMap != null )
    {
      try
      {
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        
        final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
        final Writer writer = byteArrayContainer.getOutputStreamWriter( this.encoding );
        XMLStreamWriter xmlStreamWriter = null;
        try
        {
          xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter( writer );
          {
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeStartElement( rootTagName );
            {
              this.writeNodeMap( nodeMap, xmlStreamWriter );
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
          }
        }
        finally
        {
          try
          {
            if ( xmlStreamWriter != null )
            {
              xmlStreamWriter.close();
            }
          }
          finally
          {
            writer.close();
          }
        }
        retval = byteArrayContainer.toString( this.encoding );
      }
      catch ( Exception e )
      {
        if ( this.exceptionHandler != null )
        {
          this.exceptionHandler.handleException( e );
        }
      }
    }
    
    return retval;
  }
  
  private void writeNodeMap( NodeMap<String, Map<String, String>> nodeMap, XMLStreamWriter xmlStreamWriter ) throws XMLStreamException
  {
    if ( nodeMap != null )
    {
      final Map<String, String> model = nodeMap.getModel();
      if ( model != null )
      {
        for ( String field : model.keySet() )
        {
          final String value = model.get( field );
          writeFieldAndValue( xmlStreamWriter, field, value );
        }
      }
      
      final Set<String> keySet = nodeMap.keySet();
      if ( keySet != null )
      {
        for ( String nodeName : keySet )
        {
          NodeMap<String, Map<String, String>> subNodeMap = nodeMap.get( nodeName );
          if ( subNodeMap != null )
          {
            xmlStreamWriter.writeStartElement( nodeName );
            {
              this.writeNodeMap( subNodeMap, xmlStreamWriter );
            }
            xmlStreamWriter.writeEndElement();
          }
        }
      }
    }
  }
  
  private static void writeFieldAndValue( XMLStreamWriter xmlStreamWriter, String field, String value ) throws XMLStreamException
  {
    String localName = field;
    xmlStreamWriter.writeStartElement( localName );
    {
      xmlStreamWriter.writeCharacters( value );
    }
    xmlStreamWriter.writeEndElement();
  }
}
