# MethodInvocationForwardingCapturer #

The [MethodInvocationForwardingCapturer](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/main/java/org/omnaest/utils/proxy/MethodInvocationForwardingCapturer.java?spec=svn230&r=230) can create cglib proxies which intercept all method calls, forward them to the original underlying object and capture the invocation (method, arguments) as well as the result. All informations of such an invocation will be serialized to XML format using the XStream library.

Since the XStream library is licensed under BSD license you have to include the Maven dependency explictly. The dependeny for Version 1.4.1 looks like:

```
<dependency>
  <groupId>com.thoughtworks.xstream</groupId>
  <artifactId>xstream</artifactId>
  <version>1.4.1</version>
  <type>jar</type>
  <scope>compile</scope>
</dependency>
```

Examples of usage:

```
TestClass object = new TestClass();
OutputStream outputStream = [...]

TestClass proxyInstance = MethodInvocationForwardingCapturer.newProxyInstanceCapturing( object, outputStream );
```

```
MethodInvocationForwardingCapturer.replayOn( preparedCapturingMethodInvocationInputStream, testClass );
```

```
boolean ignoreArgumentValues = false;
TestClass proxyInstanceReplaying = MethodInvocationForwardingCapturer.newProxyInstanceReplaying( TestClass.class,preparedCapturingMethodInvocationInputStream,ignoreArgumentValues );
```

For further examples how to use see the [MethodInvocationForwardingCapturerTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/proxy/MethodInvocationForwardingCapturerTest.java?spec=svn230&r=230)