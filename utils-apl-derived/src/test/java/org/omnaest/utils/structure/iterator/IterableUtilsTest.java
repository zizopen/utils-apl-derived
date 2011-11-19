package org.omnaest.utils.structure.iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Test;

/**
 * @see IterableUtils
 * @author Omnaest
 */
public class IterableUtilsTest
{
  
  @Test
  public void testEqualsIterableOfQIterableOfQ()
  {
    assertTrue( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "a", "b", "c" ) ) );
    assertTrue( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), new LinkedHashSet<String>( Arrays.asList( "a", "b", "c" ) ) ) );
    assertTrue( IterableUtils.equals( null, null ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "b", "c" ), Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b" ), Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "a", "b" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "b", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "a", "d", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "d", "c" ), Arrays.asList( "a", "b", "c" ) ) );
  }
  
  @Test
  public void testHashCode()
  {
    assertEquals( IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ),
                  IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.hashCode( Arrays.asList( "a", "b", "c", "d" ) ) == IterableUtils.hashCode( Arrays.asList( "a",
                                                                                                                         "b", "c" ) ) );
    assertFalse( IterableUtils.hashCode( Arrays.asList( "a", "c", "c" ) ) == IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.hashCode( Arrays.asList( "a", "c", "b" ) ) == IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ) );
  }
  
}
