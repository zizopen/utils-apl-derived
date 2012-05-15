package org.omnaest.utils.structure.array;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @see ArrayUtils
 * @author Omnaest
 */
public class ArrayUtilsTest
{
  
  @Test
  public void testValueOfEArray()
  {
    //
    final String[] strings = ArrayUtils.valueOf( "a", "b", "c" );
    assertArrayEquals( new String[] { "a", "b", "c" }, strings );
    
    //
    @SuppressWarnings("unchecked")
    final Number[] numbers = ArrayUtils.valueOf( Integer.valueOf( 10 ), Long.valueOf( 3 ), Double.valueOf( 3.5 ) );
    assertEquals( Number.class, ArrayUtils.componentType( numbers.getClass() ) );
  }
  
}
