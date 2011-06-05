package org.omnaest.utils.beans;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.MapToInterfaceAdapter;

public class MapToInterfaceAdapterTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  protected static interface TestType
  {
    public Double getFieldDouble();
    
    public void setFieldDouble( Double fieldDouble );
    
    public String getFieldString();
    
    public void setFieldString( String fieldString );
  }
  
  @Test
  public void testNewInstance()
  {
    //
    Map<String, Object> map = new HashMap<String, Object>();
    
    //reading from facade
    TestType testType = MapToInterfaceAdapter.newInstance( map, TestType.class );
    
    //
    map.put( "fieldString", "String value" );
    map.put( "fieldDouble", 10.0 );
    
    assertEquals( "String value", testType.getFieldString() );
    assertEquals( 10.0, testType.getFieldDouble(), 0.01 );
    
    //writing to facade
    testType.setFieldString( "New String value" );
    testType.setFieldDouble( 11.0 );
    
    assertEquals( "New String value", map.get( "fieldString" ) );
    assertEquals( 11.0, (Double) map.get( "fieldDouble" ), 0.01 );
    assertEquals( 2, map.size() );
  }
}
