package org.omnaest.utils.structure.element.factory;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see FactoryParameterized
 * @author Omnaest
 */
public class FactoryParameterizedTest
{
  /* ********************************************** Variables ********************************************** */
  private TestFactory<String> testFactory = new TestFactory<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  protected static class TestFactory<P> extends FactoryParameterized<Boolean, P>
  {
    @Override
    public Boolean newInstance( P... arguments )
    {
      return true;
    }
  }
  
  @Test
  public void testNewInstance()
  {
    assertTrue( this.testFactory.newInstance() );
  }
  
}
