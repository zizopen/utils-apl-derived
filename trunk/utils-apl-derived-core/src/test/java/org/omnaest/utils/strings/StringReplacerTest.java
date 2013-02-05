package org.omnaest.utils.strings;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.utils.strings.StringReplacer.ReplacementResult;
import org.omnaest.utils.structure.array.ArrayUtils;

/**
 * @see StringReplacer
 * @author Omnaest
 */
public class StringReplacerTest
{
  
  @Test
  public void testFindAndRemoveAll() throws Exception
  {
    ReplacementResult replacementResult = new StringReplacer( ":([0-9]+)$" ).setGroup( 1 ).findAndRemoveFirst( "123-456:2000" );
    assertEquals( "123-456", replacementResult.getOutput() );
    assertArrayEquals( ArrayUtils.valueOf( "2000" ), replacementResult.getMatchingTokens() );
  }
  
}
