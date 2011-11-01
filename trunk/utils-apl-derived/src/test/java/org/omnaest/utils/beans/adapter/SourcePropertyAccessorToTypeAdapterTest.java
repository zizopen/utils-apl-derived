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
package org.omnaest.utils.beans.adapter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.annotation.XmlType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;

/**
 * @see SourcePropertyAccessorToTypeAdapter
 * @author Omnaest
 */
public class SourcePropertyAccessorToTypeAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private SourcePropertyAccessor propertyAccessor                = Mockito.mock( SourcePropertyAccessor.class );
  private TestType               testType                        = SourcePropertyAccessorToTypeAdapter.newInstance( TestType.class,
                                                                                                                    this.propertyAccessor );
  private PropertyAccessOption   propertyAccessOption            = PropertyAccessOption.PROPERTY_LOWERCASE;
  private boolean                isRegardingAdapterAnnotation    = false;
  private boolean                isRegardingPropertyNameTemplate = true;
  private TestType2              testType2                       = SourcePropertyAccessorToTypeAdapter.newInstance( TestType2.class,
                                                                                                                    this.propertyAccessor,
                                                                                                                    new SourcePropertyAccessorToTypeAdapter.Configuration(
                                                                                                                                                                           this.propertyAccessOption,
                                                                                                                                                                           this.isRegardingAdapterAnnotation,
                                                                                                                                                                           this.isRegardingPropertyNameTemplate ) );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  @XmlType
  public static interface TestType
  {
    public String getFieldString();
    
    public void setFieldString( String value );
    
    public double getFieldPrimitiveDouble();
    
    @Converter(type = ElementConverterIdentitiyCast.class)
    public void setFieldPrimitiveDouble( double value, String additionalArgument );
  }
  
  public static interface TestType2
  {
    @PropertyNameTemplate("field({0})")
    public String getFieldString( String tag );
    
    public void setFieldString( String value, String tag );
    
    public double getFieldPrimitiveDouble();
    
    public void setFieldPrimitiveDouble( double value );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    {
      //
      Mockito.when( this.propertyAccessor.getValue( Matchers.eq( "fieldString" ), Matchers.eq( String.class ),
                                                    (PropertyMetaInformation) Matchers.anyObject() ) )
             .thenReturn( "return string" );
      
      Mockito.when( this.propertyAccessor.getValue( Matchers.eq( "fieldPrimitiveDouble" ), Matchers.eq( double.class ),
                                                    (PropertyMetaInformation) Matchers.anyObject() ) ).thenReturn( 1234.2234 );
      
      Mockito.doNothing()
             .when( this.propertyAccessor )
             .setValue( Matchers.eq( "fieldPrimitiveDouble" ), Matchers.eq( 1234.223 ),
                        (PropertyMetaInformation) Matchers.anyObject() );
    }
    //
    {
      //    
      Mockito.when( this.propertyAccessor.getValue( Matchers.eq( "field(LaLa)" ), Matchers.eq( String.class ),
                                                    (PropertyMetaInformation) Matchers.anyObject() ) )
             .thenReturn( "return string2" );
      
      Mockito.when( this.propertyAccessor.getValue( Matchers.eq( "fieldprimitivedouble" ), Matchers.eq( double.class ),
                                                    (PropertyMetaInformation) Matchers.anyObject() ) ).thenReturn( 1234.2232 );
      
      Mockito.doNothing()
             .when( this.propertyAccessor )
             .setValue( Matchers.eq( "fieldprimitivedouble" ), Matchers.eq( 1234.223 ),
                        (PropertyMetaInformation) Matchers.anyObject() );
    }
  }
  
  @Test
  public void testNewInstance()
  {
    //
    assertEquals( "return string", this.testType.getFieldString() );
    assertEquals( 1234.2234, this.testType.getFieldPrimitiveDouble(), 0.0001 );
    
    //
    this.testType.setFieldString( "new string value" );
    this.testType.setFieldPrimitiveDouble( 1234.223, "more" );
    
    //
    ArgumentCaptor<PropertyMetaInformation> argumentCaptureForPropertyMetaInformation = ArgumentCaptor.forClass( PropertyMetaInformation.class );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldString" ), Matchers.eq( String.class ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldString" ),
                                                                      Matchers.eq( "new string value" ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldPrimitiveDouble" ),
                                                                      Matchers.eq( double.class ),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldPrimitiveDouble" ),
                                                                      Matchers.eq( 1234.223 ),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    
    //
    PropertyMetaInformation propertyMetaInformation = argumentCaptureForPropertyMetaInformation.getValue();
    assertArrayEquals( new String[] { "more" }, propertyMetaInformation.getAdditionalArguments() );
    
    assertFalse( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().isEmpty() );
    assertFalse( propertyMetaInformation.getClassAnnotationAutowiredContainer().isEmpty() );
    
    assertTrue( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().containsAssignable( Converter.class ) );
    assertTrue( propertyMetaInformation.getClassAnnotationAutowiredContainer().containsAssignable( XmlType.class ) );
  }
  
  @Test
  public void testNewInstanceLowercaseAndPropertyNameTemplate()
  {
    //
    assertEquals( "return string2", this.testType2.getFieldString( "LaLa" ) );
    assertEquals( 1234.2232, this.testType2.getFieldPrimitiveDouble(), 0.0001 );
    
    //
    this.testType2.setFieldString( "new string value", "LuLu" );
    this.testType2.setFieldPrimitiveDouble( 1234.223 );
    
    //
    ArgumentCaptor<PropertyMetaInformation> argumentCaptureForPropertyMetaInformation = ArgumentCaptor.forClass( PropertyMetaInformation.class );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "field(LaLa)" ), Matchers.eq( String.class ),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "field(LuLu)" ),
                                                                      Matchers.eq( "new string value" ),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldprimitivedouble" ),
                                                                      Matchers.eq( double.class ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldprimitivedouble" ),
                                                                      Matchers.eq( 1234.223 ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    
    //
    PropertyMetaInformation propertyMetaInformation = argumentCaptureForPropertyMetaInformation.getValue();
    assertArrayEquals( new String[] { "LuLu" }, propertyMetaInformation.getAdditionalArguments() );
    
    assertFalse( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().isEmpty() );
    assertTrue( propertyMetaInformation.getClassAnnotationAutowiredContainer().isEmpty() );
    
    assertTrue( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().containsAssignable( PropertyNameTemplate.class ) );
    
  }
}
