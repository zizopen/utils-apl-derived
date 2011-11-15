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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.omnaest.utils.beans.adapter.source.DefaultValue;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.element.converter.ElementConverterNumberToString;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToDouble;

/**
 * @see SourcePropertyAccessorToTypeAdapter
 * @author Omnaest
 */
public class SourcePropertyAccessorToTypeAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private SourcePropertyAccessor propertyAccessor               = Mockito.mock( SourcePropertyAccessor.class );
  private TestType               testType                       = SourcePropertyAccessorToTypeAdapter.newInstance( TestType.class,
                                                                                                                   this.propertyAccessor );
  
  private ElementHolder<String>  elementHolderDoubleReturnValue = new ElementHolder<String>();
  
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
  
  public static interface TestType2Splitted
  {
    @PropertyNameTemplate("field({0})")
    public String getFieldString( String tag );
    
    public double getFieldPrimitiveDouble();
    
    @Converter(type = ElementConverterStringToDouble.class)
    @DefaultValue("1.23")
    public Double getFieldDouble();
    
  }
  
  public static interface TestType2SplittedInternal extends TestType2Splitted
  {
    
    public void setFieldString( String value, String tag );
    
    public void setFieldPrimitiveDouble( double value );
    
    @DefaultValue("3.45")
    @Converter(type = ElementConverterNumberToString.class)
    public void setFieldDouble( Double value );
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
      
      Mockito.when( this.propertyAccessor.getValue( Matchers.eq( "fieldDouble" ), Matchers.eq( Double.class ),
                                                    (PropertyMetaInformation) Matchers.anyObject() ) )
             .thenAnswer( new Answer<String>()
             {
               
               @Override
               public String answer( InvocationOnMock invocation ) throws Throwable
               {
                 return SourcePropertyAccessorToTypeAdapterTest.this.elementHolderDoubleReturnValue.getElement();
               }
             } );
      
      Mockito.doNothing()
             .when( this.propertyAccessor )
             .setValue( Matchers.eq( "fieldPrimitiveDouble" ), Matchers.eq( 1234.223 ), (Class<?>) Matchers.anyObject(),
                        (PropertyMetaInformation) Matchers.anyObject() );
      
      Mockito.doAnswer( new Answer<Void>()
      {
        @Override
        public Void answer( InvocationOnMock invocation ) throws Throwable
        {
          SourcePropertyAccessorToTypeAdapterTest.this.elementHolderDoubleReturnValue.setElement( (String) invocation.getArguments()[1] );
          return null;
        }
      } )
             .when( this.propertyAccessor )
             .setValue( Matchers.eq( "fieldDouble" ), Matchers.anyObject(), (Class<?>) Matchers.anyObject(),
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
             .setValue( Matchers.eq( "fieldprimitivedouble" ), Matchers.eq( 1234.223 ), (Class<?>) Matchers.anyObject(),
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
                                                                      (Class<?>) Matchers.anyObject(),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldPrimitiveDouble" ),
                                                                      Matchers.eq( double.class ),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldPrimitiveDouble" ),
                                                                      Matchers.eq( 1234.223 ), (Class<?>) Matchers.anyObject(),
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
    PropertyAccessOption propertyAccessOption = PropertyAccessOption.PROPERTY_LOWERCASE;
    boolean isRegardingAdapterAnnotation = false;
    boolean isRegardingPropertyNameTemplateAnnotation = true;
    boolean isRegardingDefaultValueAnnotation = false;
    
    TestType2 testType2 = this.newInstance( TestType2.class, propertyAccessOption, isRegardingAdapterAnnotation,
                                            isRegardingPropertyNameTemplateAnnotation, isRegardingDefaultValueAnnotation );
    
    //
    assertEquals( "return string2", testType2.getFieldString( "LaLa" ) );
    assertEquals( 1234.2232, testType2.getFieldPrimitiveDouble(), 0.0001 );
    
    //
    testType2.setFieldString( "new string value", "LuLu" );
    testType2.setFieldPrimitiveDouble( 1234.223 );
    
    //
    ArgumentCaptor<PropertyMetaInformation> argumentCaptureForPropertyMetaInformation = ArgumentCaptor.forClass( PropertyMetaInformation.class );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "field(LaLa)" ), Matchers.eq( String.class ),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "field(LuLu)" ),
                                                                      Matchers.eq( "new string value" ),
                                                                      (Class<?>) Matchers.anyObject(),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldprimitivedouble" ),
                                                                      Matchers.eq( double.class ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldprimitivedouble" ),
                                                                      Matchers.eq( 1234.223 ), (Class<?>) Matchers.anyObject(),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    
    //
    PropertyMetaInformation propertyMetaInformation = argumentCaptureForPropertyMetaInformation.getValue();
    assertArrayEquals( new String[] { "LuLu" }, propertyMetaInformation.getAdditionalArguments() );
    
    assertFalse( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().isEmpty() );
    assertTrue( propertyMetaInformation.getClassAnnotationAutowiredContainer().isEmpty() );
    
    assertTrue( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().containsAssignable( PropertyNameTemplate.class ) );
    
  }
  
  @Test
  public void testNewInstanceSupertype()
  {
    //
    PropertyAccessOption propertyAccessOption = PropertyAccessOption.PROPERTY_LOWERCASE;
    boolean isRegardingAdapterAnnotation = false;
    boolean isRegardingPropertyNameTemplateAnnotation = true;
    boolean isRegardingDefaultValueAnnotation = false;
    
    TestType2SplittedInternal testType2SplittedInternal = this.newInstance( TestType2SplittedInternal.class,
                                                                            propertyAccessOption, isRegardingAdapterAnnotation,
                                                                            isRegardingPropertyNameTemplateAnnotation,
                                                                            isRegardingDefaultValueAnnotation );
    //
    assertEquals( "return string2", testType2SplittedInternal.getFieldString( "LaLa" ) );
    assertEquals( 1234.2232, testType2SplittedInternal.getFieldPrimitiveDouble(), 0.0001 );
    
    //
    testType2SplittedInternal.setFieldString( "new string value", "LuLu" );
    testType2SplittedInternal.setFieldPrimitiveDouble( 1234.223 );
    
    //
    ArgumentCaptor<PropertyMetaInformation> argumentCaptureForPropertyMetaInformation = ArgumentCaptor.forClass( PropertyMetaInformation.class );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "field(LaLa)" ), Matchers.eq( String.class ),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "field(LuLu)" ),
                                                                      Matchers.eq( "new string value" ),
                                                                      (Class<?>) Matchers.anyObject(),
                                                                      argumentCaptureForPropertyMetaInformation.capture() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( Matchers.eq( "fieldprimitivedouble" ),
                                                                      Matchers.eq( double.class ),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( Matchers.eq( "fieldprimitivedouble" ),
                                                                      Matchers.eq( 1234.223 ), (Class<?>) Matchers.anyObject(),
                                                                      (PropertyMetaInformation) Matchers.anyObject() );
    
    //
    PropertyMetaInformation propertyMetaInformation = argumentCaptureForPropertyMetaInformation.getValue();
    assertArrayEquals( new String[] { "LuLu" }, propertyMetaInformation.getAdditionalArguments() );
    
    assertFalse( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().isEmpty() );
    assertTrue( propertyMetaInformation.getClassAnnotationAutowiredContainer().isEmpty() );
    
    assertTrue( propertyMetaInformation.getPropertyAnnotationAutowiredContainer().containsAssignable( PropertyNameTemplate.class ) );
    
  }
  
  @Test
  public void testNewInstanceSupertypeAndAdapterAndDefaultValue()
  {
    //
    PropertyAccessOption propertyAccessOption = PropertyAccessOption.PROPERTY;
    boolean isRegardingAdapterAnnotation = true;
    boolean isRegardingPropertyNameTemplateAnnotation = true;
    boolean isRegardingDefaultValueAnnotation = true;
    
    TestType2SplittedInternal testType2SplittedInternal = this.newInstance( TestType2SplittedInternal.class,
                                                                            propertyAccessOption, isRegardingAdapterAnnotation,
                                                                            isRegardingPropertyNameTemplateAnnotation,
                                                                            isRegardingDefaultValueAnnotation );
    
    //
    this.elementHolderDoubleReturnValue.setElement( "1234.2232" );
    assertEquals( 1234.2232, testType2SplittedInternal.getFieldDouble(), 0.0001 );
    
    //
    {
      //
      final Double newValue = 567.3;
      testType2SplittedInternal.setFieldDouble( newValue );
      assertEquals( "" + newValue, this.elementHolderDoubleReturnValue.getElement() );
      assertEquals( newValue, testType2SplittedInternal.getFieldDouble(), 0.001 );
    }
    {
      //
      final Double newValue = null;
      testType2SplittedInternal.setFieldDouble( newValue );
      assertEquals( "3.45", this.elementHolderDoubleReturnValue.getElement() );
      assertEquals( 3.45, testType2SplittedInternal.getFieldDouble().doubleValue(), 0.01 );
    }
    {
      //
      this.elementHolderDoubleReturnValue.setElement( null );
      assertEquals( 1.23, testType2SplittedInternal.getFieldDouble().doubleValue(), 0.01 );
    }
    
  }
  
  /**
   * @param type
   * @param propertyAccessOption
   * @param isRegardingAdapterAnnotation
   * @param isRegardingPropertyNameTemplateAnnotation
   * @param isRegardingDefaultValueAnnotation
   * @return
   */
  private <T> T newInstance( Class<T> type,
                             PropertyAccessOption propertyAccessOption,
                             boolean isRegardingAdapterAnnotation,
                             boolean isRegardingPropertyNameTemplateAnnotation,
                             boolean isRegardingDefaultValueAnnotation )
  {
    
    return SourcePropertyAccessorToTypeAdapter.newInstance( type,
                                                            this.propertyAccessor,
                                                            new SourcePropertyAccessorToTypeAdapter.Configuration(
                                                                                                                   propertyAccessOption,
                                                                                                                   isRegardingAdapterAnnotation,
                                                                                                                   isRegardingPropertyNameTemplateAnnotation,
                                                                                                                   isRegardingDefaultValueAnnotation ) );
  }
  
}
