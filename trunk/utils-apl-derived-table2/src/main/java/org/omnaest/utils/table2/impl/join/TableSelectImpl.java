/*******************************************************************************
 * Copyright 2012 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.table2.impl.join;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.operation.foreach.Range;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentity;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table2.ImmutableColumn;
import org.omnaest.utils.table2.ImmutableColumn.ColumnIdentity;
import org.omnaest.utils.table2.ImmutableRow;
import org.omnaest.utils.table2.ImmutableTable;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSelect;
import org.omnaest.utils.table2.TableSelect.Predicate.FilterRow;
import org.omnaest.utils.table2.TableSelect.TableJoin;
import org.omnaest.utils.table2.TableSelect.TableSelectExecution;
import org.omnaest.utils.table2.impl.ArrayTable;

import com.google.common.base.Joiner;

/**
 * @see TableSelect
 * @author Omnaest
 * @param <E>
 */
public class TableSelectImpl<E> implements TableSelect<E>, TableJoin<E>, TableSelectExecution<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private List<Predicate<E>>  predicateList    = new ArrayList<Predicate<E>>();
  private List<ColumnJoin<E>> columnJoinList   = new ArrayList<TableSelectImpl.ColumnJoin<E>>();
  private List<Bucket<E>>     closedBucketList = new ArrayList<Bucket<E>>();
  private Bucket<E>           bucket;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static interface FilterRowIdentifiable<E> extends FilterRow<E>
  {
    public int rowIndex();
  }
  
  public static class FilterRowFilterer<E>
  {
    private final Iterable<FilterRowIdentifiable<E>> filterRowIterable;
    private final Iterable<Predicate<E>>             predicateIterable;
    
    public FilterRowFilterer( Iterable<FilterRowIdentifiable<E>> filterRowIterable, Iterable<Predicate<E>> predicateIterable )
    {
      super();
      this.filterRowIterable = filterRowIterable;
      this.predicateIterable = predicateIterable;
    }
    
    public BitSet calculateFilterResult()
    {
      final BitSet retval = new BitSet();
      
      for ( FilterRowIdentifiable<E> filterRow : this.filterRowIterable )
      {
        //
        final int rowIndex = filterRow.rowIndex();
        
        //
        boolean including = true;
        for ( Predicate<E> predicate : this.predicateIterable )
        {
          if ( !predicate.isIncluding( filterRow ) )
          {
            including = false;
            break;
          }
        }
        
        //
        if ( including )
        {
          retval.set( rowIndex );
        }
      }
      
      return retval;
    }
  }
  
  private static abstract class FilterRowAbstract<E> implements FilterRow<E>
  {
    @Override
    public E getElement( ImmutableTable<E> table, int columnIndex )
    {
      E retval = null;
      if ( table != null )
      {
        ImmutableColumn<E> immutableColumn = table.column( columnIndex );
        if ( immutableColumn != null )
        {
          ColumnIdentity<E> columnIdentity = immutableColumn.id();
          retval = this.getElement( columnIdentity );
        }
      }
      return retval;
    }
  }
  
  private static class FilterRowComposite<E> extends FilterRowAbstract<E>
  {
    private final List<FilterRow<E>> filterRowList;
    
    public FilterRowComposite( List<FilterRow<E>> filterRowList )
    {
      super();
      this.filterRowList = filterRowList;
    }
    
    @Override
    public E getElement( ColumnIdentity<E> columnIdentity )
    {
      E retval = null;
      for ( FilterRow<E> filterRow : this.filterRowList )
      {
        final E element = filterRow.getElement( columnIdentity );
        if ( element != null )
        {
          retval = element;
          break;
        }
      }
      return retval;
    }
  }
  
  private static class CrossProductFilterRowGenerator<E> implements Iterable<FilterRow<E>>
  {
    private Iterable<FilterRow<E>> filterRowIterableLeft;
    private Iterable<FilterRow<E>> filterRowIterableRight;
    
    @SuppressWarnings("unchecked")
    public CrossProductFilterRowGenerator( List<Iterable<? extends FilterRow<E>>> filterRowIterableList )
    {
      super();
      
      this.filterRowIterableLeft = (Iterable<FilterRow<E>>) ListUtils.firstElement( filterRowIterableList );
      final List<Iterable<? extends FilterRow<E>>> reducedFilterRowIteratorList = ListUtils.removeFirstToNewList( filterRowIterableList );
      if ( reducedFilterRowIteratorList.size() > 1 )
      {
        this.filterRowIterableRight = new CrossProductFilterRowGenerator<E>( reducedFilterRowIteratorList );
      }
      else if ( reducedFilterRowIteratorList.size() == 1 )
      {
        this.filterRowIterableRight = (Iterable<FilterRow<E>>) ListUtils.firstElement( reducedFilterRowIteratorList );
      }
      else
      {
        this.filterRowIterableRight = null;
      }
      
    }
    
    @Override
    public Iterator<FilterRow<E>> iterator()
    {
      //
      if ( this.filterRowIterableRight == null )
      {
        return this.filterRowIterableLeft.iterator();
      }
      
      //
      final Iterator<FilterRow<E>> iteratorLeft = this.filterRowIterableLeft.iterator();
      final Factory<Iterator<FilterRow<E>>> iteratorRightFactory = new Factory<Iterator<FilterRow<E>>>()
      {
        @Override
        public Iterator<FilterRow<E>> newInstance()
        {
          return CrossProductFilterRowGenerator.this.filterRowIterableRight.iterator();
        }
      };
      
      return new Iterator<FilterRow<E>>()
      {
        private FilterRow<E>           filterRowRight = null;
        private boolean                resolved       = false;
        
        private FilterRow<E>           filterRowLeft  = null;
        private Iterator<FilterRow<E>> iteratorRight  = null;
        
        @Override
        public boolean hasNext()
        {
          this.resolveNextFilterRowListIfUnresolved();
          return this.filterRowRight != null && this.filterRowLeft != null;
        }
        
        private void resolveNextFilterRowListIfUnresolved()
        {
          if ( !this.resolved )
          {
            this.filterRowRight = null;
            
            if ( this.iteratorRight != null && !this.iteratorRight.hasNext() )
            {
              this.filterRowLeft = null;
              this.iteratorRight = null;
            }
            
            if ( this.iteratorRight == null )
            {
              this.iteratorRight = iteratorRightFactory.newInstance();
            }
            if ( this.iteratorRight != null && this.iteratorRight.hasNext() )
            {
              this.filterRowRight = this.iteratorRight.next();
            }
            if ( this.filterRowLeft == null )
            {
              if ( iteratorLeft.hasNext() )
              {
                this.filterRowLeft = iteratorLeft.next();
              }
            }
            
            this.resolved = true;
          }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public FilterRow<E> next()
        {
          if ( this.hasNext() )
          {
            this.resolved = false;
            return new FilterRowComposite<E>( Arrays.asList( this.filterRowLeft, this.filterRowRight ) );
          }
          return null;
        }
        
        @Override
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
    
  }
  
  /**
   * @author Omnaest
   * @param <E>
   */
  private static class ColumnJoin<E>
  {
    private final Set<ColumnIdentity<E>> columnIdentitySet = new HashSet<ColumnIdentity<E>>();
    
    public boolean contains( ColumnIdentity<E> columnIdentity )
    {
      return this.columnIdentitySet.contains( columnIdentity );
    }
    
    public boolean add( ColumnIdentity<E> columnIdentity )
    {
      return this.columnIdentitySet.add( columnIdentity );
    }
    
    public void mergeIntoThis( ColumnJoin<E> columnJoin )
    {
      if ( columnJoin != null )
      {
        final Set<ColumnIdentity<E>> columnIdentitySet = columnJoin.getColumnIdentitySet();
        this.columnIdentitySet.addAll( columnIdentitySet );
        columnIdentitySet.clear();
      }
    }
    
    public Set<ColumnIdentity<E>> getColumnIdentitySet()
    {
      return this.columnIdentitySet;
    }
    
  }
  
  private static class PreparedColumnJoin<E>
  {
    private final Map<ColumnJoin<E>, Set<E>> columnJoinToIntersectionMap = new HashMap<ColumnJoin<E>, Set<E>>();
    
    public PreparedColumnJoin( List<ColumnJoin<E>> columnJoinList )
    {
      super();
      
      for ( ColumnJoin<E> columnJoin : columnJoinList )
      {
        Set<ColumnIdentity<E>> columnIdentitySet = columnJoin.getColumnIdentitySet();
        Set<Set<E>> setOfElementSet = SetUtils.convert( columnIdentitySet, new ElementConverter<ColumnIdentity<E>, Set<E>>()
        {
          @Override
          public Set<E> convert( ColumnIdentity<E> columnIdentity )
          {
            return columnIdentity.getColumn().to().set();
          }
        } );
        Set<E> intersection = SetUtils.intersection( setOfElementSet );
        this.columnJoinToIntersectionMap.put( columnJoin, intersection );
      }
    }
    
    private Map<ColumnIdentity<E>, Set<E>> getColumnIdentityToIntersectionMap( ImmutableTable<E> table )
    {
      final Map<ColumnIdentity<E>, Set<E>> retmap = new HashMap<ImmutableColumn.ColumnIdentity<E>, Set<E>>();
      for ( ColumnJoin<E> columnJoin : this.columnJoinToIntersectionMap.keySet() )
      {
        Set<ColumnIdentity<E>> columnIdentitySet = columnJoin.getColumnIdentitySet();
        for ( ColumnIdentity<E> columnIdentity : columnIdentitySet )
        {
          if ( ObjectUtils.equals( table, columnIdentity.getTable() ) )
          {
            Set<E> intersection = this.columnJoinToIntersectionMap.get( columnJoin );
            retmap.put( columnIdentity, intersection );
          }
        }
      }
      return retmap;
    }
    
    public Predicate<E> newIntersectionPredicate( ImmutableTable<E> table )
    {
      final Map<ColumnIdentity<E>, Set<E>> columnIdentityToIntersectionMap = this.getColumnIdentityToIntersectionMap( table );
      return new Predicate<E>()
      {
        @Override
        public boolean isIncluding( FilterRow<E> row )
        {
          boolean retval = true;
          for ( ColumnIdentity<E> columnIdentity : columnIdentityToIntersectionMap.keySet() )
          {
            Set<E> intersection = columnIdentityToIntersectionMap.get( columnIdentity );
            E element = row.getElement( columnIdentity );
            if ( !intersection.contains( element ) )
            {
              retval = false;
              break;
            }
          }
          return retval;
        }
      };
    }
  }
  
  /**
   * Holds the currently modifiable join data
   * 
   * @author Omnaest
   * @param <E>
   */
  private static class Bucket<E>
  {
    private final class ElementConverterRowToFilterRow implements ElementConverter<ImmutableRow<E>, FilterRowIdentifiable<E>>
    {
      private final Set<ColumnIdentity<E>> columnIdentitySet;
      
      private ElementConverterRowToFilterRow( Set<ColumnIdentity<E>> columnIdentitySet )
      {
        this.columnIdentitySet = columnIdentitySet;
      }
      
      @Override
      public FilterRowIdentifiable<E> convert( final ImmutableRow<E> immutableRow )
      {
        final Set<ColumnIdentity<E>> columnIdentitySet = this.columnIdentitySet;
        return new FilterRowIdentifiable<E>()
        {
          @Override
          public E getElement( ColumnIdentity<E> columnIdentity )
          {
            E retval = null;
            if ( columnIdentitySet.contains( columnIdentity ) )
            {
              retval = immutableRow.getCellElement( columnIdentity.getColumnIndex() );
            }
            return retval;
          }
          
          @Override
          public E getElement( ImmutableTable<E> table, int columnIndex )
          {
            E retval = null;
            if ( table != null )
            {
              ImmutableColumn<E> immutableColumn = table.column( columnIndex );
              if ( immutableColumn != null )
              {
                ColumnIdentity<E> columnIdentity = immutableColumn.id();
                retval = this.getElement( columnIdentity );
              }
            }
            return retval;
          }
          
          @Override
          public int rowIndex()
          {
            return immutableRow.index();
          }
        };
      }
    }
    
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final ImmutableTable<E>        table;
    private final List<ImmutableColumn<E>> columnList    = new ArrayList<ImmutableColumn<E>>();
    private List<Predicate<E>>             predicateList = new ArrayList<Predicate<E>>();
    
    /* *************************************************** Methods **************************************************** */
    
    public Bucket( ImmutableTable<E> table )
    {
      super();
      this.table = table;
    }
    
    public int columnSize()
    {
      return this.table.columnSize();
    }
    
    public void addColumnByIndex( int columnIndex )
    {
      final ImmutableColumn<E> column = this.table.column( columnIndex );
      if ( column != null )
      {
        this.columnList.add( column );
      }
    }
    
    public boolean add( TableSelect.Predicate<E> e )
    {
      return this.predicateList.add( e );
    }
    
    public Iterable<FilterRowIdentifiable<E>> newUnfilteredFilterRowIterable()
    {
      final Set<ColumnIdentity<E>> columnIdentitySet = this.determineColumnIdentitySet();
      return IterableUtils.adapter( this.table, new ElementConverterRowToFilterRow( columnIdentitySet ) );
    }
    
    public Iterable<FilterRowIdentifiable<E>> newFilteredFilterRowIterable( final BitSet filterResult )
    {
      final Set<ColumnIdentity<E>> columnIdentitySet = this.determineColumnIdentitySet();
      final ImmutableTable<E> table = this.table;
      final Iterable<ImmutableRow<E>> rowIterable = new Iterable<ImmutableRow<E>>()
      {
        @Override
        public Iterator<ImmutableRow<E>> iterator()
        {
          return new Iterator<ImmutableRow<E>>()
          {
            private int     index    = filterResult.nextSetBit( 0 );
            private boolean resolved = true;
            
            @Override
            public boolean hasNext()
            {
              if ( !this.resolved )
              {
                this.index = filterResult.nextSetBit( this.index + 1 );
                this.resolved = true;
              }
              
              return this.index >= 0;
            }
            
            @Override
            public ImmutableRow<E> next()
            {
              if ( this.hasNext() )
              {
                this.resolved = false;
                return table.row( this.index );
              }
              throw new NoSuchElementException();
            }
            
            @Override
            public void remove()
            {
              throw new UnsupportedOperationException();
            }
          };
        }
      };
      return IterableUtils.adapter( rowIterable, new ElementConverterRowToFilterRow( columnIdentitySet ) );
    }
    
    private Set<ColumnIdentity<E>> determineColumnIdentitySet()
    {
      final Set<ColumnIdentity<E>> columnIdentitySet = new HashSet<ImmutableColumn.ColumnIdentity<E>>();
      for ( ImmutableColumn<E> immutableColumn : this.table.columns() )
      {
        columnIdentitySet.add( immutableColumn.id() );
      }
      return columnIdentitySet;
    }
    
    public ImmutableTable<E> getTable()
    {
      return this.table;
    }
    
    public List<Predicate<E>> getPredicateList()
    {
      return this.predicateList;
    }
    
    public Set<ColumnIdentity<E>> getSelectedColumnIdentitySet()
    {
      final Set<ColumnIdentity<E>> columnIdentitySet = new LinkedHashSet<ImmutableColumn.ColumnIdentity<E>>();
      for ( ImmutableColumn<E> immutableColumn : this.columnList )
      {
        columnIdentitySet.add( immutableColumn.id() );
      }
      return columnIdentitySet;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  public TableSelectImpl( Table<E> table )
  {
    this.bucket = new Bucket<E>( table );
  }
  
  @Override
  public TableJoin<E> allColumns()
  {
    return this.columns( 0, new Range( 1, this.bucket.columnSize() - 1 ).toIntArray() );
  }
  
  @Override
  public TableSelect<E> allColumns( ImmutableTable<E> table )
  {
    for ( ImmutableColumn<E> column : table.columns() )
    {
      this.column( column );
    }
    return this;
  }
  
  @Override
  public TableSelect<E> column( ImmutableTable<E> table, int columnIndex )
  {
    final Set<Bucket<E>> bucketSet = this.determineBucketSetFor( table );
    for ( Bucket<E> bucket : bucketSet )
    {
      bucket.addColumnByIndex( columnIndex );
    }
    
    return this;
  }
  
  private Set<Bucket<E>> determineBucketSetFor( ImmutableTable<E> table )
  {
    final Set<Bucket<E>> bucketSet = new HashSet<Bucket<E>>();
    {
      List<Bucket<E>> bucketList = ListUtils.addToNewList( this.closedBucketList, this.bucket );
      for ( Bucket<E> bucket : bucketList )
      {
        if ( ObjectUtils.equals( table, bucket.getTable() ) )
        {
          bucketSet.add( bucket );
        }
      }
    }
    return bucketSet;
  }
  
  @Override
  public TableSelect<E> column( int columnIndex )
  {
    this.bucket.addColumnByIndex( columnIndex );
    return this;
  }
  
  @Override
  public TableSelect<E> column( ImmutableColumn<E> column )
  {
    if ( column != null )
    {
      this.column( column.table(), column.index() );
    }
    return this;
  }
  
  @Override
  public TableJoin<E> columns( int columnIndex, int... columnIndexes )
  {
    this.bucket.addColumnByIndex( columnIndex );
    for ( int iColumnIndex : columnIndexes )
    {
      this.bucket.addColumnByIndex( iColumnIndex );
    }
    
    return this;
  }
  
  @Override
  public TableSelect.TableJoin<E> join( ImmutableTable<E> table )
  {
    this.closeAndRolloverBucket( table );
    return this;
  }
  
  private void closeAndRolloverBucket( ImmutableTable<E> table )
  {
    this.closedBucketList.add( this.bucket );
    this.bucket = new Bucket<E>( table );
  }
  
  @Override
  public TableSelect<E> where( TableSelect.Predicate<E> predicate, TableSelect.Predicate<E>... predicates )
  {
    if ( predicate != null )
    {
      this.predicateList.add( predicate );
    }
    for ( Predicate<E> iPredicate : predicates )
    {
      if ( iPredicate != null )
      {
        this.predicateList.add( iPredicate );
      }
    }
    return this;
  }
  
  @Override
  public org.omnaest.utils.table2.TableSelect.TableSelectExecution<E> as()
  {
    this.closeAndRolloverBucket( null );
    return this;
  }
  
  @Override
  public TableJoin<E> onEqual( ImmutableColumn<E> columnLeft, ImmutableColumn<E> columnRight )
  {
    if ( columnLeft != null && columnRight != null )
    {
      ColumnIdentity<E> columnIdentityLeft = columnLeft.id();
      ColumnIdentity<E> columnIdentityRight = columnRight.id();
      
      boolean addedToExistingJoin = false;
      ColumnJoin<E> columnJoinResolved = null;
      List<ColumnJoin<E>> removableColumnJoinList = new ArrayList<ColumnJoin<E>>();
      for ( ColumnJoin<E> columnJoin : this.columnJoinList )
      {
        if ( columnJoin.contains( columnIdentityLeft ) || columnJoin.contains( columnIdentityRight ) )
        {
          if ( !addedToExistingJoin )
          {
            columnJoin.add( columnIdentityRight );
            columnJoin.add( columnIdentityLeft );
            addedToExistingJoin = true;
            columnJoinResolved = columnJoin;
          }
          else
          {
            columnJoinResolved.mergeIntoThis( columnJoin );
            removableColumnJoinList.add( columnJoin );
          }
        }
      }
      if ( !addedToExistingJoin )
      {
        final ColumnJoin<E> columnJoin = new ColumnJoin<E>();
        columnJoin.add( columnIdentityRight );
        columnJoin.add( columnIdentityLeft );
        this.columnJoinList.add( columnJoin );
      }
      this.columnJoinList.removeAll( removableColumnJoinList );
      
    }
    
    return this;
  }
  
  @Override
  public TableJoin<E> on( TableSelect.Predicate<E> predicate )
  {
    if ( predicate != null )
    {
      this.bucket.add( predicate );
    }
    return this;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.table2.TableSelect.TableSelectExecution#table()
   */
  @SuppressWarnings("unchecked")
  @Override
  public Table<E> table()
  {
    E[][] elementMatrix = null;
    
    //
    final Class<E> componentType = ListUtils.firstElement( this.closedBucketList ).getTable().elementType();
    
    //
    Set<ColumnIdentity<E>> selectedColumnIdentitySet = new LinkedHashSet<ColumnIdentity<E>>();
    List<Iterable<? extends FilterRow<E>>> filteredFilterRowIterableList = new ArrayList<Iterable<? extends FilterRow<E>>>();
    {
      final PreparedColumnJoin<E> preparedColumnJoin = new PreparedColumnJoin<E>( this.columnJoinList );
      for ( Bucket<E> bucket : this.closedBucketList )
      {
        final Iterable<FilterRowIdentifiable<E>> filterRowIterable = bucket.newUnfilteredFilterRowIterable();
        final Predicate<E> predicate = preparedColumnJoin.newIntersectionPredicate( bucket.getTable() );
        FilterRowFilterer<E> filterRowFilterer = new FilterRowFilterer<E>( filterRowIterable,
                                                                           ListUtils.add( bucket.getPredicateList(), predicate ) );
        BitSet filterResult = filterRowFilterer.calculateFilterResult();
        
        Iterable<FilterRowIdentifiable<E>> filteredFilterRowIterable = bucket.newFilteredFilterRowIterable( filterResult );
        filteredFilterRowIterableList.add( filteredFilterRowIterable );
        
        //
        selectedColumnIdentitySet.addAll( bucket.getSelectedColumnIdentitySet() );
      }
    }
    
    CrossProductFilterRowGenerator<E> crossProductFilterRowGenerator = new CrossProductFilterRowGenerator<E>(
                                                                                                              filteredFilterRowIterableList );
    
    final List<E[]> elementArrayList = new ArrayList<E[]>();
    int columnSize = 0;
    for ( FilterRow<E> filterRow : crossProductFilterRowGenerator )
    {
      //
      boolean include = true;
      joinPredicateLoop: for ( ColumnJoin<E> columnJoin : this.columnJoinList )
      {
        Set<ColumnIdentity<E>> columnIdentitySet = columnJoin.getColumnIdentitySet();
        if ( columnIdentitySet.size() > 1 )
        {
          //
          final Iterator<ColumnIdentity<E>> iterator = columnIdentitySet.iterator();
          final ColumnIdentity<E> columnIdentityFirst = iterator.next();
          
          final E compareElement = filterRow.getElement( columnIdentityFirst );
          while ( iterator.hasNext() )
          {
            final ColumnIdentity<E> columnIdentity = iterator.next();
            final E element = filterRow.getElement( columnIdentity );
            if ( !ObjectUtils.equals( compareElement, element ) )
            {
              include = false;
              break joinPredicateLoop;
            }
          }
        }
      }
      
      //
      if ( include )
      {
        final List<E> elementList = new ArrayList<E>();
        for ( ColumnIdentity<E> columnIdentity : selectedColumnIdentitySet )
        {
          E element = filterRow.getElement( columnIdentity );
          elementList.add( element );
        }
        elementArrayList.add( elementList.toArray( (E[]) Array.newInstance( componentType, elementList.size() ) ) );
        columnSize = elementArrayList.size();
      }
    }
    
    elementMatrix = elementArrayList.toArray( (E[][]) Array.newInstance( componentType, elementArrayList.size(), columnSize ) );
    final ArrayTable<E> arrayTable = new ArrayTable<E>( elementMatrix );
    {
      final Set<String> tableNameSet = new LinkedHashSet<String>();
      int columnIndex = 0;
      for ( ColumnIdentity<E> columnIdentity : selectedColumnIdentitySet )
      {
        final ImmutableColumn<E> column = columnIdentity.getColumn();
        final String tableName = column.table().getTableName();
        String columnTitle = tableName + "." + column.getTitle();
        arrayTable.setColumnTitle( columnIndex++, columnTitle );
        
        tableNameSet.add( tableName );
      }
      arrayTable.setTableName( CollectionUtils.toString( tableNameSet, new ElementConverterIdentity<String>(), Joiner.on( " " ) ) );
    }
    return arrayTable;
  }
  
  @Override
  public SortedMap<E, Set<Row<E>>> sortedMap()
  {
    // 
    final SortedMap<E, Set<Row<E>>> retmap = new TreeMap<E, Set<Row<E>>>();
    
    Table<E> table = this.table();
    for ( Row<E> row : table.rows() )
    {
      //
      final E key = row.getCellElement( 0 );
      
      Set<Row<E>> set = retmap.get( key );
      if ( set == null )
      {
        set = new LinkedHashSet<Row<E>>();
        retmap.put( key, set );
      }
      set.add( row );
    }
    
    return retmap;
  }
  
}
