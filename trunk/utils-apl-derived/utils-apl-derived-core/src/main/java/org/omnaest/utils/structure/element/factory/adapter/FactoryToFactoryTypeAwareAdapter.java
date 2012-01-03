package org.omnaest.utils.structure.element.factory.adapter;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.element.factory.FactoryTypeAware;

/**
 * Adapter which allows to use a {@link Factory} in combination with a given type as {@link FactoryTypeAware} instance
 * 
 * @see Factory
 * @see FactoryTypeAware
 * @author Omnaest
 */
public class FactoryToFactoryTypeAwareAdapter<E> implements FactoryTypeAware<E>
{
  /* ********************************************** Variables ********************************************** */
  private final Factory<E> factory;
  private final Class<E>   instanceType;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see FactoryToFactoryTypeAwareAdapter
   * @param factory
   * @param instanceType
   */
  public FactoryToFactoryTypeAwareAdapter( Factory<E> factory, Class<E> instanceType )
  {
    //
    super();
    this.factory = factory;
    this.instanceType = instanceType;
    
    //
    Assert.isNotNull( "Factory and instanceType must not be null", factory, instanceType );
  }
  
  @Override
  public E newInstance()
  {
    return this.factory.newInstance();
  }
  
  @Override
  public Class<?> getInstanceType()
  {
    return this.instanceType;
  }
}
