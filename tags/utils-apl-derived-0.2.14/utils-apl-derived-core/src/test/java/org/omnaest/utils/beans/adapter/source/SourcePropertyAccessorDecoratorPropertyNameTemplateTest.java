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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.omnaest.utils.beans.adapter.SourcePropertyAccessorToTypeAdapter;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;

/**
 * @see SourcePropertyAccessorDecoratorPropertyNameTemplate
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorPropertyNameTemplateTest
{
  /* ********************************************** Variables ********************************************** */
  private SourcePropertyAccessor                  underlyingSourcePropertyAccessor      = Mockito.mock( SourcePropertyAccessor.class );
  private SourcePropertyAccessor                  sourcePropertyAccessor                = new SourcePropertyAccessorDecoratorPropertyNameTemplate(
                                                                                                                                                   this.underlyingSourcePropertyAccessor );
  private ExampleInterface                        testInterface                         = SourcePropertyAccessorToTypeAdapter.newInstance( ExampleInterface.class,
                                                                                                                                           this.sourcePropertyAccessor );
  private ArgumentCaptor<String>                  argumentCaptorPropertyName            = null;
  @SuppressWarnings("rawtypes")
  private ArgumentCaptor<Class>                   argumentCaptorReturnType              = null;
  private ArgumentCaptor<PropertyMetaInformation> argumentCaptorPropertyMetaInformation = null;
  private ArgumentCaptor<Object>                  argumentCaptorPropertyValue           = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @PropertyNameTemplate("{propertyname}_class")
  protected interface ExampleInterface
  {
    
    //This will map to "field_class" because of the class type annotation
    public void setField( String value );
    
    //This will map to "field_class" because of the class type annotation
    public String getField();
    
    //This will map to "fieldWithTemplateAndMore" because of the property annotation
    @PropertyNameTemplate("{propertyname}AndMore")
    public void setFieldWithTemplate( String value );
    
    //This will map to "fieldWithTemplateAndMore" because of the property annotation of the corresponding setter
    public String getFieldWithTemplate();
    
    //This will map to "fieldWithTemplateAndAdditionalArgumentsAndMore_abc_" for the given tagValue "abc". 
    //The name template of the corresponding getter is ignored. The "{0}" token is replaced by the given tag value.
    @PropertyNameTemplate("{propertyname}AndMore_{0}_")
    public void setFieldWithTemplateAndAdditionalArguments( String value, String tagValue );
    
    //This will map to "fieldWithTemplateAndAdditionalArgumentsAndMore(abc)" for the given tagValue "abc". 
    //The name template of the corresponding setter is ignored. The "{0}" token is replaced by the given tag value.
    @PropertyNameTemplate("{propertyname}AndMore({0})")
    public String getFieldWithTemplateAndAdditionalArguments( String tagValue );
    
    @PropertyNameTemplate(value = "test1", alternativeValues = { "test2", "test3" })
    public String getFieldWithAlternativeTemplate();
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
           .thenReturn( "value1" );
    
    //
    this.argumentCaptorPropertyValue = ArgumentCaptor.forClass( Object.class );
    Mockito.doNothing()
           .when( this.underlyingSourcePropertyAccessor )
           .setValue( this.argumentCaptorPropertyName.capture(), this.argumentCaptorPropertyValue.capture(),
                      (Class<?>) Matchers.anyObject(), this.argumentCaptorPropertyMetaInformation.capture() );
  }
  
  @Test
  public void testGetValue()
  {
    //
    {
      //
      String fieldWithTemplate = this.testInterface.getField();
      assertEquals( "value1", fieldWithTemplate );
      assertEquals( "field_class", this.argumentCaptorPropertyName.getValue() );
    }
    {
      //      
      String fieldWithTemplate = this.testInterface.getFieldWithTemplate();
      assertEquals( "value1", fieldWithTemplate );
      assertEquals( "fieldWithTemplateAndMore", this.argumentCaptorPropertyName.getValue() );
    }
    {
      //
      String fieldWithTemplate = this.testInterface.getFieldWithTemplateAndAdditionalArguments( "abc" );
      assertEquals( "value1", fieldWithTemplate );
      assertEquals( "fieldWithTemplateAndAdditionalArgumentsAndMore(abc)", this.argumentCaptorPropertyName.getValue() );
    }
    {
      //
      String fieldWithTemplate = this.testInterface.getFieldWithAlternativeTemplate();
      assertEquals( "value1", fieldWithTemplate );
      assertEquals( "test1", this.argumentCaptorPropertyName.getValue() );
    }
    
  }
  
  @Test
  public void testSetValue()
  {
    //
    {
      //
      this.testInterface.setField( "lala" );
      assertEquals( "field_class", this.argumentCaptorPropertyName.getValue() );
      assertEquals( "lala", this.argumentCaptorPropertyValue.getValue() );
    }
    {
      //
      this.testInterface.setFieldWithTemplate( "lala" );
      assertEquals( "fieldWithTemplateAndMore", this.argumentCaptorPropertyName.getValue() );
      assertEquals( "lala", this.argumentCaptorPropertyValue.getValue() );
    }
    {
      //
      this.testInterface.setFieldWithTemplateAndAdditionalArguments( "lala", "abc" );
      assertEquals( "fieldWithTemplateAndAdditionalArgumentsAndMore_abc_", this.argumentCaptorPropertyName.getValue() );
      assertEquals( "lala", this.argumentCaptorPropertyValue.getValue() );
    }
  }
}
