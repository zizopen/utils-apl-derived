package org.omnaest.utils.structure.table.concrete.components;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.concrete.components.body.StripeCore;
import org.omnaest.utils.structure.table.internal.TableInternal;

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
  
  /* ********************************************** Methods ********************************************** */

  public RowAndColumnImpl( TableInternal<E> tableInternal )
  {
    //
    super();
    
    //
    this.tableInternal = tableInternal;
  }
  
}
