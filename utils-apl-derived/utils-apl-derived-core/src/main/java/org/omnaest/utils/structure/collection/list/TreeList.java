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
package org.omnaest.utils.structure.collection.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.element.factory.concrete.ArrayListFactory;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * This sorted {@link List} implementation does use an internal {@link TreeMap} to provide all features a {@link SortedSet} has,
 * except the fact that duplicate elements are allowed. <br>
 * <br>
 * But be aware, that the order of elements, which are considered equal due to the {@link Comparator#compare(Object, Object)}
 * method, is not determined. <br>
 * <br>
 * The {@link TreeList} has a quite fast {@link #add(Object)} method which uses the advantages of the underlying {@link TreeMap}.
 * All index based operations like {@link #get(int)} or {@link #remove(int)} are slow for large {@link List}s, since all elements
 * of the {@link TreeMap} have to be traversed to identify the right index position. <br>
 * <br>
 * The {@link TreeList} does break the contract of the {@link List#add(Object)}, as well as the {@link List#add(int, Object)} in
 * the way, that it does insert new elements at the appropriate sort position.
 * 
 * @author Omnaest
 * @param <E>
 */
public class TreeList<E> extends ListAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long           serialVersionUID = -3181777537000872260L;
  /* ********************************************** Variables ********************************************** */
  private final SortedMap<E, List<E>> elementToEqualElementListMap;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @see TreeList
   * @see TreeList#visitElements(ElementVisitor)
   * @author Omnaest
   * @param <E>
   */
  protected static interface ElementVisitor<E>
  {
    /**
     * This method is called for each visited element of the internal {@link SortedMap#keySet()}<br>
     * <br>
     * If false is returned, the traversal is stopped immediately
     * 
     * @param element
     * @param indexPosition
     * @param elementToEqualElementListMap
     * @return
     */
    public boolean visitKeyElements( E element, int indexPosition, SortedMap<E, List<E>> elementToEqualElementListMap );
    
    /**
     * This method is called for each visited element of a sub {@link List} of the internal {@link SortedMap#values()}<br>
     * <br>
     * If false is returned, the traversal is stopped immediately
     * 
     * @param element
     * @param indexPosition
     * @param elementList
     * @param subListIndexPosition
     * @return
     */
    public boolean visitSubList( E element, int indexPosition, List<E> elementList, int subListIndexPosition );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TreeList
   */
  public TreeList()
  {
    this( new Factory<SortedMap<E, List<E>>>()
    {
      @Override
      public SortedMap<E, List<E>> newInstance()
      {
        return new TreeMap<E, List<E>>();
      }
    } );
  }
  
  /**
   * @see TreeList
   * @param comparator
   */
  public TreeList( final Comparator<E> comparator )
  {
    this( new Factory<SortedMap<E, List<E>>>()
    {
      @Override
      public SortedMap<E, List<E>> newInstance()
      {
        return new TreeMap<E, List<E>>( comparator );
      }
    } );
    
  }
  
  /**
   * @see TreeList
   * @param collection
   */
  public TreeList( Collection<E> collection )
  {
    this();
    
    //
    this.addAll( collection );
  }
  
  /**
   * @see TreeList
   * @param comparator
   * @param collection
   */
  public TreeList( Comparator<E> comparator, Collection<E> collection )
  {
    this( comparator );
    
    //
    this.addAll( collection );
  }
  
  /**
   * @see TreeList
   * @param elementToEqualElementListMapFactory
   */
  protected TreeList( Factory<SortedMap<E, List<E>>> elementToEqualElementListMapFactory )
  {
    super();
    this.elementToEqualElementListMap = MapUtils.initializedSortedMap( elementToEqualElementListMapFactory.newInstance(),
                                                                       new ArrayListFactory<E>() );
  }
  
  @Override
  public int size()
  {
    //
    int retval = 0;
    
    //
    for ( E element : this.elementToEqualElementListMap.keySet() )
    {
      retval += 1 + this.elementToEqualElementListMap.get( element ).size();
    }
    
    // 
    return retval;
  }
  
  /**
   * This does add the given element at the right order position. This does break the contract of the {@link Collection}
   * interface, since the new element is not appended to the {@link Collection}.
   */
  @Override
  public boolean add( E e )
  {
    //
    boolean retval = false;
    
    //
    if ( e != null )
    {
      //
      if ( this.elementToEqualElementListMap.containsKey( e ) )
      {
        this.elementToEqualElementListMap.get( e ).add( e );
      }
      else
      {
        this.elementToEqualElementListMap.get( e );
      }
      
      //
      retval = true;
    }
    
    //
    return retval;
  }
  
  @Override
  public E get( final int index )
  {
    //
    final ElementHolder<E> retvalHolder = new ElementHolder<E>();
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      @Override
      public boolean visitKeyElements( E element, int indexPosition, SortedMap<E, List<E>> elementToEqualElementListMap )
      {
        return this.visitElements( element, indexPosition );
      }
      
      public boolean visitElements( E element, int indexPosition )
      {
        //
        boolean retval = true;
        
        //
        if ( indexPosition == index )
        {
          //
          retvalHolder.setElement( element );
          
          //
          retval = false;
        }
        
        //
        return retval;
      }
      
      @Override
      public boolean visitSubList( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        return this.visitElements( element, indexPosition );
      }
      
    };
    this.visitElements( elementVisitor );
    
    //
    return retvalHolder.getElement();
  }
  
  /**
   * Visitor method which allows to traverse over all elements in their current order and execute the
   * {@link ElementVisitor#visitKeyElements(Object, int, SortedMap)} on them.
   * 
   * @see ElementVisitor
   * @param elementVisitor
   */
  protected void visitElements( ElementVisitor<E> elementVisitor )
  {
    //
    int indexPosition = -1;
    outerloop: for ( E element : this.elementToEqualElementListMap.keySet() )
    {
      //
      indexPosition++;
      boolean traverseToNextElement = elementVisitor.visitKeyElements( element, indexPosition, this.elementToEqualElementListMap );
      if ( !traverseToNextElement )
      {
        break;
      }
      
      //
      final List<E> elementList = this.elementToEqualElementListMap.get( element );
      int subListIndexPosition = -1;
      for ( E innerElement : elementList )
      {
        //
        indexPosition++;
        subListIndexPosition++;
        
        //
        traverseToNextElement = elementVisitor.visitSubList( innerElement, indexPosition, elementList, subListIndexPosition );
        if ( !traverseToNextElement )
        {
          break outerloop;
        }
      }
    }
    
  }
  
  /**
   * Removes the element from the given index position an returns it. The newly given element will NOT be inserted at the given
   * index position, instead it will be inserted at its right sort position.
   */
  @Override
  public E set( int index, E element )
  {
    //
    E retval = this.remove( index );
    
    //
    this.add( element );
    
    // 
    return retval;
  }
  
  /**
   * The {@link #add(int, Object)} ignores the given index position and acts similar to the {@link #add(Object)} method
   */
  @Override
  public void add( int index, E element )
  {
    this.add( element );
  }
  
  @Override
  public E remove( final int index )
  {
    //
    final ElementHolder<E> retvalHolder = new ElementHolder<E>();
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      @Override
      public boolean visitKeyElements( E element, int indexPosition, SortedMap<E, List<E>> elementToEqualElementListMap )
      {
        //
        boolean retval = true;
        
        //
        if ( indexPosition == index )
        {
          //
          final List<E> elementList = elementToEqualElementListMap.get( element );
          elementToEqualElementListMap.remove( element );
          
          //
          if ( elementList != null && !elementList.isEmpty() )
          {
            //
            final E newFirstElement = elementList.remove( 0 );
            elementToEqualElementListMap.put( newFirstElement, elementList );
          }
          
          //
          retvalHolder.setElement( element );
          retval = false;
        }
        
        //
        return retval;
      }
      
      @Override
      public boolean visitSubList( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        //
        boolean retval = true;
        
        //
        if ( indexPosition == index )
        {
          //
          elementList.remove( subListIndexPosition );
          
          //
          retvalHolder.setElement( element );
          retval = false;
        }
        
        //
        return retval;
      }
    };
    this.visitElements( elementVisitor );
    
    // 
    return retvalHolder.getElement();
  }
  
  /**
   * Returns the index position of the first occurrence of an element which is {@link Object#equals(Object)} to the given
   * {@link Object}
   */
  @Override
  public int indexOf( final Object object )
  {
    //
    final ElementHolder<Integer> retvalHolder = new ElementHolder<Integer>( -1 );
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      @Override
      public boolean visitKeyElements( E element, int indexPosition, SortedMap<E, List<E>> elementToEqualElementListMap )
      {
        return this.visitElements( element, indexPosition );
      }
      
      @Override
      public boolean visitSubList( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        return this.visitElements( element, indexPosition );
      }
      
      private boolean visitElements( E element, int indexPosition )
      {
        //
        boolean retval = true;
        
        //
        if ( ObjectUtils.equals( object, element ) )
        {
          //
          retvalHolder.setElement( indexPosition );
          retval = false;
        }
        
        //
        return retval;
      }
      
    };
    this.visitElements( elementVisitor );
    
    // 
    return retvalHolder.getElement();
  }
  
  @Override
  public int lastIndexOf( final Object object )
  {
    //
    final ElementHolder<Integer> retvalHolder = new ElementHolder<Integer>( -1 );
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      private boolean hasAlreadyFoundEqualElement = false;
      
      @Override
      public boolean visitKeyElements( E element, int indexPosition, SortedMap<E, List<E>> elementToEqualElementListMap )
      {
        return this.visitElements( element, indexPosition );
      }
      
      @Override
      public boolean visitSubList( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        return this.visitElements( element, indexPosition );
      }
      
      private boolean visitElements( E element, int indexPosition )
      {
        //
        boolean retval = true;
        
        //
        if ( ObjectUtils.equals( object, element ) )
        {
          //
          this.hasAlreadyFoundEqualElement = true;
        }
        else if ( this.hasAlreadyFoundEqualElement )
        {
          //
          retvalHolder.setElement( indexPosition - 1 );
          retval = false;
        }
        
        //
        return retval;
      }
      
    };
    this.visitElements( elementVisitor );
    
    // 
    return retvalHolder.getElement();
  }
  
  /**
   * Removes the first occurring element within the {@link TreeList}, which is {@link Object#equals(Object)} to the given
   * {@link Object}
   * 
   * @return true: if any equal {@link Object} is found
   */
  @Override
  public boolean remove( Object object )
  {
    //
    boolean retval = false;
    
    //
    if ( this.elementToEqualElementListMap.containsKey( object ) )
    {
      //
      final List<E> keyElementList = new ArrayList<E>( this.elementToEqualElementListMap.keySet() );
      int indexOf = keyElementList.indexOf( object );
      if ( indexOf >= 0 )
      {
        //
        final List<E> elementList = this.elementToEqualElementListMap.remove( keyElementList.get( indexOf ) );
        if ( !elementList.isEmpty() )
        {
          //
          final E newFirstElement = elementList.remove( 0 );
          this.elementToEqualElementListMap.put( newFirstElement, elementList );
        }
        
        //
        retval = true;
      }
      
      //
      if ( !retval )
      {
        retval = this.elementToEqualElementListMap.get( object ).remove( object );
      }
    }
    
    // 
    return retval;
  }
  
  /**
   * Returns true if any element within the {@link TreeList} is {@link Object#equals(Object)} to the given {@link Object}
   */
  @Override
  public boolean contains( Object object )
  {
    //
    boolean retval = false;
    
    //
    if ( this.elementToEqualElementListMap.containsKey( object ) )
    {
      retval = new HashSet<E>( this.elementToEqualElementListMap.keySet() ).contains( object )
               || this.elementToEqualElementListMap.get( object ).contains( object );
    }
    
    // 
    return retval;
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.elementToEqualElementListMap.isEmpty();
  }
  
  @Override
  public void clear()
  {
    this.elementToEqualElementListMap.clear();
  }
  
}
