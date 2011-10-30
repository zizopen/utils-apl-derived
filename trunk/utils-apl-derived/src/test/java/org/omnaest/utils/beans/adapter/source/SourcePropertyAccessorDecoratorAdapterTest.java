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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.omnaest.utils.beans.adapter.SourcePropertyAccessorToTypeAdapter;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;
import org.omnaest.utils.structure.element.converter.Adapter;
import org.omnaest.utils.structure.element.converter.ElementConverterIntegerToString;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToInteger;

/**
 * @see SourcePropertyAccessorDecoratorAdapter
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  
  private SourcePropertyAccessor                  underlyingSourcePropertyAccessor      = Mockito.mock( SourcePropertyAccessor.class );
  private SourcePropertyAccessor                  sourcePropertyAccessor                = new SourcePropertyAccessorDecoratorAdapter(
                                                                                                                                      this.underlyingSourcePropertyAccessor );
  
  private TestTypeWithAdapter                     testTypeWithAdapter                   = SourcePropertyAccessorToTypeAdapter.newInstance( TestTypeWithAdapter.class,
                                                                                                                                           this.sourcePropertyAccessor );
  private ArgumentCaptor<String>                  argumentCaptorPropertyName            = null;
  @SuppressWarnings("rawtypes")
  private ArgumentCaptor<Class>                   argumentCaptorReturnType              = null;
  private ArgumentCaptor<PropertyMetaInformation> argumentCaptorPropertyMetaInformation = null;
  private ArgumentCaptor<Object>                  argumentCaptorPropertyValue           = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static interface TestTypeWithAdapter
  {
    @Adapter(type = ElementConverterIntegerToString.class)
    public String getFieldString();
    
    @Adapter(type = ElementConverterStringToInteger.class)
    public void setFieldString( String fieldString );
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
           .thenReturn( Integer.valueOf( 12 ) );
    
    //
    this.argumentCaptorPropertyValue = ArgumentCaptor.forClass( Object.class );
    Mockito.doNothing()
           .when( this.underlyingSourcePropertyAccessor )
           .setValue( this.argumentCaptorPropertyName.capture(), this.argumentCaptorPropertyValue.capture(),
                      this.argumentCaptorPropertyMetaInformation.capture() );
  }
  
  @Test
  public void testSetValue()
  {
    //
    this.testTypeWithAdapter.setFieldString( "1" );
    assertTrue( this.argumentCaptorPropertyValue.getValue() instanceof Integer );
  }
  
  @Test
  public void testGetValue()
  {
    //
    String fieldString = this.testTypeWithAdapter.getFieldString();
    assertEquals( "12", fieldString );
    assertEquals( String.class, this.argumentCaptorReturnType.getValue() );
  }
  
}
