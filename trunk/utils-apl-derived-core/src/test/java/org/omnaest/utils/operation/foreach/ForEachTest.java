package org.omnaest.utils.operation.foreach;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see ForEach
 * @author Omnaest
 */
public class ForEachTest
{
  
  @Test
  public void testOperation() throws ExecutionException
  {
    assertEquals( ListUtils.valueOf( "a", "b", "c" ),
                  ListUtils.valueOf( new ForEach<String>( "a", "b", "c" ).map( new Operation<String, String>()
                  {
                    @Override
                    public String execute( String parameter )
                    {
                      return parameter;
                    }
                  } ) ) );
    assertEquals( "a b c ", new ForEach<String>( "a", "b", "c" ).map( new Operation<String, String>()
    {
      @Override
      public String execute( String parameter )
      {
        return parameter + " ";
      }
    } ).reduce( new Operation<String, Collection<String>>()
    {
      @Override
      public String execute( Collection<String> parameter )
      {
        return StringUtils.join( parameter, "" );
      }
    } ) );
  }
  
  @Test
  public void testExceptionHandling() throws ExecutionException
  {
    try
    {
      Operation<Object, String> operation = new Operation<Object, String>()
      {
        @Override
        public Object execute( String parameter )
        {
          throw new RuntimeException();
        }
      };
      new ForEach<String>( "a", "b", "c" ).map( operation );
      
      fail();
    }
    catch ( ExecutionException e )
    {
    }
  }
}
