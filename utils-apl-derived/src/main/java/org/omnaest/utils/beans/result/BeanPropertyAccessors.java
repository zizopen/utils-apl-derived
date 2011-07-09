package org.omnaest.utils.beans.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Container for a ordered set of {@link BeanPropertyAccessor} instances.
 * 
 * @param <B>
 *          type of the Java Bean
 * @author Omnaest
 */
public class BeanPropertyAccessors<B> implements Iterable<BeanPropertyAccessor<B>>
{
  /* ********************************************** Variables ********************************************** */
  protected List<BeanPropertyAccessor<B>> beanPropertyAccessorList = new ArrayList<BeanPropertyAccessor<B>>();
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @see BeanPropertyAccessors
   */
  public BeanPropertyAccessors()
  {
    super();
  }
  
  /**
   * @see BeanPropertyAccessors
   * @param beanPropertyAccessorCollection
   */
  public BeanPropertyAccessors( Collection<BeanPropertyAccessor<B>> beanPropertyAccessorCollection )
  {
    super();
    this.beanPropertyAccessorList.addAll( beanPropertyAccessorCollection );
  }
  
  /**
   * Copies the values of all related properties of the given source Java Bean to the destination Java Bean. The affected
   * properties are based on the {@link BeanPropertyAccessor} instances within the {@link BeanPropertyAccessors} container.
   * 
   * @param beanSource
   * @param beanDestination
   */
  public void copyPropertyValues( B beanSource, B beanDestination )
  {
    //
    for ( BeanPropertyAccessor<B> beanPropertyAccessor : this.beanPropertyAccessorList )
    {
      try
      {
        //
        beanPropertyAccessor.copyPropertyValue( beanSource, beanDestination );
      }
      catch ( Exception e )
      {
      }
    }
  }
  
  /**
   * Returns the size of the {@link BeanPropertyAccessors} container
   */
  public int size()
  {
    return this.beanPropertyAccessorList.size();
  }
  
  /**
   * Returns true if the {@link BeanPropertyAccessors} container has no {@link BeanPropertyAccessor} instances
   * 
   * @return
   */
  public boolean isEmpty()
  {
    return this.beanPropertyAccessorList.isEmpty();
  }
  
  /**
   * Adds a {@link BeanPropertyAccessor} to the {@link BeanPropertyAccessors} container
   * 
   * @param e
   * @return
   */
  public boolean add( BeanPropertyAccessor<B> e )
  {
    return this.beanPropertyAccessorList.add( e );
  }
  
  /**
   * Removes a {@link BeanPropertyAccessor} instance from the {@link BeanPropertyAccessors} container
   * 
   * @param beanPropertyAccessor
   * @return
   */
  public boolean remove( BeanPropertyAccessor<B> beanPropertyAccessor )
  {
    return this.beanPropertyAccessorList.remove( beanPropertyAccessor );
  }
  
  /**
   * Adds {@link BeanPropertyAccessor} instances to the {@link BeanPropertyAccessors} container
   * 
   * @param c
   * @return
   */
  public boolean addAll( Collection<? extends BeanPropertyAccessor<B>> c )
  {
    return this.beanPropertyAccessorList.addAll( c );
  }
  
  /**
   * Clears the {@link BeanPropertyAccessors} container
   */
  public void clear()
  {
    this.beanPropertyAccessorList.clear();
  }
  
  @Override
  public Iterator<BeanPropertyAccessor<B>> iterator()
  {
    return this.beanPropertyAccessorList.iterator();
  }
  
  /**
   * Removes the {@link BeanPropertyAccessor} at the given index position
   * 
   * @param index
   * @return
   */
  public BeanPropertyAccessor<B> remove( int index )
  {
    return this.beanPropertyAccessorList.remove( index );
  }
  
  /**
   * Gets the {@link BeanPropertyAccessor} at the given index position
   * 
   * @param index
   * @return
   */
  public BeanPropertyAccessor<B> get( int index )
  {
    return this.beanPropertyAccessorList.get( index );
  }
  
}
