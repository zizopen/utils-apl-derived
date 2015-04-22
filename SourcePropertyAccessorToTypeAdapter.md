# SourcePropertyAccessorToTypeAdapter #

The SourcePropertyAccessorToTypeAdapter allows to create arbitrary proxies (cglib) which dispatches access calls to the properties of the given type to a simple SourcePropertyAccessor interface instance. Such an instance has a getValue(propertyName) and setValue(propertyName,value) method which is quite similar to an Map<String,Object> access.

Example:

```
TestType         testType         = SourcePropertyAccessorToTypeAdapter.newInstance( TestType.class, this.propertyAccessor );
```


SourcePropertyAccessor:
```
/**
   * Simple {@link SourcePropertyAccessor} interface which reduces to a {@link #setValue(Object, Object)} and {@link #getValue(Object)}
   * method signature.
   * 
   * @author Omnaest
   */
  public static interface SourcePropertyAccessor
  {
    /**
     * Sets the given value for the given property name.
     * 
     * @param propertyName
     * @param value
     * @param propertyMetaInformation
     */
    public void setValue( String propertyName, Object value,  PropertyMetaInformation propertyMetaInformation  );
    
    /**
     * Returns the value related to the given property name.
     * 
     * @param propertyName
     * @param returnType
     * @param propertyMetaInformation     * @return
     */
    public Object getValue(  String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation );
  }
```