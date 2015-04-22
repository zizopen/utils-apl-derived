# BeanToNestedMapConverter #

Did you every had the need to reduce a given Java object graph consisting of several classes down to use only primitives or classes which are part of the JDK?
E.g. when using OSGi and you have to connect to another container which was not managed by the OSGi container? (Eclipse Equinox Servlet Bridge)

The BeanToNestedMapConverter gives a simple solution by transforming a given JavaBean into a Map which contains the property name as string and a primitve as value.

See [BeanToNestedMapConverterTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/beans/mapconverter/BeanToNestedMapConverterTest.java?spec=svn179&r=179) for a simple example.