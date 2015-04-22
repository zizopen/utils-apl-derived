# ArrayTable #

## General ##

A Table represents a two dimensional container which allows modification of Rows and Columns.
```

 Table<String> table = new ArrayTable<String>( String.class );
 table.addRowElements( "a", "b", "c", "d" );
 
 String element = table.getElement( 7, 4 );
 
 for ( Map<String, String> map : table.rows( 3, 6 ).to().maps() )
 {
   //...
 }
```

## Index ##

With the index() method the Table allows to create TableIndexs of several forms. Those indexes can be backed by the Table which does hide the complexity of updates within the Table implementation.

```
 TableIndex<String, Cell<String>> tableIndex = table.index().of( table.column( 1 ) );
```

## Select ##

With select() is a simple TableJoin related to SQL possible.
```
 Table<String> result = table.select()
                             .column( 1 )
                             .column( 2 )
                             .join( tableOther )
                             .allColumns()
                             .onEqual( table.column( 1 ), tableOther.column( 0 ) )
                             .as()
                             .table();
```

## Serialization ##

With serializer() it is possible to serialize and deserialzie the Table content into and from several formats like Json, Xml, plain text.
```
 String content = table.serializer().marshal().asJson().toString();
```

## Persistence ##

The Table can be attached and detached to and from a TablePersistence instance.
There are rudimentary file and directory based implementations, which are serializing the objects e.g. as xml or Java object Serializable. (Some formats imply the import of third party dependencies)

Example:
```
 Table<String> table = new ArrayTable<String>( String.class ).persistence().attach().asXML().usingJAXB().toDirectory( directory );
```

## Copy ##

With copy() the table can copy content of other sources, like other Table instances or any other TableDataSource. E.g. a ResultSet can be used as source as well.

## Adapter ##

The as() method provides adapter instances which allow to access the Table e.g. as a List of Java beans.
```
 List<Domain> domainList = table.as().beanList( Domain.class );
 domainList.add( new Domain() );
```