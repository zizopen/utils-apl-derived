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
package org.omnaest.utils.structure.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.IndexArrayList;
import org.omnaest.utils.time.DurationCapture;

public class IndexArrayListTest
{
  @Test
  public void testAddE()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "Hallo" );
    
    assertNotNull( list );
    assertEquals( 1, list.size() );
    assertEquals( "Hallo", list.get( 0 ) );
    
    /*
     * performance test
     */
    //
    int addingNumber = 40000;
    DurationCapture timeDurationArrayList = DurationCapture.newInstance();
    DurationCapture timeDurationIndexedList = DurationCapture.newInstance();
    
    List<String> indexedList = new IndexArrayList<String>();
    List<String> arrayList = new ArrayList<String>();
    List<String> valuesList = new ArrayList<String>( addingNumber );
    
    for ( int ii = 0; ii < addingNumber; ii++ )
    {
      String randomString = String.valueOf( Math.abs( Math.random() * addingNumber ) );
      valuesList.add( randomString );
    }
    
    //arraylist
    timeDurationArrayList.startTimeMeasurement();
    for ( int ii = 0; ii < addingNumber; ii++ )
    {
      String addingString = valuesList.get( ii );
      arrayList.add( addingString );
    }
    timeDurationArrayList.stopTimeMeasurement();
    
    //indexed list
    timeDurationIndexedList.startTimeMeasurement();
    for ( int ii = 0; ii < addingNumber; ii++ )
    {
      String addingString = valuesList.get( ii );
      indexedList.add( addingString );
    }
    timeDurationIndexedList.stopTimeMeasurement();
    
    //
    //    System.out.println(timeDurationArrayList.getDuration() + ":"
    //                       + timeDurationIndexedList.getDuration());
    
    assertEquals( true, timeDurationArrayList.getDurationInMilliseconds() < timeDurationIndexedList.getDurationInMilliseconds() );
    
  }
  
  @Test
  public void testAddIntE()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "A" );
    list.add( "B" );
    list.add( "C" );
    list.add( 1, "Hallo" );
    
    assertNotNull( list );
    assertEquals( 4, list.size() );
    assertEquals( "Hallo", list.get( 1 ) );
    assertEquals( "B", list.get( 2 ) );
  }
  
  @Test
  public void testAddAllCollectionOfQextendsE()
  {
    List<String> list = new IndexArrayList<String>();
    List<String> addList = new ArrayList<String>();
    addList.add( "A" );
    addList.add( "B" );
    addList.add( "C" );
    
    list.addAll( addList );
    
    assertNotNull( list );
    assertEquals( 3, list.size() );
    assertEquals( "A", list.get( 0 ) );
    assertEquals( "C", list.get( 2 ) );
  }
  
  @Test
  public void testAddAllIntCollectionOfQextendsE()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> addList = new ArrayList<String>();
    addList.add( "A" );
    addList.add( "B" );
    addList.add( "C" );
    
    list.addAll( 1, addList );
    
    assertNotNull( list );
    assertEquals( 6, list.size() );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "A", list.get( 1 ) );
    assertEquals( "c", list.get( 5 ) );
  }
  
  @Test
  public void testClear()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "c" );
    
    list.clear();
    
    assertNotNull( list );
    assertEquals( 0, list.size() );
  }
  
  @Test
  public void testContains()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "c" );
    
    assertNotNull( list );
    assertEquals( true, list.contains( "b" ) );
    assertEquals( false, list.contains( "z" ) );
    
    /*
     * performance test
     */
    //
    int dataNumber = 40000;
    int searchingNumber = 500;
    DurationCapture timeDurationArrayList = DurationCapture.newInstance();
    DurationCapture timeDurationIndexedList = DurationCapture.newInstance();
    
    List<String> indexedList = new IndexArrayList<String>();
    List<String> arrayList = new ArrayList<String>();
    
    for ( int ii = 0; ii < dataNumber; ii++ )
    {
      String randomString = String.valueOf( Math.abs( Math.random() * 1000000 ) );
      arrayList.add( randomString );
      indexedList.add( randomString );
    }
    
    //arraylist
    timeDurationArrayList.startTimeMeasurement();
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = arrayList.get( ( ( ii + 1 ) * dataNumber / searchingNumber ) - 1 );
      assertEquals( true, arrayList.contains( searchString ) );
    }
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = "not to be found";
      assertEquals( false, arrayList.contains( searchString ) );
    }
    timeDurationArrayList.stopTimeMeasurement();
    
    //indexed list
    timeDurationIndexedList.startTimeMeasurement();
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = arrayList.get( ( ( ii + 1 ) * dataNumber / searchingNumber ) - 1 );
      assertEquals( true, indexedList.contains( searchString ) );
    }
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = "not to be found";
      assertEquals( false, indexedList.contains( searchString ) );
    }
    timeDurationIndexedList.stopTimeMeasurement();
    
    //
    assertEquals( true, timeDurationArrayList.getDurationInMilliseconds() > timeDurationIndexedList.getDurationInMilliseconds() );
    
    /*
     * logarithmic test
     */
    int initialDataNumber = 10;
    int initialSearchNumber = 1000000;
    int loopIntervalFaktor = 10;
    int loopNumbers = 3;
    DurationCapture timeDurationLog = DurationCapture.newInstance();
    
    //initial data
    indexedList = new IndexArrayList<String>();
    arrayList = new ArrayList<String>();
    
    List<Long> measurementList = new ArrayList<Long>( loopNumbers );
    int currentDataNumber = initialDataNumber;
    for ( int loopCounter = 0; loopCounter < loopNumbers; loopCounter++ )
    {
      //load data
      arrayList.clear();
      indexedList.clear();
      for ( int ii = 0; ii < currentDataNumber; ii++ )
      {
        String randomString = String.valueOf( Math.abs( Math.random() * currentDataNumber ) );
        arrayList.add( randomString );
        indexedList.add( randomString );
      }
      
      //test
      timeDurationLog.startTimeMeasurement();
      for ( int ii = 0; ii < initialSearchNumber; ii++ )
      {
        String searchString = arrayList.get( ii % initialDataNumber );
        assertEquals( true, indexedList.contains( searchString ) );
      }
      timeDurationLog.stopTimeMeasurement();
      
      //
      measurementList.add( timeDurationLog.getDurationInMilliseconds() );
      
      //
      currentDataNumber = currentDataNumber * loopIntervalFaktor;
    }
    
    //
    Long previousTimeDuration = null;
    for ( Long iMeasurementTime : measurementList )
    {
      //System.out.println( String.valueOf( iMeasurementTime ) );
      if ( previousTimeDuration != null )
      {
        boolean isFaster = previousTimeDuration == 0 || ( iMeasurementTime < previousTimeDuration * loopIntervalFaktor / 2 );
        assertEquals( true, isFaster );
      }
      previousTimeDuration = iMeasurementTime;
    }
  }
  
  @Test
  public void testContainsAll()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> containList = new ArrayList<String>();
    containList.add( "b" );
    containList.add( "c" );
    
    List<String> containNotList = new ArrayList<String>();
    containNotList.add( "b" );
    containNotList.add( "d" );
    
    assertNotNull( list );
    assertEquals( true, list.containsAll( containList ) );
    assertEquals( false, list.containsAll( containNotList ) );
  }
  
  @Test
  public void testGet()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "c" );
    
    assertNotNull( list );
    assertEquals( "b", list.get( 1 ) );
  }
  
  @Test
  public void testIndexOf()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    assertNotNull( list );
    assertEquals( 1, list.indexOf( "b" ) );
    assertEquals( 4, list.indexOf( "c" ) );
    assertEquals( -1, list.indexOf( "t" ) );
    
    assertEquals( 3, list.lastIndexOf( "b" ) );
    assertEquals( 4, list.lastIndexOf( "c" ) );
    assertEquals( -1, list.lastIndexOf( "t" ) );
    
    list.remove( "a" );
    
    assertEquals( 0, list.indexOf( "b" ) );
    assertEquals( 3, list.indexOf( "c" ) );
    
    list.add( 0, "a" );
    
    assertEquals( 1, list.indexOf( "b" ) );
    assertEquals( 4, list.indexOf( "c" ) );
    
    assertEquals( 3, list.lastIndexOf( "b" ) );
    assertEquals( 4, list.lastIndexOf( "c" ) );
    
    /*
     * performance test
     */
    //
    int dataNumber = 40000;
    int searchingNumber = 2000;
    DurationCapture timeDurationArrayList = DurationCapture.newInstance();
    DurationCapture timeDurationIndexedList = DurationCapture.newInstance();
    
    List<String> indexedList = new IndexArrayList<String>();
    List<String> arrayList = new ArrayList<String>();
    
    for ( int ii = 0; ii < dataNumber; ii++ )
    {
      String randomString = String.valueOf( Math.abs( Math.random() * 1000000 ) );
      arrayList.add( randomString );
      indexedList.add( randomString );
    }
    
    //arraylist
    timeDurationArrayList.startTimeMeasurement();
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = arrayList.get( ( ( ii + 1 ) * dataNumber / searchingNumber ) - 1 );
      assertEquals( true, arrayList.indexOf( searchString ) >= 0 );
    }
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = "not to be found";
      assertEquals( false, arrayList.indexOf( searchString ) >= 0 );
    }
    timeDurationArrayList.stopTimeMeasurement();
    
    //indexed list
    timeDurationIndexedList.startTimeMeasurement();
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = arrayList.get( ( ( ii + 1 ) * dataNumber / searchingNumber ) - 1 );
      boolean foundString = indexedList.indexOf( searchString ) >= 0;
      assertEquals( true, foundString );
    }
    for ( int ii = 0; ii < searchingNumber / 2; ii++ )
    {
      String searchString = "not to be found";
      assertEquals( false, indexedList.indexOf( searchString ) >= 0 );
    }
    timeDurationIndexedList.stopTimeMeasurement();
    
    //
    //assertEquals( true, timeDurationArrayList.getDuration() > timeDurationIndexedList.getDuration() * 10 );
    //    System.out.println(timeDurationArrayList.getDuration() + ":"
    //                       + timeDurationIndexedList.getDuration());   
    
  }
  
  @Test
  public void testIsEmpty()
  {
    List<String> list = new IndexArrayList<String>();
    
    assertEquals( true, list.isEmpty() );
    
    list.add( "a" );
    list.add( "b" );
    
    assertEquals( false, list.isEmpty() );
    
    list.clear();
    
    assertEquals( true, list.isEmpty() );
  }
  
  @Test
  public void testIterator()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> iteratorResultList = new ArrayList<String>();
    Iterator<String> listIterator = list.iterator();
    while ( listIterator.hasNext() )
    {
      iteratorResultList.add( listIterator.next() );
    }
    
    assertEquals( list.size(), iteratorResultList.size() );
    
    for ( int ii = 0; ii < iteratorResultList.size(); ii++ )
    {
      assertEquals( list.get( ii ), iteratorResultList.get( ii ) );
    }
    
  }
  
  @Test
  public void testLastIndexOf()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    assertNotNull( list );
    assertEquals( 3, list.lastIndexOf( "b" ) );
    assertEquals( 4, list.lastIndexOf( "c" ) );
    assertEquals( -1, list.lastIndexOf( "t" ) );
  }
  
  @Test
  public void testListIterator()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> iteratorResultList = new ArrayList<String>();
    Iterator<String> listIterator = list.listIterator();
    while ( listIterator.hasNext() )
    {
      iteratorResultList.add( listIterator.next() );
    }
    
    assertEquals( list.size(), iteratorResultList.size() );
    
    for ( int ii = 0; ii < iteratorResultList.size(); ii++ )
    {
      assertEquals( list.get( ii ), iteratorResultList.get( ii ) );
    }
  }
  
  @Test
  public void testListIteratorInt()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> iteratorResultList = new ArrayList<String>();
    Iterator<String> listIterator = list.listIterator( 1 );
    while ( listIterator.hasNext() )
    {
      iteratorResultList.add( listIterator.next() );
    }
    
    assertEquals( list.size() - 1, iteratorResultList.size() );
    
    for ( int ii = 0; ii < iteratorResultList.size() - 1; ii++ )
    {
      assertEquals( list.get( ii + 1 ), iteratorResultList.get( ii ) );
    }
  }
  
  @Test
  public void testRemoveObject()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    list.remove( "b" );
    
    assertNotNull( list );
    assertEquals( 4, list.size() );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "b", list.get( 2 ) );
    assertEquals( "c", list.get( 3 ) );
  }
  
  @Test
  public void testRemoveInt()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    list.remove( 2 );
    
    assertNotNull( list );
    assertEquals( 4, list.size() );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "b", list.get( 2 ) );
    assertEquals( "c", list.get( 3 ) );
  }
  
  @Test
  public void testRemoveAll()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> removeList = new ArrayList<String>( 0 );
    removeList.add( "b" );
    removeList.add( "c" );
    removeList.add( "d" );
    
    list.removeAll( removeList );
    
    assertNotNull( list );
    assertEquals( 3, list.size() );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "b", list.get( 2 ) );
  }
  
  @Test
  public void testRetainAll()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> retainList = new ArrayList<String>( 0 );
    retainList.add( "b" );
    retainList.add( "c" );
    retainList.add( "d" );
    
    list.retainAll( retainList );
    
    assertNotNull( list );
    assertEquals( 4, list.size() );
    assertEquals( "b", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "b", list.get( 2 ) );
    assertEquals( "c", list.get( 3 ) );
  }
  
  @Test
  public void testSet()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    list.set( 2, "d" );
    
    assertNotNull( list );
    assertEquals( 5, list.size() );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "d", list.get( 2 ) );
    assertEquals( "b", list.get( 3 ) );
    assertEquals( "c", list.get( 4 ) );
  }
  
  @Test
  public void testSize()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    assertNotNull( list );
    assertEquals( 5, list.size() );
  }
  
  @Test
  public void testSubList()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    List<String> subList = list.subList( 1, 3 );
    
    assertNotNull( list );
    assertNotNull( subList );
    assertEquals( 2, subList.size() );
    assertEquals( "b", subList.get( 0 ) );
    assertEquals( "b", subList.get( 1 ) );
  }
  
  @Test
  public void testToArray()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    Object[] objectArray = list.toArray();
    
    int index = 0;
    for ( Object iObject : objectArray )
    {
      assertEquals( list.get( index++ ), iObject );
    }
    
  }
  
  @Test
  public void testToArrayTArray()
  {
    List<String> list = new IndexArrayList<String>();
    list.add( "a" );
    list.add( "b" );
    list.add( "b" );
    list.add( "b" );
    list.add( "c" );
    
    String[] stringArray = new String[0];
    stringArray = list.toArray( stringArray );
    
    int index = 0;
    for ( String iString : stringArray )
    {
      assertEquals( list.get( index++ ), iString );
    }
  }
  
  @Test
  public void testIndexesOfElementsGreaterThan()
  {
    IndexArrayList<Integer> list = new IndexArrayList<Integer>();
    list.add( 1 );
    list.add( 2 );
    list.add( 3 );
    list.add( 4 );
    list.add( 5 );
    
    int[] indexPositions = list.indexesOfElementsGreaterThan( 3 );
    
    List<Integer> indexPositionList = new ArrayList<Integer>( 0 );
    CollectionUtils.addAll( indexPositionList, indexPositions );
    Collections.sort( indexPositionList );
    //CollectionUtil.printCollection(indexPositionList);
    
    assertNotNull( indexPositions );
    assertEquals( 2, indexPositions.length );
  }
  
  @Test
  public void testIndexesOfElementsLessThan()
  {
    IndexArrayList<Integer> list = new IndexArrayList<Integer>();
    list.add( 1 );
    list.add( 2 );
    list.add( 3 );
    list.add( 4 );
    list.add( 5 );
    
    int[] indexPositions = list.indexesOfElementsLessThan( 3 );
    
    List<Integer> indexPositionList = new ArrayList<Integer>( 0 );
    CollectionUtils.addAll( indexPositionList, indexPositions );
    Collections.sort( indexPositionList );
    //CollectionUtil.printCollection(indexPositionList);
    
    assertNotNull( indexPositions );
    assertEquals( 2, indexPositions.length );
  }
  
  @Test
  public void testIndexesOfElementsEqualOrBetween()
  {
    IndexArrayList<Integer> list = new IndexArrayList<Integer>();
    list.add( 1 );//     0
    list.add( 2 );//     1
    list.add( 5 );//2
    list.add( 7 );//3  3
    list.add( 9 );//   4
    list.add( 3 );//5
    list.add( 4 );//6
    list.add( 5 );//7
    
    //
    int[] indexPositions = list.indexesOfElementsEqualOrBetween( 3, 7 );
    
    List<Integer> indexPositionList = new ArrayList<Integer>( 0 );
    CollectionUtils.addAll( indexPositionList, indexPositions );
    Collections.sort( indexPositionList );
    //CollectionUtil.printCollection(indexPositionList);
    
    assertNotNull( indexPositions );
    assertEquals( 5, indexPositions.length );
    assertEquals( 2, indexPositionList.get( 0 ).intValue() );
    assertEquals( 3, indexPositionList.get( 1 ).intValue() );
    assertEquals( 5, indexPositionList.get( 2 ).intValue() );
    assertEquals( 6, indexPositionList.get( 3 ).intValue() );
    assertEquals( 7, indexPositionList.get( 4 ).intValue() );
    
    //
    indexPositions = list.indexesOfElementsEqualOrBetween( 7, 10 );
    
    indexPositionList = new ArrayList<Integer>( 0 );
    CollectionUtils.addAll( indexPositionList, indexPositions );
    Collections.sort( indexPositionList );
    //CollectionUtil.printCollection(indexPositionList);
    
    assertNotNull( indexPositions );
    assertEquals( 2, indexPositions.length );
    assertEquals( 3, indexPositionList.get( 0 ).intValue() );
    assertEquals( 4, indexPositionList.get( 1 ).intValue() );
    
    //
    indexPositions = list.indexesOfElementsEqualOrBetween( -5, 2 );
    
    indexPositionList = new ArrayList<Integer>( 0 );
    CollectionUtils.addAll( indexPositionList, indexPositions );
    Collections.sort( indexPositionList );
    //CollectionUtil.printCollection(indexPositionList);
    
    assertNotNull( indexPositions );
    assertEquals( 2, indexPositions.length );
    assertEquals( 0, indexPositionList.get( 0 ).intValue() );
    assertEquals( 1, indexPositionList.get( 1 ).intValue() );
    
    //
    indexPositions = list.indexesOfElementsEqualOrBetween( 10, 20 );
    assertNull( indexPositions );
    
  }
  
}
