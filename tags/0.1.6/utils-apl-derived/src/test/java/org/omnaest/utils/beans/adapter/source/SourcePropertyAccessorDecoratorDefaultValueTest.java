package org.omnaest.utils.beans.adapter.source;

import static org.junit.Assert.assertEquals;

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
    @DefaultValue(value = "defaultValueSet")
    public void setFieldString( String value );
    
    @DefaultValue(value = "defaultValueGet")
    public String getFieldString();
    
    @DefaultValue(value = "1.23")
    public void setFieldDouble( Double value );
    
    @DefaultValue(value = "3.45")
    public Double getFieldDouble();
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
    
  }
  
}
