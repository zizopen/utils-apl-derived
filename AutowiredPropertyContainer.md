# AutowiredPropertyContainer #

The AutowiredPropertyContainer allows to put the property values of a Java Bean by their class or interface types directly into the bean object without specifying their target propety.

On the other hand any property value can be resolved just simply by its property type.

An example of code would look like:

```
MockClass mockClass = new MockClass();
AutowiredPropertyContainer autowiredPropertyContainer = TypeToAutowiredPropertyContainerAdapter.newInstance( mockClass );
    
String value = autowiredPropertyContainer.getValue( String.class );
```

or

```
for ( Object object : autowiredPropertyContainer )
{
  if (object instanceof String)
  {
    String value = (String) object;
  }
}
```

For more see:
  * [TypeToAutowiredPropertyContainerAdapterTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/beans/TypeToAutowiredPropertyContainerAdapterTest.java)