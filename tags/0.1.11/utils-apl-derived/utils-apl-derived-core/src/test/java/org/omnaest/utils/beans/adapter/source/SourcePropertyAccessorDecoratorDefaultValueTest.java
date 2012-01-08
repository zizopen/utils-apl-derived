/*******************************************************************************
 * Copyright 2011 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.beans.adapter.source;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.omnaest.utils.beans.adapter.SourcePropertyAccessorToTypeAdapter;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.map.MapBuilder;

/**
 * @see SourcePropertyAccessorDecoratorDefaultValue
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorDefaultValueTest
{
  /* ********************************************** Variables ********************************************** */
  private SourcePropertyAccessor                  underlyingSourcePropertyAccessor      = Mockito.mock( SourcePropertyAccessor.class );
  private SourcePropertyAccessor                  sourcePropertyAccessor                = new SourcePropertyAccessorDecoratorDefaultValue(
                                                                                                                                           this.underlyingSourcePropertyAccessor );
  private ExampleInterface                        testInterface                         = SourcePropertyAccessorToTypeAdapter.newInstance( ExampleInterface.class,
                                                                                                                                           this.sourcePropertyAccessor );
  private ArgumentCaptor<String>                  argumentCaptorPropertyName            = null;
  @SuppressWarnings("rawtypes")
  private ArgumentCaptor<Class>                   argumentCaptorReturnType              = null;
  private ArgumentCaptor<PropertyMetaInformation> argumentCaptorPropertyMetaInformation = null;
  private ArgumentCaptor<Object>                  argumentCaptorPropertyValue           = null;
  private ElementHolder<Object>                   elementHolderReturnValue              = new ElementHolder<Object>( null );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  protected interface ExampleInterface
  {
    @DefaultValue("defaultValueSet")
    public void setFieldString( String value );
    
    @DefaultValue("defaultValueGet")
    public String getFieldString();
    
    @DefaultValue("1.23")
    public void setFieldDouble( Double value );
    
    @DefaultValue("3.45")
    public Double getFieldDouble();
    
    @DefaultValues(values = { "a", "b" })
    public List<String> getListString();
    
    @DefaultValues(values = { "e", "f" })
    public void setListString( List<String> listString );
    
    @DefaultValues(values = { "1.23", "2.34" })
    public double[] getArrayDouble();
    
    @DefaultValues(values = { "key1", "1.23", "key2", "2.34" })
    public Map<String, Double> getMapStringAndDouble();
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.argumentCaptorPropertyName = ArgumentCaptor.forClass( String.class );
    this.argumentCaptorReturnType = ArgumentCaptor.forClass( Class.class );
    this.argumentCaptorPropertyMetaInformation = ArgumentCaptor.forClass( PropertyMetaInformation.class );
    
    //
    Mockito.when( this.underlyingSourcePropertyAccessor.getValue( this.argumentCaptorPropertyName.capture(),
                                                                  this.argumentCaptorReturnType.capture(),
                                                                  this.argumentCaptorPropertyMetaInformation.capture() ) )
           .thenAnswer( new Answer<Object>()
           {
             
             @Override
             public Object answer( InvocationOnMock invocation ) throws Throwable
             {
               return SourcePropertyAccessorDecoratorDefaultValueTest.this.elementHolderReturnValue.getElement();
             }
           } );
    
    //
    this.argumentCaptorPropertyValue = ArgumentCaptor.forClass( Object.class );
    Mockito.doNothing()
           .when( this.underlyingSourcePropertyAccessor )
           .setValue( this.argumentCaptorPropertyName.capture(), this.argumentCaptorPropertyValue.capture(),
                      (Class<?>) Matchers.anyObject(), this.argumentCaptorPropertyMetaInformation.capture() );
  }
  
  @Test
  public void testSetValue()
  {
    //
    {
      //
      String value = null;
      this.testInterface.setFieldString( value );
      
      //
      assertEquals( "defaultValueSet", this.argumentCaptorPropertyValue.getValue() );
    }
    {
      //
      String value = "lala";
      this.testInterface.setFieldString( value );
      
      //
      assertEquals( "lala", this.argumentCaptorPropertyValue.getValue() );
    }
    
    //
    {
      //
      Double value = null;
      this.testInterface.setFieldDouble( value );
      
      //
      assertEquals( 1.23, this.argumentCaptorPropertyValue.getValue() );
    }
    {
      //
      Double value = 3.45;
      this.testInterface.setFieldDouble( value );
      
      //
      assertEquals( 3.45, this.argumentCaptorPropertyValue.getValue() );
    }
    
    //
    {
      //
      List<String> value = null;
      this.testInterface.setListString( value );
      
      //
      assertEquals( Arrays.asList( "e", "f" ), this.argumentCaptorPropertyValue.getValue() );
    }
    {
      //
      List<String> value = Arrays.asList( "c", "d" );
      this.testInterface.setListString( value );
      
      //
      assertEquals( value, this.argumentCaptorPropertyValue.getValue() );
    }
  }
  
  @Test
  public void testGetValue()
  {
    //
    {
      //
      this.elementHolderReturnValue.setElement( null );
      assertEquals( "defaultValueGet", this.testInterface.getFieldString() );
      
      //
      this.elementHolderReturnValue.setElement( "lala" );
      assertEquals( "lala", this.testInterface.getFieldString() );
    }
    
    //
    {
      //
      this.elementHolderReturnValue.setElement( null );
      assertEquals( 3.45, this.testInterface.getFieldDouble().doubleValue(), 0.01 );
      
      //
      this.elementHolderReturnValue.setElement( 1.34 );
      assertEquals( 1.34, this.testInterface.getFieldDouble().doubleValue(), 0.01 );
    }
    
    //
    {
      //
      this.elementHolderReturnValue.setElement( null );
      assertEquals( Arrays.asList( "a", "b" ), this.testInterface.getListString() );
      
      //
      this.elementHolderReturnValue.setElement( Arrays.asList( "c", "d" ) );
      assertEquals( Arrays.asList( "c", "d" ), this.testInterface.getListString() );
    }
    
    //
    {
      //
      this.elementHolderReturnValue.setElement( null );
      assertArrayEquals( new double[] { 1.23, 2.34 }, this.testInterface.getArrayDouble(), 0.01 );
    }
    
    //
    {
      //
      this.elementHolderReturnValue.setElement( null );
      assertEquals( new MapBuilder<String, Double>().linkedHashMap().put( "key1", 1.23 ).put( "key2", 2.34 ).build(),
                    this.testInterface.getMapStringAndDouble() );
    }
  }
}
