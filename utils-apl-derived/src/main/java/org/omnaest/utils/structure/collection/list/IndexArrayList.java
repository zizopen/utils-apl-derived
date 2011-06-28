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
package org.omnaest.utils.structure.collection.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This class implements the list interface.<br>
 * The implementation is based on an internal {@link ArrayList} with a second list acting as an index.<br>
 * What means that the list will need some more than twice the memory than an ArrayList and more time to add elements to the list.<br>
 * For the increased memory space used and the increased time used for adding elements, it offers look up methods, like
 * {@link IndexArrayList#contains(Object)}, {@link IndexArrayList#indexOf(Object)}, {@link IndexArrayList#lastIndexOf(Object)},
 * that are based on an index search algorithm.<br>
 * The algorithm uses binary search, and has by that a limited cost of log(n) for searching and adding, whereby n is the size of
 * the list.<br>
 * The order of the list is the same, as the order of the elements added. <br>
 * A limitation of the list is, that it can only be used for objects, that implement the {@link Comparable} interface. This is
 * caused by the use of a sorted internal table for the index.
 * 
 * @author Omnaest
 * @param <E>
 */
public class IndexArrayList<E extends Comparable<? super E>> implements IndexList<E>
{
  /*
   * Data
   */
  /** holds the list data in its orginal order */
  private ArrayList<E> dataList = null;
  
  /** used to fill the index list with the original data and its index position in the original list */
  private class DataAndIndexPostionInOriginalList
  {
    private E   data                    = null;
    private int indexPositionInDataList = -1;
    
    public DataAndIndexPostionInOriginalList( E e, int dataListIndexPosition )
    {
      this.data = e;
      this.indexPositionInDataList = dataListIndexPosition;
    }
    
    public int getIndexPositionInDataList()
    {
      return indexPositionInDataList;
    }
    
    public E getData()
    {
      return this.data;
    }
    
    public void setIndexPositionInDataList( int indexPositionInDataList )
    {
      this.indexPositionInDataList = indexPositionInDataList;
    }
  }
  
  /** index list is a simple sorted list, which holds the data and an index of the original list */
  private ArrayList<DataAndIndexPostionInOriginalList> indexList              = null;
  
  private final IndexElementComparator                 indexElementComparator = new IndexElementComparator();
  
  /*
   * Methods
   */

  /**
   * Creates a new empty indexed list.
   */
  public IndexArrayList()
  {
    this( 0 );
  }
  
  public IndexArrayList( int initialSize )
  {
    super();
    this.dataList = new ArrayList<E>( initialSize );
    this.indexList = new ArrayList<DataAndIndexPostionInOriginalList>( 0 );
  }
  
  /**
   * Creates a new indexed list, which is filled with all objects of the collection.
   * 
   * @param c
   */
  public IndexArrayList( Collection<? extends E> c )
  {
    this();
    this.addAll( c );
  }
  
  @Override
  public boolean add( E e )
  {
    /*
     * The new data object has to be added to the orginal data list, and to the index list.
     */
    boolean retval = true;
    
    //add to data
    retval = this.dataList.add( e );
    
    //add to index
    if ( retval )
    {
      this.addToIndexList( e, this.dataList.size() - 1 );
    }
    
    //
    return retval;
  }
  
  /**
   * Adds a new data object to the index list. To do this, the fact, that the list is sorted will be used, to determine the new
   * position by binary search.
   * 
   * @param e
   * @return
   */
  private void addToIndexList( E e, int dataListIndexPosition )
  {
    int indexPosition = this.determineIndexListIndexPosition( e );
    if ( indexPosition < 0 )
    {
      indexPosition = ( indexPosition + 1 ) * -1;
    }
    
    this.indexList.add( indexPosition, new DataAndIndexPostionInOriginalList( e, dataListIndexPosition ) );
  }
  
  private class IndexElementComparator implements Comparator<DataAndIndexPostionInOriginalList>
  {
    public int compare( E e1, E e2 )
    {
      //
      int retval;
      
      //
      if ( e1 != null && e2 != null )
      {
        retval = e1.compareTo( e2 );
      }
      else if ( e1 != null && e2 == null )
      {
        try
        {
          retval = e1.compareTo( e2 );
        }
        catch ( NullPointerException e )
        {
          retval = -1;
        }
      }
      else if ( e1 == null && e2 != null )
      {
        try
        {
          retval = -1 * e2.compareTo( e1 );
        }
        catch ( NullPointerException e )
        {
          retval = 1;
        }
      }
      else
      //both are null
      {
        retval = 0;
      }
      
      //
      return retval;
    }
    
    @Override
    public int compare( DataAndIndexPostionInOriginalList o1, DataAndIndexPostionInOriginalList o2 )
    {
      //
      int retval;
      
      //
      if ( o1 != null && o2 != null )
      {
        //
        E e1 = o1.getData();
        E e2 = o2.getData();
        
        //
        retval = this.compare( e1, e2 );
      }
      else if ( o1 != null && o2 == null )
      {
        retval = -1;
      }
      else if ( o1 == null && o2 != null )
      {
        retval = 1;
      }
      else
      {
        retval = 0;
      }
      
      //
      return retval;
    }
    
  }
  
  /**
   * Determines the index position of a data object within the indexlist. <br>
   * Index position belongs to the index list, and is not to be used with the original data list. <br>
   * To determine the position binary search is used on the sorted list.
   * 
   * @see Collections#binarySearch(List, Object)
   * @param e
   * @return
   */
  private int determineIndexListIndexPosition( E e )
  {
    //
    int retval = -1;
    
    //
    DataAndIndexPostionInOriginalList dataAndIndexPositionInOriginalList = new DataAndIndexPostionInOriginalList( e, 0 );
    //
    retval = Collections.binarySearch( this.indexList, dataAndIndexPositionInOriginalList, this.indexElementComparator );
    
    //
    return retval;
  }
  
  private class IndexBoundaries
  {
    private DataListWithIndexListIndexPosition lower = null;
    private DataListWithIndexListIndexPosition upper = null;
    
    public DataListWithIndexListIndexPosition getLower()
    {
      return lower;
    }
    
    public void setLower( DataListWithIndexListIndexPosition lower )
    {
      this.lower = lower;
    }
    
    public DataListWithIndexListIndexPosition getUpper()
    {
      return upper;
    }
    
    public void setUpper( DataListWithIndexListIndexPosition upper )
    {
      this.upper = upper;
    }
    
  }
  
  /**
   * Holds index positions of an element within the datalist and the indexlist.
   */
  private class DataListWithIndexListIndexPosition
  {
    private int dataListIndexPosition  = -1;
    private int indexListIndexPosition = -1;
    
    public int getDataListIndexPosition()
    {
      return dataListIndexPosition;
    }
    
    public void setDataListIndexPosition( int dataListIndexPosition )
    {
      this.dataListIndexPosition = dataListIndexPosition;
    }
    
    public int getIndexListIndexPosition()
    {
      return indexListIndexPosition;
    }
    
    public void setIndexListIndexPosition( int indexListIndexPosition )
    {
      this.indexListIndexPosition = indexListIndexPosition;
    }
    
  }
  
  private int[] determineDataListIndexPositions( E e )
  {
    //
    int[] retvals = null;
    
    //
    List<DataListWithIndexListIndexPosition> dataListWithIndexListIndexPositionList = this.determineDataListAndIndexListIndexPositionList( e );
    if ( dataListWithIndexListIndexPositionList.size() > 0 )
    {
      retvals = new int[dataListWithIndexListIndexPositionList.size()];
      for ( int ii = 0; ii < retvals.length; ii++ )
      {
        retvals[ii] = dataListWithIndexListIndexPositionList.get( ii ).getDataListIndexPosition();
      }
    }
    
    //
    return retvals;
  }
  
  private List<DataListWithIndexListIndexPosition> determineDataListAndIndexListIndexPositionList( E e )
  {
    //
    List<DataListWithIndexListIndexPosition> retlist = new ArrayList<DataListWithIndexListIndexPosition>( 0 );
    
    //
    List<Integer> indexListIndexPositionList = new ArrayList<Integer>( 0 );
    
    //
    int startIndexPosition = this.determineIndexListIndexPosition( e );
    
    if ( startIndexPosition >= 0 )
    {
      //
      indexListIndexPositionList.add( startIndexPosition );
      
      //
      int lowerIndexPosition = startIndexPosition - 1;
      int upperIndexPosition = startIndexPosition + 1;
      
      boolean loopAgain;
      do
      {
        //
        loopAgain = false;
        
        //
        if ( lowerIndexPosition >= 0 && lowerIndexPosition < this.indexList.size() )
        {
          E indexData = this.indexList.get( lowerIndexPosition ).getData();
          if ( ( e == null && indexData == null ) || indexData.equals( e ) )
          {
            indexListIndexPositionList.add( lowerIndexPosition );
            lowerIndexPosition--;
            
            //
            loopAgain = true;
          }
          
        }
      } while ( loopAgain );
      
      do
      {
        //
        loopAgain = false;
        
        //
        if ( upperIndexPosition >= 0 && upperIndexPosition < this.indexList.size() )
        {
          E indexData = this.indexList.get( upperIndexPosition ).getData();
          if ( ( e == null && indexData == null ) || indexData != null && ( indexData.equals( e ) ) )
          {
            indexListIndexPositionList.add( upperIndexPosition );
            upperIndexPosition++;
            
            //
            loopAgain = true;
          }
        }
      } while ( loopAgain );
    }
    
    //
    if ( indexListIndexPositionList.size() > 0 )
    {
      //
      for ( Integer iIndexPosition : indexListIndexPositionList )
      {
        DataListWithIndexListIndexPosition dataListWithIndexListIndexPosition = new DataListWithIndexListIndexPosition();
        dataListWithIndexListIndexPosition.setDataListIndexPosition( this.indexList.get( iIndexPosition )
                                                                                   .getIndexPositionInDataList() );
        dataListWithIndexListIndexPosition.setIndexListIndexPosition( iIndexPosition );
        retlist.add( dataListWithIndexListIndexPosition );
      }
    }
    
    //
    return retlist;
  }
  
  private IndexBoundaries determineDataListAndIndexListIndexPositionBoundaries( E e )
  {
    //
    IndexBoundaries retval = new IndexBoundaries();
    
    //
    List<DataListWithIndexListIndexPosition> dataListWithIndexListPositionList = this.determineDataListAndIndexListIndexPositionList( e );
    
    //    
    if ( dataListWithIndexListPositionList.size() > 0 )
    {
      Collections.sort( dataListWithIndexListPositionList, new Comparator<DataListWithIndexListIndexPosition>()
      {
        @Override
        public int compare( DataListWithIndexListIndexPosition o1, DataListWithIndexListIndexPosition o2 )
        {
          return Integer.valueOf( o1.getDataListIndexPosition() ).compareTo( Integer.valueOf( o2.getDataListIndexPosition() ) );
        }
      } );
      
      DataListWithIndexListIndexPosition upperData = dataListWithIndexListPositionList.get( dataListWithIndexListPositionList.size() - 1 );
      retval.setUpper( upperData );
      
      DataListWithIndexListIndexPosition lowerData = dataListWithIndexListPositionList.get( 0 );
      retval.setLower( lowerData );
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves the indexposition within the indexlist for a given indexposition from the datalist. This is a slow operation,
   * because it have possibly to iterate over a large part of index.
   * 
   * @param dataListIndexPosition
   * @return
   */
  private int determineIndexListIndexPosition( int dataListIndexPosition )
  {
    //
    int retval = -1;
    
    //
    E element = this.dataList.get( dataListIndexPosition );
    
    List<DataListWithIndexListIndexPosition> dataListWithIndexListIndexPositionList = this.determineDataListAndIndexListIndexPositionList( element );
    for ( DataListWithIndexListIndexPosition iDataListWithIndexListIndexPosition : dataListWithIndexListIndexPositionList )
    {
      if ( iDataListWithIndexListIndexPosition.getDataListIndexPosition() == dataListIndexPosition )
      {
        retval = iDataListWithIndexListIndexPosition.getIndexListIndexPosition();
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public void add( int index, E element )
  {
    this.dataList.add( index, element );
    this.updateIndexDataListIndexPositionsWithCorrectureOffset( index, +1 );
    this.addToIndexList( element, index );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> c )
  {
    boolean retval = true;
    
    //
    for ( E iElement : c )
    {
      boolean insertSuccess;
      
      //
      insertSuccess = this.dataList.add( iElement );
      if ( insertSuccess )
      {
        this.addToIndexList( iElement, this.dataList.size() - 1 );
      }
      
      //
      retval &= insertSuccess;
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean addAll( int index, Collection<? extends E> c )
  {
    boolean retval = true;
    
    //
    int indexOffset = 0;
    for ( E iElement : c )
    {
      int insertIndex = index + indexOffset;
      
      //
      this.dataList.add( insertIndex, iElement );
      this.addToIndexList( iElement, insertIndex );
      
      //
      indexOffset++;
    }
    
    //
    return retval;
  }
  
  @Override
  public void clear()
  {
    this.dataList.clear();
    this.indexList.clear();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean contains( Object o )
  {
    //
    boolean retval = false;
    
    //
    retval = this.determineIndexListIndexPosition( (E) o ) >= 0;
    
    //
    return retval;
  }
  
  @Override
  public boolean containsAll( Collection<?> c )
  {
    //
    boolean retval = true;
    
    //
    for ( Object iObjectElement : c )
    {
      retval &= this.contains( iObjectElement );
    }
    
    //
    return retval;
  }
  
  @Override
  public E get( int index )
  {
    return this.dataList.get( index );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public int indexOf( Object o )
  {
    //
    int retval = -1;
    
    //
    DataListWithIndexListIndexPosition lowerIndexPosition = this.determineDataListAndIndexListIndexPositionBoundaries( (E) o )
                                                                .getLower();
    if ( lowerIndexPosition != null )
    {
      retval = lowerIndexPosition.getDataListIndexPosition();
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the indexes of the elements greater than the as parameter given element.
   * 
   * @param element
   * @return
   */
  public int[] indexesOfElementsGreaterThan( E element )
  {
    //
    int[] retval = null;
    
    //
    int indexListStartIndexPosition = this.determineIndexListIndexPositionOfFirstElementGreaterThan( element );
    if ( indexListStartIndexPosition >= 0 )
    {
      //
      int[] indexListIndexPositions = new int[this.indexList.size() - indexListStartIndexPosition];
      for ( int ii = 0; ii < indexListIndexPositions.length; ii++ )
      {
        indexListIndexPositions[ii] = indexListStartIndexPosition + ii;
      }
      
      //
      int[] dataListIndexPositions = this.determineDataListIndexPositionsFromIndexListIndexPositions( indexListIndexPositions );
      
      //
      retval = dataListIndexPositions;
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the indexes of the elements less than the parameter element.
   * 
   * @param element
   * @return
   */
  public int[] indexesOfElementsLessThan( E element )
  {
    //
    int[] retval = null;
    
    //
    int indexListEndIndexPosition = this.determineIndexListIndexPositionOfLastElementLessThan( element );
    if ( indexListEndIndexPosition >= 0 )
    {
      //
      int[] indexListIndexPositions = new int[indexListEndIndexPosition + 1];
      for ( int ii = 0; ii < indexListIndexPositions.length; ii++ )
      {
        indexListIndexPositions[ii] = ii;
      }
      
      //
      int[] dataListIndexPositions = this.determineDataListIndexPositionsFromIndexListIndexPositions( indexListIndexPositions );
      
      //
      retval = dataListIndexPositions;
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the indexes of the elements that are equal or between the smallestElement and the largestElement.
   * 
   * @param element
   * @return
   */
  public int[] indexesOfElementsEqualOrBetween( E smallestElement, E largestElement )
  {
    //
    int[] retval = null;
    
    //
    int indexListStartIndexPosition = this.determineIndexListIndexPositionOfLastElementLessThan( smallestElement );
    int indexListEndIndexPosition = this.determineIndexListIndexPositionOfFirstElementGreaterThan( largestElement );
    if ( !( indexListEndIndexPosition < 0 && indexListStartIndexPosition < 0 ) )
    {
      if ( indexListStartIndexPosition < 0 )
      {
        indexListStartIndexPosition = 0;
      }
      else
      {
        indexListStartIndexPosition++;
      }
      if ( indexListEndIndexPosition < 0 )
      {
        indexListEndIndexPosition = this.indexList.size() - 1;
      }
      else
      {
        indexListEndIndexPosition--;
      }
    }
    
    //
    if ( indexListStartIndexPosition >= 0 && indexListEndIndexPosition >= 0
         && indexListStartIndexPosition <= indexListEndIndexPosition )
    {
      //
      int[] indexListIndexPositions = new int[indexListEndIndexPosition - indexListStartIndexPosition + 1];
      for ( int ii = 0; ii < indexListIndexPositions.length; ii++ )
      {
        indexListIndexPositions[ii] = indexListStartIndexPosition + ii;
      }
      
      //
      int[] dataListIndexPositions = this.determineDataListIndexPositionsFromIndexListIndexPositions( indexListIndexPositions );
      
      //
      retval = dataListIndexPositions;
    }
    
    //
    return retval;
  }
  
  /**
   * Reads all dataList index positions out for the given indexlist index positions.
   * 
   * @param indexListIndexPositions
   * @return
   */
  private int[] determineDataListIndexPositionsFromIndexListIndexPositions( int[] indexListIndexPositions )
  {
    //
    int[] retval = new int[indexListIndexPositions.length];
    
    //
    {
      int counter = 0;
      for ( int iIndexListIndexPosition : indexListIndexPositions )
      {
        retval[counter++] = this.indexList.get( iIndexListIndexPosition ).getIndexPositionInDataList();
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Finds the indexlist index position of the first element, that is lager than the given element.<br>
   * Uses binary search. <br>
   * Note: All following index positions will have larger data values, too, cause of the ordering.
   * 
   * @param element
   * @return x : if any element found: x >= 0, if no element found: 0 > x = (-insertposition)-1
   */
  private int determineIndexListIndexPositionOfFirstElementGreaterThan( E element )
  {
    //
    int retval = -1;
    
    int leftBorder = 0;
    int rightBorder = this.indexList.size();
    
    //
    int indexListSize = this.indexList.size();
    int previousMiddlePosition = -1;
    for ( boolean looping = true; looping; )
    {
      looping = false;
      int middlePosition = ( leftBorder + rightBorder ) / 2;
      
      //System.out.println("l m r: " + leftBorder + ":" + middlePosition + ":" + rightBorder);
      
      if ( middlePosition != previousMiddlePosition && middlePosition >= 0 && middlePosition < indexListSize )
      {
        //        
        E currentDataElement = this.indexList.get( middlePosition ).getData();
        int currentElementCompare = this.indexElementComparator.compare( currentDataElement, element );
        
        int previousElementCompare = -1;
        if ( middlePosition > 0 )
        {
          E previousDataElement = this.indexList.get( middlePosition - 1 ).getData();
          previousElementCompare = this.indexElementComparator.compare( previousDataElement, element );
        }
        
        if ( currentElementCompare > 0 )
        {
          if ( previousElementCompare <= 0 )
          {
            retval = middlePosition;
            looping = false;
          }
          else
          {
            rightBorder = middlePosition - 1;
            looping = true;
          }
        }
        else
        {
          leftBorder = middlePosition + 1;
          looping = true;
        }
        
        //
        previousMiddlePosition = middlePosition;
      }
      else
      {
        retval = ( middlePosition * -1 ) - 1;
        looping = false;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Finds the indexlist index position of the first element, that is lager than the given element.<br>
   * Uses binary search. <br>
   * Note: All following index positions will have larger data values, too, cause of the ordering.
   * 
   * @param element
   * @return x : if any element found: x >= 0, if no element found: 0 > x = (-insertposition)-1
   */
  private int determineIndexListIndexPositionOfLastElementLessThan( E element )
  {
    //
    int retval = -1;
    
    int leftBorder = 0;
    int rightBorder = this.indexList.size();
    
    //
    int indexListSize = this.indexList.size();
    int previousMiddlePosition = -1;
    for ( boolean looping = true; looping; )
    {
      looping = false;
      int middlePosition = ( leftBorder + rightBorder ) / 2;
      
      //System.out.println("l m r: " + leftBorder + ":" + middlePosition + ":" + rightBorder);
      
      if ( middlePosition != previousMiddlePosition && middlePosition >= 0 && middlePosition < indexListSize )
      {
        //        
        E currentDataElement = this.indexList.get( middlePosition ).getData();
        int currentElementCompare = this.indexElementComparator.compare( currentDataElement, element );
        
        int followingElementCompare = -1;
        if ( middlePosition + 1 < indexListSize )
        {
          E followingDataElement = this.indexList.get( middlePosition + 1 ).getData();
          followingElementCompare = this.indexElementComparator.compare( followingDataElement, element );
        }
        
        if ( currentElementCompare < 0 )
        {
          if ( followingElementCompare >= 0 )
          {
            retval = middlePosition;
            looping = false;
          }
          else
          {
            leftBorder = middlePosition + 1;
            looping = true;
          }
        }
        else
        {
          rightBorder = middlePosition - 1;
          looping = true;
        }
        
        //
        previousMiddlePosition = middlePosition;
      }
      else
      {
        retval = ( middlePosition * -1 ) - 1;
        looping = false;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns all index positions, where the data values at are equal to the given element.
   * 
   * @see #indexOf(Object)
   * @see #lastIndexOf(Object)
   * @param e
   * @return
   */
  public int[] indexesOf( E e )
  {
    return determineDataListIndexPositions( e );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.dataList.isEmpty();
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.dataList.iterator();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public int lastIndexOf( Object o )
  {
    //
    int retval = -1;
    
    //
    DataListWithIndexListIndexPosition dataListWithIndexListIndexPosition = this.determineDataListAndIndexListIndexPositionBoundaries(
                                                                                                                                       (E) o )
                                                                                .getUpper();
    if ( dataListWithIndexListIndexPosition != null )
    {
      retval = dataListWithIndexListIndexPosition.getDataListIndexPosition();
    }
    //
    return retval;
  }
  
  @Override
  public ListIterator<E> listIterator()
  {
    return this.dataList.listIterator();
  }
  
  @Override
  public ListIterator<E> listIterator( int index )
  {
    return this.dataList.listIterator( index );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean remove( Object o )
  {
    //
    boolean retval = false;
    
    //
    DataListWithIndexListIndexPosition dataListWithIndexListIndexPosition = this.determineDataListAndIndexListIndexPositionBoundaries(
                                                                                                                                       (E) o )
                                                                                .getLower();
    if ( dataListWithIndexListIndexPosition != null )
    {
      int dataListIndexPosition = dataListWithIndexListIndexPosition.getDataListIndexPosition();
      int indexListIndexPosition = dataListWithIndexListIndexPosition.getIndexListIndexPosition();
      
      retval = this.remove( dataListIndexPosition, indexListIndexPosition ) != null;
    }
    
    //
    return retval;
  }
  
  @Override
  public E remove( int dataListIndexPosition )
  {
    //
    E retval = null;
    
    //
    if ( dataListIndexPosition >= 0 && dataListIndexPosition < this.dataList.size() )
    {
      int indexListIndexPosition = this.determineIndexListIndexPosition( dataListIndexPosition );
      
      this.remove( dataListIndexPosition, indexListIndexPosition );
    }
    
    //
    return retval;
  }
  
  /**
   * Internal method to remove an object from the datalist and the indexlist.
   * 
   * @param dataListIndexPosition
   * @param indexListIndexPosition
   * @return
   */
  private E remove( int dataListIndexPosition, int indexListIndexPosition )
  {
    //
    E retval = null;
    
    //
    if ( indexListIndexPosition >= 0 && indexListIndexPosition < this.indexList.size() && dataListIndexPosition >= 0
         && dataListIndexPosition < this.dataList.size() )
    {
      this.indexList.remove( indexListIndexPosition );
      this.updateIndexDataListIndexPositionsWithCorrectureOffset( dataListIndexPosition, -1 );
      retval = this.dataList.remove( dataListIndexPosition );
    }
    
    //
    return retval;
  }
  
  /**
   * This method walks through the whole index and updates every elements index position value refering to the datalist and having
   * a datalist index postion larger or equal than the begin index position.<br>
   * Used by insert or remove methods.
   * 
   * @param beginDataListIndexPosition
   * @param correctureOffset
   */
  private void updateIndexDataListIndexPositionsWithCorrectureOffset( int beginDataListIndexPosition, int correctureOffset )
  {
    for ( int ii = 0; ii < this.indexList.size(); ii++ )
    {
      DataAndIndexPostionInOriginalList dataAndIndexPostionInOriginalList = this.indexList.get( ii );
      int dataListIndexPosition = dataAndIndexPostionInOriginalList.getIndexPositionInDataList();
      if ( dataListIndexPosition >= beginDataListIndexPosition )
      {
        dataAndIndexPostionInOriginalList.setIndexPositionInDataList( dataListIndexPosition + correctureOffset );
      }
    }
  }
  
  @Override
  public boolean removeAll( Collection<?> c )
  {
    //
    boolean retval = false;
    
    //
    for ( Object iObjectElement : c )
    {
      retval |= this.remove( iObjectElement );
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    //
    boolean retval = true;
    
    //
    ArrayList<E> removeList = new ArrayList<E>( 0 );
    for ( E iElement : this.dataList )
    {
      if ( !c.contains( iElement ) )
      {
        removeList.add( iElement );
      }
    }
    
    //
    retval = this.removeAll( removeList );
    
    //
    return retval;
  }
  
  @Override
  public E set( int index, E element )
  {
    //
    E retval = null;
    
    //
    if ( index >= 0 && this.dataList.size() > index )
    {
      //
      int indexListIndexPosition = this.determineIndexListIndexPosition( index );
      if ( indexListIndexPosition >= 0 )
      {
        this.indexList.remove( indexListIndexPosition );
      }
      this.addToIndexList( element, index );
      
      //
      retval = this.dataList.set( index, element );
    }
    
    //
    return retval;
  }
  
  @Override
  public int size()
  {
    return this.dataList.size();
  }
  
  @Override
  public List<E> subList( int fromIndex, int toIndex )
  {
    return this.dataList.subList( fromIndex, toIndex );
  }
  
  @Override
  public Object[] toArray()
  {
    return this.dataList.toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] a )
  {
    return this.dataList.toArray( a );
  }
  
  public void printDataList()
  {
    for ( E iElement : this.dataList )
    {
      System.out.println( iElement );
    }
  }
  
  public void printIndexList()
  {
    for ( DataAndIndexPostionInOriginalList iElement : this.indexList )
    {
      System.out.println( iElement.getData() + ":" + iElement.getIndexPositionInDataList() );
    }
  }
  
}
