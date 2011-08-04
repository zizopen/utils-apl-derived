package org.omnaest.utils.structure.table.concrete.internal.selection.scannable;

import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;

/**
 * A {@link ScannableStripeDataContainer} allows to retrieve {@link StripeData} instances in relation to {@link Cell#getElement()}
 * s
 * 
 * @author Omnaest
 * @param <E>
 */
public interface ScannableStripeDataContainer<E>
{
  
  /**
   * Returns a new {@link List} containing all {@link StripeData} instances containing elements which are between the fromKey and
   * the toKey. The upper and lower bound of {@link StripeData}s will be included in the result.
   * 
   * @param fromKey
   * @param toKey
   * @return
   */
  public List<StripeData<E>> determineStripeDataListForRange( final E fromKey, final E toKey );
  
  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey( E key );
  
  /**
   * @return
   * @see java.util.SortedMap#keySet()
   */
  public Set<E> keySet();
  
  /**
   * @see java.util.Map#clear()
   */
  public void clear();
  
  /**
   * @return
   * @see java.util.Map#size()
   */
  public int size();
  
  /**
   * Returns a new {@link List} containg all {@link StripeData} instances within the {@link ScannableStripeDataContainerIndex}
   * 
   * @return
   */
  public List<StripeData<E>> values();
  
  /**
   * @param key
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  public List<StripeData<E>> determineStripeDataListContainingElement( E key );
  
  /**
   * Returns true if the underlying structure could be build without errors
   * 
   * @return
   */
  public boolean isValid();
  
}