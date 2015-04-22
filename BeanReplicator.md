# BeanReplicator #

> org.omnaest.utils.beans.replicator.BeanReplicator<FROM, TO>



## General ##

A BeanReplicator allows to copy(Object, Object) or clone(Object) instances from one type to another type.

By default the BeanReplicator maps similar named and typed properties to each other. The declare(Declaration) method allows to specify in more detail how the replication of a target instance takes place.


## Mappings ##

Mappings can be done from
Java bean to Java bean
Java bean to Map<String,Object>
Map<String,Object> to Java bean
Map to Map / SortedMap
List to List / Set / Collection / Array
Set to List / Set / Collection / Array
Collection to List / Set / Collection / Array
Iterable to List / Set / Collection / Array
Array to List / Set / Collection / Array
Some primitive type mappings like String to Long conversions are done automatically.


## Proxy generation ##

If a target type is an interface, the BeanReplicator will try to generate an proxy implementation on the fly which will act like a normal bean. Of course such conversions are much slower, than normal bean to bean copy actions.


## Exception handling ##

If one ore more properties can not be matched, the BeanReplicator throws internally NoMatchingPropertiesExceptions which are given to any ExceptionHandler set with setExceptionHandler(ExceptionHandler).
Unexpected exceptions will be catched and wrapped into a CopyException and given to the set ExceptionHandler. The BeanReplicator methods itself will never throw any Exception.


## Thread safety ##

The BeanReplicator instance is thread safe in its copy(Object, Object) and clone(Object) methods. The declare(Declaration) method is NOT thread safe and the modification of any Declaration should be strongly avoided during any copy(Object, Object) or clone(Object) invocation.

## Performance ##

Even with its very strong mapping capabilities the BeanReplicator is still about 2-5 times faster than the Apache Commons BeanUtils.copyProperties(Object, Object)

For about 1000000 clone invocations it needs about 5-10 seconds. Based on simple beans with a hand full of primitive properties.

The direct getter setter invocation for the same amount of beans takes about 100 ms!!

## Code examples ##

Copy example:
```
   BeanReplicator<BeanFrom, BeanTo> beanReplicator = new BeanReplicator<BeanFrom, BeanTo>( BeanFrom.class, BeanTo.class );
   final BeanFrom simpleBean = new BeanFrom();
   final TestSimpleBeanTo clone = new TestSimpleBeanTo();
   beanReplicator.copy( simpleBean, clone );
```

Clone example:
```
   BeanReplicator<BeanFrom, BeanTo> beanReplicator = new BeanReplicator<BeanFrom, BeanTo>( BeanFrom.class, BeanTo.class );
   final BeanFrom simpleBean = new BeanFrom();
   final BeanTo clone = beanReplicator.clone( simpleBean );
```

Remapping example:
```
 Source bean:  
 |--Bean0From bean0From
    |--Bean1From bean1From
       |--Bean2From bean2From
          |--String fieldForLong
          |--String fieldString1
          |--String fieldString2
 
  Target bean: 
  |-- Bean0To bean0To
    |--Bean1To bean1To
       |--Bean2To bean2To
          |--long fieldLong
          |--String fieldString1
          |--String fieldString2
```
```
 beanReplicator.declare( new Declaration()
 {
   @Override
   public void declare( DeclarationSupport support )
   {
     support.addTypeMapping( Bean1From.class, Bean1To.class );
     support.addPropertyNameMapping( "bean1From", "bean1To" );
     
     support.addTypeMappingForPath( "bean1From", Bean2From.class, Bean2To.class );
     support.addPropertyNameMapping( "bean1From", "bean2From", "bean2To" );
     
     support.addTypeAndPropertyNameMapping( "bean1From.bean2From", String.class, "fieldForLong", Long.class, "fieldLong" );
   }
 } );
 
```