package org.omnaest.utils.structure.table.concrete.components.body;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeListContainer;

/**
 * @see TableInternal
 * @see CellResolver
 * @author Omnaest
 * @param <E>
 */
public class CellResolverImpl<E> implements CellResolver<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID = 7793892246619215531L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E> tableInternal    = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param tableInternal
   */
  public CellResolverImpl( TableInternal<E> tableInternal )
  {
    super();
    this.tableInternal = tableInternal;
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, int columnIndexPosition )
  {
    //
    Cell<E> retval = null;
    
    //
    Stripe<E> stripeRow = this.resolveStripe( StripeType.ROW, rowIndexPosition );
    Stripe<E> stripeColumn = this.resolveStripe( StripeType.COLUMN, columnIndexPosition );
    
    //
    Row<E> row = (Row<E>) stripeRow;
    Column<E> column = (Column<E>) stripeColumn;
    retval = this.resolveCell( row, column );
    
    //
    return retval;
  }
  
  @Override
  public Cell<E> resolveCell( Row<E> row, int columnIndexPosition )
  {
    //
    Cell<E> retval = null;
    
    //
    Stripe<E> stripeColumn = this.resolveStripe( StripeType.COLUMN, columnIndexPosition );
    
    //
    Column<E> column = (Column<E>) stripeColumn;
    retval = this.resolveCell( row, column );
    
    //
    return retval;
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, Column<E> column )
  {
    //
    Cell<E> retval = null;
    
    //
    Stripe<E> stripeRow = this.resolveStripe( StripeType.ROW, rowIndexPosition );
    
    //
    Row<E> row = (Row<E>) stripeRow;
    
    retval = this.resolveCell( row, column );
    
    //
    return retval;
  }
  
  @Override
  public Cell<E> resolveCell( Row<E> row, Column<E> column )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( row != null && column != null )
    {
      //
      for ( Cell<E> cell : row )
      {
        if ( column.contains( cell ) )
        {
          retval = cell;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves the {@link Stripe} for the given {@link StripeType} and index position from the internal {@link Table} reference.
   * 
   * @param stripeType
   * @param indexPosition
   * @return
   */
  protected Stripe<E> resolveStripe( StripeType stripeType, int indexPosition )
  {
    //
    Stripe<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      //
      StripeListContainer<E> stripeListContainer = this.tableInternal.getStripeListContainer();
      
      //
      StripeList<E> stripeList = stripeListContainer.getStripeList( stripeType );
      
      //
      retval = stripeList.get( indexPosition );
    }
    
    //
    return retval;
  }
  
}
