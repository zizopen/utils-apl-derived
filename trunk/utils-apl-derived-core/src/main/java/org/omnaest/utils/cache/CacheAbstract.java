package org.omnaest.utils.cache;

import org.omnaest.utils.structure.element.factory.Factory;

/**
 * Abstract {@link Cache} implementation
 * 
 * @author Omnaest
 */
abstract class CacheAbstract<K, V> implements Cache<K, V>
{
  
  private static final long serialVersionUID = 6436972766259961690L;
  
  @Override
  public V getOrCreate( K key, Factory<V> factory )
  {
    V retval = this.get( key );
    
    if ( retval == null )
    {
      retval = factory.newInstance();
      this.put( key, retval );
    }
    
    return retval;
  }
  
}
