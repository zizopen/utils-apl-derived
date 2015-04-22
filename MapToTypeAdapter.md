# Mapping of Java Bean interfaces or classes to Map instances #

The `PropertynameMapToTypeAdapter<T, M extends Map<String, ?>>` class allows to map arbitrary types to an underlying `Map<String,?>` instance.

This is done by following code:
```
Map<String, Object> map = new HashMap<String, Object>();
TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class );
```

every call to the `TestType` methods like
```
testType.setFieldString( "New String value" );
testType.setFieldDouble( 11.0 );
```
will change the values of the underlying map for the respective property names as keys:
```
assertEquals( "New String value", map.get( "fieldString" ) );
assertEquals( 11.0, (Double) map.get( "fieldDouble" ), 0.01 );
```

For more see [PropertynameMapToTypeAdapterTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/beans/PropertynameMapToTypeAdapterTest.java)