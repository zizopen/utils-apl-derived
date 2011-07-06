package org.omnaest.utils.structure.table.concrete.components;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;

/**
 * @see CellAndStripeResolver
 * @author Omnaest
 * @param <E>
 */
public abstract class CellAndStripeResolverAbstract<E> implements CellAndStripeResolver<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -5213948878611712685L;
  
  /* ********************************************** Methods ********************************************** */

  @Override
  public Cell<E> resolveCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resolveCell( this.resolveRow( rowIndexPosition ), this.resolveColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( Row<E> row, int columnIndexPosition )
  {
    return this.resolveCell( row, this.resolveColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, Column<E> column )
  {
    return this.resolveCell( this.resolveRow( rowIndexPosition ), column );
  }
  
}