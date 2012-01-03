package org.omnaest.utils.structure.element.factory.concrete;

import java.util.LinkedHashSet;
import java.util.Set;

import org.omnaest.utils.structure.element.factory.FactoryTypeAware;

/**
 * {@link FactoryTypeAware} creating new instances of {@link LinkedHashSet}
 * 
 * @param <E>
 * @author Omnaest
 */
public class LinkedHashSetFactory<E> implements FactoryTypeAware<Set<E>>
{
  
  @Override
  public Set<E> newInstance()
  {
    return new LinkedHashSet<E>();
  }
  
  @Override
  public Class<?> getInstanceType()
  {
    return LinkedHashSet.class;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "LinkedHashSetFactory []" );
    return builder.toString();
  }
  
}
