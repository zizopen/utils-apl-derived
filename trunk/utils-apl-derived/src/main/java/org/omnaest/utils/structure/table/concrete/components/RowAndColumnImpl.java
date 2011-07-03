package org.omnaest.utils.structure.table.concrete.components;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.concrete.components.body.StripeCore;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal.TitleInternal;

/**
 * @see StripeCore
 * @see Row
 * @see Column
 * @author Omnaest
 * @param <E>
 */
public class RowAndColumnImpl<E> extends StripeCore<E> implements Row<E>, Column<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -2038384778853074830L;
  
  /* ********************************************** Variables ********************************************** */
  
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public Cell<E> getCell( int indexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public int determineIndexPosition()
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public Title getTitle()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Cell<E> getCell( Enum<?> title )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Cell<E> getCell( String title )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public <B> B asBeanAdapter( Class<B> beanClass )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}
