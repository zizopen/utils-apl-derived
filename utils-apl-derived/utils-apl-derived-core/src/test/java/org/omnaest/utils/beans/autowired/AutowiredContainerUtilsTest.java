package org.omnaest.utils.beans.autowired;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * @see AutowiredContainerUtils
 * @author Omnaest
 */
public class AutowiredContainerUtilsTest
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  final AutowiredContainer<TestEntityInterface> autowiredContainer = AutowiredContainerUtils.newInstance();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  protected static interface TestEntityInterface
  {
    
  }
  
  protected static class TestEntity implements TestEntityInterface
  {
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Before
  public void setUp()
  {
    for ( int ii = 0; ii < 1000; ii++ )
    {
      this.autowiredContainer.put( new TestEntity() );
    }
    for ( int ii = 0; ii < 10000; ii++ )
    {
      assertNotNull( this.autowiredContainer.getValue( TestEntity.class ) );
      assertNotNull( this.autowiredContainer.getValue( TestEntityInterface.class ) );
    }
  }
  
  @Test
  public void testNewInstancePerformanceGetExactType()
  {
    //
    for ( int ii = 0; ii < 100000; ii++ )
    {
      TestEntityInterface value = this.autowiredContainer.getValue( TestEntity.class );
      assertNotNull( value );
    }
  }
  
  @Test
  public void testNewInstancePerformanceGetInterfaceType()
  {
    //
    for ( int ii = 0; ii < 100000; ii++ )
    {
      TestEntityInterface value = this.autowiredContainer.getValue( TestEntityInterface.class );
      assertNotNull( value );
    }
  }
  
}
