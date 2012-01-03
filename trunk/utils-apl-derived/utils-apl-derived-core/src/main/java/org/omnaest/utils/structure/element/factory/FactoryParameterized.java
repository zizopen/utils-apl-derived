package org.omnaest.utils.structure.element.factory;

/**
 * Parameterized alternative to the {@link Factory}
 * 
 * @see #newInstance()
 * @see #newInstance(Object...)
 * @author Omnaest
 */
public abstract class FactoryParameterized<E, P> implements Factory<E>
{
  /**
   * Returns a new element instance for the given parameters
   * 
   * @param arguments
   * @return new instance
   */
  public abstract E newInstance( P... arguments );
  
  @SuppressWarnings("unchecked")
  @Override
  public E newInstance()
  {
    return this.newInstance( (P[]) new Object[0] );
  }
  
}
