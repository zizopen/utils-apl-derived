package org.omnaest.utils.beans.autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * @see AutowiredContainerDecorator
 * @author Omnaest
 */
public class AutowiredContainerDecoratorTest
{
  /* ********************************************** Variables ********************************************** */
  protected AutowiredContainer<Object> autowiredContainer          = ClassMapToAutowiredContainerAdapter.newInstanceUsingLinkedHashMap();
  protected AutowiredContainer<Object> autowiredContainerDecorator = new AutowiredContainerDecorator<Object>(
                                                                                                              this.autowiredContainer );
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.autowiredContainer.put( "text" );
    this.autowiredContainer.put( 1.45f );
  }
  
  @Test
  public void testJAXBCompliance()
  {
    //
    String objectAsXML = JAXBXMLHelper.storeObjectAsXML( this.autowiredContainerDecorator );
    assertNotNull( objectAsXML );
    
    //System.out.println( objectAsXML );
    @SuppressWarnings("unchecked")
    AutowiredContainerDecorator<Object> objectFromXML = JAXBXMLHelper.loadObjectFromXML( objectAsXML,
                                                                                         AutowiredContainerDecorator.class );
    assertNotNull( objectAsXML );
    assertEquals( this.autowiredContainerDecorator, objectFromXML );
    assertEquals( objectFromXML, this.autowiredContainerDecorator );
  }
  
}
