# Capturing method calls #

With the `MethodCallCapturer`you can track method calls on interface or class methods.

E.g. following code will create a stub for the `TestInterface` interface:
```
MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
TestInterface testInterface = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestInterface.class );
```

Once you have created a stub for a given class or interface you can easily track the method names of method calls like:

```
String methodName = methodCallCapturer.methodName.of( testInterface.getTestClass().getFieldString() );
```

which would result in "getTestClass.getFieldString".
The same way you can get the property names with following statement:

```
String propertyName = methodCallCapturer.beanProperty.name.of( testInterface.getTestClass().getFieldString() );
```

which would result in "testClass.fieldString".
An advanced example is to get a BeanPropertyAccessor for a given method call statement:

```
BeanPropertyAccessor<TestInterface> beanPropertyAccessor = methodCallCapturer.beanProperty.accessor.of( testInterface.getTestClass().getFieldString() );
```

A BeanPropertyAccessor lets you access the value and let you copy the property value from one bean object to another.

Or back to method calls, you can even replay the raised method calls on real objects like:
```
ReplayResult replayResult = methodCallCapturer.replay( testInterfaceObject );
```

For more examples see the
  * [MethodCallCapturerTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/proxy/MethodCallCapturerTest.java)
  * [MethodNameTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/proxy/MethodNameTest.java)
  * [BeanPropertyTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/proxy/BeanPropertyTest.java)