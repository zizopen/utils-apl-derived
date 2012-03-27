/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.store.ElementStore.KeyExtractor;
import org.omnaest.utils.store.ElementStore.PersistenceAccessor;
import org.omnaest.utils.store.ElementStore.PersistenceExecutionControl;
import org.omnaest.utils.store.persistence.PersistenceAccessorDirectoryFiles;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.factory.Factory;

/**
 * @see ElementStore
 * @author Omnaest
 */
public class ElementStoreTest
{
  @Rule
  public ContiPerfRule                         contiPerfRule       = new ContiPerfRule();
  
  /* ********************************************** Constants ********************************************** */
  private static final AtomicInteger           counter             = new AtomicInteger();
  
  /* ********************************************** Variables ********************************************** */
  private final File                           directory           = new File( FileUtils.getTempDirectory(), "data" );
  private final PersistenceAccessor<TestClass> persistenceAccessor = new PersistenceAccessorDirectoryFiles<TestClass>(
                                                                                                                       this.directory );
  private ElementStore<TestClass>              elementStore        = new ElementStore<ElementStoreTest.TestClass>(
                                                                                                                   this.persistenceAccessor );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   */
  protected static class TestClass implements Serializable
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 8017013967182306038L;
    /* ********************************************** Variables ********************************************** */
    private final String      fieldString;
    private final Double      fieldDouble;
    private final Calendar    calendar;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @see TestClass
     * @param fieldString
     * @param fieldDouble
     */
    public TestClass( String fieldString, Double fieldDouble, Calendar calendar )
    {
      super();
      this.fieldString = fieldString;
      this.fieldDouble = fieldDouble;
      this.calendar = calendar;
    }
    
    /**
     * @return the fieldString
     */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    /**
     * @return the fieldDouble
     */
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "TestClass [fieldString=" );
      builder.append( this.fieldString );
      builder.append( ", fieldDouble=" );
      builder.append( this.fieldDouble );
      builder.append( ", calendar=" );
      builder.append( this.calendar );
      builder.append( "]" );
      return builder.toString();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.calendar == null ) ? 0 : this.calendar.hashCode() );
      result = prime * result + ( ( this.fieldDouble == null ) ? 0 : this.fieldDouble.hashCode() );
      result = prime * result + ( ( this.fieldString == null ) ? 0 : this.fieldString.hashCode() );
      return result;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TestClass ) )
      {
        return false;
      }
      TestClass other = (TestClass) obj;
      if ( this.calendar == null )
      {
        if ( other.calendar != null )
        {
          return false;
        }
      }
      else if ( !this.calendar.equals( other.calendar ) )
      {
        return false;
      }
      if ( this.fieldDouble == null )
      {
        if ( other.fieldDouble != null )
        {
          return false;
        }
      }
      else if ( !this.fieldDouble.equals( other.fieldDouble ) )
      {
        return false;
      }
      if ( this.fieldString == null )
      {
        if ( other.fieldString != null )
        {
          return false;
        }
      }
      else if ( !this.fieldString.equals( other.fieldString ) )
      {
        return false;
      }
      return true;
    }
    
    /**
     * @return the calendar
     */
    public Calendar getCalendar()
    {
      return this.calendar;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.elementStore.clear();
    this.elementStore.getExceptionHandlerRegistration().registerExceptionHandler( new ExceptionHandler()
    {
      @Override
      public void handleException( Exception e )
      {
        e.printStackTrace();
      }
    } );
  }
  
  @After
  public void tearDown()
  {
    //shutdown controlled
    this.elementStore.setPersistenceExecutionControl( new ElementStore.PersistenceExecutionControlImmediateExecution<TestClass>() )
                     .reloadFromPersistence()
                     .clear();
    assertTrue( this.elementStore.isEmpty() );
    
  }
  
  @Test
  public void testPersistence() throws InterruptedException
  
  {
    //
    final int numberOfElements = 100;
    final List<TestClass> testClassList = newTestClassList( numberOfElements );
    
    //
    this.elementStore.addAll( testClassList );
    assertEquals( new HashSet<TestClass>( this.elementStore ), new HashSet<TestClass>( testClassList ) );
    
    //
    final ElementStore<TestClass> elementStore2 = new ElementStore<ElementStoreTest.TestClass>( this.persistenceAccessor );
    assertEquals( new HashSet<TestClass>( testClassList ), new HashSet<TestClass>( elementStore2 ) );
  }
  
  @Test
  public void testAdd()
  {
    //
    final int numberOfElements = 100;
    final List<TestClass> testClassList = newTestClassList( numberOfElements );
    
    //
    this.elementStore.addAll( testClassList );
    assertTrue( this.elementStore.containsAll( testClassList ) );
    
    //
    this.elementStore.reloadFromPersistence();
    
    //
    for ( TestClass element : testClassList )
    {
      //
      this.elementStore.remove( element );
      assertFalse( this.elementStore.contains( element ) );
    }
    
    //
    this.elementStore.clear();
    assertTrue( this.elementStore.isEmpty() );
  }
  
  @PerfTest(invocations = 60, threads = 5)
  @Test
  public void testMultithreadedAdd() throws InterruptedException
  {
    //
    final int numberOfElements = 20;
    final List<TestClass> testClassList = newTestClassList( numberOfElements );
    
    //
    {
      //
      final TestClass testClass = ListUtils.elementAt( testClassList, (int) Math.round( Math.random() * ( numberOfElements - 1 ) ) );
      this.elementStore.add( testClass );
      assertTrue( this.elementStore.contains( testClass ) );
    }
    
    //
    this.elementStore.addAll( testClassList );
    assertTrue( this.elementStore.containsAll( testClassList ) );
    
    //
    this.elementStore.removeAll( testClassList );
  }
  
  @Test
  public void testNewIndex()
  {
    //
    final List<TestClass> testClassList = ElementStoreTest.newTestClassList( 100 );
    this.elementStore.addAll( testClassList );
    
    //
    final KeyExtractor<String, TestClass> keyExtractor = new KeyExtractor<String, TestClass>()
    {
      @Override
      public String getKey( TestClass element )
      {
        return element.getFieldString();
      }
    };
    final SortedMap<String, TestClass> index = this.elementStore.newIndex( keyExtractor );
    
    //
    assertEquals( testClassList.size(), index.size() );
    
    //
    for ( TestClass element : testClassList )
    {
      assertEquals( element, index.get( element.getFieldString() ) );
    }
    
    //
    final TestClass firstElement = ListUtils.firstElement( testClassList );
    this.elementStore.remove( firstElement );
    assertFalse( index.containsKey( firstElement.getFieldString() ) );
    
    //
    this.elementStore.add( firstElement );
    assertTrue( index.containsKey( firstElement.getFieldString() ) );
    
    //
    this.elementStore.reloadFromPersistence();
    assertTrue( index.containsKey( firstElement.getFieldString() ) );
    
  }
  
  @Test
  public void testNewIndexWithCalendar()
  {
    //
    final List<TestClass> testClassList = ElementStoreTest.newTestClassList( 100 );
    this.elementStore.addAll( testClassList );
    
    //
    final KeyExtractor<Calendar, TestClass> keyExtractor = new KeyExtractor<Calendar, TestClass>()
    {
      @Override
      public Calendar getKey( TestClass element )
      {
        return element.getCalendar();
      }
    };
    final SortedMap<Calendar, TestClass> index = this.elementStore.newIndex( keyExtractor );
    
    //
    final Calendar calendar = Calendar.getInstance();
    calendar.add( Calendar.DAY_OF_MONTH, 50 );
    final SortedMap<Calendar, TestClass> tailMap = index.tailMap( calendar );
    assertNotNull( tailMap );
    assertFalse( tailMap.isEmpty() );
    assertTrue( tailMap.size() < testClassList.size() );
    
  }
  
  @Test
  public void testExecutorServiceBasedPersistenceOperation() throws InterruptedException
  {
    //
    final PersistenceExecutionControl<TestClass> persistenceExecutionControl = new ElementStore.PersistenceExecutionControlUsingExecutorService<TestClass>();
    this.elementStore.setPersistenceExecutionControl( persistenceExecutionControl );
    
    //
    final List<TestClass> testClassList = ElementStoreTest.newTestClassList( 100 );
    this.elementStore.addAll( testClassList );
    assertTrue( this.elementStore.containsAll( testClassList ) );
    
    //
    this.elementStore.clear();
    
  }
  
  /**
   * @param numberOfElements
   * @return
   */
  private static List<TestClass> newTestClassList( final int numberOfElements )
  {
    //
    final Factory<TestClass> valueFactory = new Factory<TestClass>()
    {
      /* ********************************************** Methods ********************************************** */
      @Override
      public TestClass newInstance()
      {
        //
        final String fieldString = "test" + counter.incrementAndGet();
        final Double fieldDouble = 1.2345 + Math.random() * 10;
        final Calendar calendar = Calendar.getInstance();
        final TestClass element = new TestClass( fieldString, fieldDouble, calendar );
        
        //
        calendar.add( Calendar.DAY_OF_MONTH, (int) Math.round( Math.random() * 100 ) );
        
        //
        return element;
      }
    };
    return ListUtils.generateList( numberOfElements, valueFactory );
  }
  
}
