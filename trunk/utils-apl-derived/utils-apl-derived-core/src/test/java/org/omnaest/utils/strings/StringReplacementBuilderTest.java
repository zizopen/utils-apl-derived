package org.omnaest.utils.strings;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @see StringReplacementBuilder
 * @author Omnaest
 */
public class StringReplacementBuilderTest
{
  
  @Test
  public void testProcess()
  {
    final String value = "A value and its replacement";
    assertEquals( "A replacement and its replacement",
                  new StringReplacementBuilder().add( "value", "replacement" ).process( value ) );
  }
  
}
