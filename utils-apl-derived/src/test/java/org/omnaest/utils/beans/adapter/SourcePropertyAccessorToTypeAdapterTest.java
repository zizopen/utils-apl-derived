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
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;
import org.omnaest.utils.structure.element.converter.Adapter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;

/**
 * @see SourcePropertyAccessorToTypeAdapter
 * @author Omnaest
 */
public class SourcePropertyAccessorToTypeAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private SourcePropertyAccessor                  propertyAccessor = Mockito.mock( SourcePropertyAccessor.class );
  private TestType                                testType         = SourcePropertyAccessorToTypeAdapter.newInstance( TestType.class,
                                                                                                                      this.propertyAccessor );
  private ArgumentCaptor<PropertyMetaInformation> argumentCaptureForPropertyMetaInformation;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  @XmlType
  public static interface TestType
  {
    public String getFieldString();
    
    public void setFieldString( String value );
    
    public double getFieldPrimitiveDouble();
    
    @Adapter(type = ElementConverterIdentitiyCast.class)
    public void setFieldPrimitiveDouble( double value, String additionalArgument );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    Mockito.when( this.propertyAccessor.getValue( Matchers.eq( "fieldString" ), Matchers.eq( String.class ),
                                                  (PropertyMetaInformation) Matchers.anyObject() ) ).thenReturn( "return string" );
    
    this.argumentCaptureForPropertyMetaInformation = ArgumentCaptor.forClass( PropertyMetaInformation.class );
    Mockito.when( this.propertyAccessor.getValue( Matchers.eq( "fieldPrimitiveDouble" ), Matchers.eq( double.class ),
                                                  this.argumentCaptureForPropertyMetaInformation.capture() ) )
           .thenReturn( 1234.2234 );
    
    Mockito.doNothing()
           .when( this.propertyAccessor )
           .setValue( Matchers.eq( "fieldPrimitiveDouble" ), Matchers.eq( 1234.223 ),
                      this.argumentCaptureForPropertyMetaInformation.capture() );
    
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
    PropertyMetaInformation propertyMetaInformation = this.argumentCaptureForPropertyMetaInformation.getValue();
    assertArrayEquals( new String[] { "more" }, propertyMetaInformation.getAdditionalArguments() );
    
    assertFalse( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().isEmpty() );
    assertFalse( propertyMetaInformation.getClassAnnotationAutowiredContainer().isEmpty() );
    
    assertTrue( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().containsAssignable( Adapter.class ) );
    assertTrue( propertyMetaInformation.getClassAnnotationAutowiredContainer().containsAssignable( XmlType.class ) );
    
    //
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldString" ), Matchers.eq( String.class ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldString" ),
                                                                      Matchers.eq( "new string value" ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldPrimitiveDouble" ),
                                                                      Matchers.eq( double.class ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldPrimitiveDouble" ),
                                                                      Matchers.eq( 1234.223 ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
  }
}
