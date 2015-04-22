# MapBuilder #

> org.omnaest.utils.structure.map.MapBuilder


Builder for Map instances filled with keys and values.

The MapBuilder is not thread safe, since it uses an temporary not thread safe internal Map.
The order of elements is respected when put into the result Map, so a Map implementation which supports ordering will contain the Entrys in the right order afterwards.

Example:
```
 Map<String, String> map = MapUtils.builder()
                                   .put( "key1", "value1" )
                                   .put( "key2", "value2" )
                                   .put( "key3", "value3" )
                                   .buildAs()
                                   .linkedHashMap();
 
```