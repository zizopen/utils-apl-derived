# PropertynameMapToTypeAdapter #

The PropertynameMapToTypeAdapter is an adapter which allows to use a Map<String,Object> with the property names of a type as an instance of a given type.

For example the following interface could be used to write and read
  * fieldDoubleRenamed
  * fieldString
keys into/from a Map with the above names as keys and the value of the setter and the return value of the getter as map value:

```
Map<String, Object> map = new HashMap<String, Object>();  
ExampleType exampleType = PropertynameMapToTypeAdapter.newInstance( map, ExampleType.class );
 }
```
```

 protected static interface ExampleType
 {
   @PropertyNameTemplate("fieldDoubleRenamed")
   public Double getFieldDouble();
   
   public void setFieldDouble( Double fieldDouble );
   
   @Converter(type = ElementConverterIntegerToString.class)
   public String getFieldString();
   
   @Converter(type = ElementConverterStringToInteger.class)
   public void setFieldString( String fieldString );   
 }
```