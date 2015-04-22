# AutowiredContainer #

An [AutowiredContainer](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/main/java/org/omnaest/utils/beans/autowired/AutowiredContainer.java) allows to store and resolve arbitrary objects based on their implemented types. This makes it to an top level container which operates on type aspects.

Example:

```
public interface ExampleInterface
{
}
 
public class Example extends ExampleInterface
{
}
 
{
   Example example = new Example();
   autowiredContainer.put( example );
   
   assertEquals( 1, autowiredContainer.getValueSet( Example.class ).size() );
   assertEquals( example, autowiredContainer.getValue( Example.class ) );
   
   assertEquals( 1, autowiredContainer.getValueSet( ExampleInterface.class ).size() );
   assertEquals( example, autowiredContainer.getValue( ExampleInterface.class ) );
}
```