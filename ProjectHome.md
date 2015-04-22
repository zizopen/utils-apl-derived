# Utils-APL-Derived #
Just a "simple" utils project collecting and extending some further utils projects based all on Apache License 2.0

&lt;wiki:gadget url="http://www.ohloh.net/p/584364/widgets/project\_thin\_badge.xml" height="50" border="0"/&gt;

<a href='http://www.xing.com/profile/Danny_Kunz'><img src='http://www.xing.com/img/buttons/10_en_btn.gif' alt='Danny Kunz' width='85' height='23' /></a>

# Documentation #

Please refer to the wiki pages...

or consider the [core Javadocs](http://utils-apl-derived.googlecode.com/svn/wiki/apidocs/core/index.html) or [table Javadocs](http://utils-apl-derived.googlecode.com/svn/wiki/apidocs/table/index.html)

# Releases #

## Release 0.1.17 ##

2012, 29th July

  * first approach of another table/TableDataCore.java implementation
  * implemented the new ArrayTable core functions including joining
  * improved the ArrayTable adding more functions and making it serializable
  * improved ArrayTable including table meta data and plain text serialization now
  * improved ArrayTable allowing now to be serialized into XML and JSON format
  * improved ArrayTable allowing to remove columns
  * improved ArrayTable joining performance by using indexes on the join columns
  * improved ArrayTable allowing to specify row and column titles now and to use locks during the join operations
  * improved ArrayTable allowing to filter Rows by a BitSet
  * improved ArrayTable supporting detached Rows now, and allowing to copy from a TableDataSource
  * improved ArrayTable supporting a DTO based TableToList adapter
  * improved new ArrayTable to be able to be used as a ResultSet source
  * improved ArrayTable to allow select using IN,LIKE predicates
  * improved ArrayTable performance and added support for bean mapping based on the BeanReplicator
  * improved ArrayTable allowing now to act as List of java beans, as well as build an arbitray index on those
  * improved ArrayTable allowing now to create arbitrary SortedMap index structures based on the table
  * improved ArrayTable allowing to use a directory based persistence
  * improved ArrayTable allowing to attach a TablePersistence
  * improved ArrayTable supports sorting now
  * added first adapter for the ArrayTable
  * added StripeTransformerPluginODocument which creates an ODocument of the OrientDB database
  * added StripeTransformerPlugin extension
  * added Rows interface and StripesTransformer allowing to transform multiple Rows into other types
  * added jaxb based persistence capabilities to ArrayTable
  * added further TableEventHandler methods
  * added xhtml serialization capabilities to and enhanced the xml model of the new ArrayTable
  * added File and URL to Unmarshaller and Marshaller
  * added one more DirectoryBasedObjectStore implementation using XStream
  * fixed bug in TableSelectImpl not regarding the where predicates
  * fixed bug in ArrayTable add methods
  * changed ResourceLoaderTable to use the new ArrayTable

  * added TableDataSourceOrientDBTable allowing to load data from an OrientDB into a Table
  * added TablePersistenceOrientDB allowing to persist Table instances into a OrientDB repository
  * improved TablePersistenceOrientDB

  * added first approach of a second BeanReplicator implementation
  * improved new BeanReplicator
  * replace the old with the new BeanReplicator completely
  * removed old BeanReplicator

  * added SubmitGroupFactory
  * added NodeMap

  * improved MapUtils
  * improved IterableUtils, IteratorUtils, ListUtils allowing to filter by BitSets
  * small improvement to NestedDirectoryToByteArrayContainerListAdapter caching now the list size

  * added FactorySerializable
  * added new MapBuilder and replaced the old one
  * added DirectoryBasedObjectStore
  * added KeyValue type
  * added Tuple2 and Tuple3 which replaces the TupleTwo and TupleThree classes
  * added FactoryParameterized interface
  * added ProxyDispatcherFactory

  * removed the ElementStore since the ArrayTable now offers the same and more possibilities
  * updated dependency to newest Commons-IO version 2.4
  * added Jackson as dependency


```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.17</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.17</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.17</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table-orientdbconnector</artifactId>
  <version>0.1.17</version>
</dependency>


```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-spring|0.1.17|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-core|0.1.17|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table|0.1.17|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table-orientdbconnector|0.1.17|jar)



Java: 1.6+

Optional dependencies: Spring Framework, ASM, StAXON, Jersey, OrientDB, Jackson




## Release 0.1.16 ##

2012, 24th June

  * added PreparedBeanCopier
  * improved PreparedBeanCopier to be thread safe
  * improved PreparedBeanCopier serialization abilities
  * improved BeanPropertyAccessor to be able to handle multithreaded requests for values
  * improved BeanPropertyAccessor to be serializable
  * improved AssertLogger to be serializable
  * improved ArrayTable in Performance
  * improved SetUtils, IterableUtils, MapUtils
  * added ExceptionHandlerSerializable
  * added SetComposite, CollectionComposite, ElementBidirectionalConverter, SetToSetAdapter, CollectionToCollectionAdapter, IteratorToIteratorAdapter
  * added some Serializable ElementConverter types
  * fixed bug in MapUtils.innerJoinMapByKey

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.16</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.16</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.16</version>
</dependency>

```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-spring|0.1.16|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-core|0.1.16|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table|0.1.16|jar)



Java: 1.6+

Optional dependencies: Spring Framework, ASM, StAXON, Jersey



## Release 0.1.15 ##

2012, 28th May

  * updated to guava 12.0

  * changed AutowiredContainer interface
  * changed signature of AssertLogger
  * moved ElementFilter from ListUtils to an own package
  * refactoring due to KeyExtractor
  * renamed Enumeration interface to Name

  * fixed bug within XMLIteratorFactory

  * enhanced TableUnmarshallerCSV and TableMarshallerCSV to support simple quotation rules
  * enhanced ObjectUtils, SetUtils, MapUtils, ArrayUtils, IteratorUtils, ListUtils, CollectionUtils
  * enhanced JAXBXMLHelper, XMLIteratorFactory having higher performance
  * enhanced SourcePropertyAccessorToTypeAdapter, PropertynameMapToTypeAdapter allowing now to create instances in a batch a lot faster
  * enhanced XMLIteratorFactory
  * enhanced DurationCapture

  * added support for JSON to XMLIteratorFactory using StAXON
  * added SetDelta, InputStreamDecorator
  * added ThreadLocalBeanScope
  * added MapJoiner
  * added JAXBName which allows to translate Name instances including enums via JAXB
  * added ElementConverterChain
  * added ThreadLocalCachedIterator
  * added ExtensionPoint and ExtensionPointTemplate
  * added OperationVoid interface

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.15</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.15</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.15</version>
</dependency>

```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-spring|0.1.15|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-core|0.1.15|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table|0.1.15|jar)



Java: 1.6+

Optional dependencies: Spring Framework, ASM, StAXON, Jersey


## Release 0.1.14 ##

2012, 1st May

  * added XMLIteratorFactory
  * added ElementStore
  * added InsertionSortedList
  * added SortedList implementations
  * added Enumeration interface
  * added XMLHelper

  * enhanced ReflectionUtils, ListUtils, SetUtils
  * enhanced JAXBXMLHelper
  * enhanced ForEach with parallel execution
  * enhanced ExceptionHandler
  * enhanded ElementStore
  * enhanced HttpSessionServiceBean

  * fixed some bugs within ListAbstract
  * fixed [Issue 2](https://code.google.com/p/utils-apl-derived/issues/detail?id=2): Jersey rest client prints exceptions instead of rethrowing them

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.14</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.14</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.14</version>
</dependency>

```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-spring|0.1.14|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-core|0.1.14|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table|0.1.14|jar)



Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.13 ##

2012, 11th March

  * added  TreeList
  * added CharacterSequenceTokenizer and derivates
  * added Accessor interface, including AccessorReadable and AccessorWritable
  * added a ChainedIterator and ChainedListIterator implementation
  * added IteratorDecoratorSwitchable and ListIteratorDecoratorSwitchable
  * enhanced ListUtils and IteratorUtils
  * separated IteratorUtils from IterableUtils
  * enhanced MapUtils
  * enhanced tests of BeanReplicator using performance tests, especially of the AdapterDeclarableBindings class

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.13</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.13</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.13</version>
</dependency>

```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-spring|0.1.13|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-core|0.1.13|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table|0.1.13|jar)



Java: 1.6+

Optional dependencies: Spring Framework, ASM



## Release 0.1.12 ##

2012, 30th January

Just a maintenance release, adding a bug fix and minor enhancements


```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.12</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.12</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.12</version>
</dependency>

```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-spring|0.1.12|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-core|0.1.12|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table|0.1.12|jar)



Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.11 ##

2012, 09th January

This release concludes a split of the single utils-apl-derived library into three separated modules:

  * utils-apl-derived-core
  * utils-apl-derived-table
  * utils-apl-derived-spring

(If you include the utils-apl-derived-spring you get the other modules inclusively since this one depends on both others)

Notes:
  * enhanced BeanReplicator
  * removed deprecated BeanUtil and ObjectToStringMap
  * enhanced StatelessValidatorBean
  * changed ElementFilter and introduced ExcludingElementFilter
  * enhanced ArrayUtils, MapUtils, ReflectionUtils, StringUtils
  * added StringReplacementBuilder
  * added FactoryParameterized
  * enhanced TupleTwo, SimpleEntry with invert methods
  * added ElementConverterClassToClassInstanceFactory and ElementConverterClassToInstance
  * added FactoryTypeAware interface and FactoryTypeAwareReflectionBased implementation
  * enhanced ByteArrayContainer
  * added SortedMapDecorator



```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.11</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.11</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.11</version>
</dependency>

```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-spring|0.1.11|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-core|0.1.11|jar)
  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived-table|0.1.11|jar)



Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.10 ##

2011, 26th December

Notes:
  * enhanced DefaultValue allowing specification of ElementConverter
  * enhanced PropertyNameTemplate allowing specification of ElementConverter
  * added ElementConverter based on Codec
  * added ElementConverterRegistration
  * introduced ElementConverterTypeAware
  * added CallableDecorator, RunnableDecorator
  * added RequestContextAwareCallableDecorator, RequestContextAwareRunnableDecorator
  * added SpringConverterToElementConverterAdapter
  * added OperationExceptionHandledResult
  * enhanced OperationBlockingToFastRepeatingExecutions allowing to specify a tolerated number of invocations
  * enhanced ObjectUtils, MapUtils, IterableUtils, SetUtils, ListUtils, ComparatorUtils, ArrayUtils
  * enhanced TreeNavigator

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.10</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.10|jar)

Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.9 ##

2011, 11th December

Notes:
  * added ExceptionHandler and ExceptionHandlerManager
  * enhanced TreeNavigator
  * enhanced Assert and AssertLogger
  * enhanced HttpSessionService and HttpSessionServiceBean
  * changed signature of OperationIntrinsic
  * added Codec
  * added StatelessValidatorBean
  * enhanced ReflectionUtils
  * enhanced MapUtils

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.9</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.9|jar)

Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.8 ##

2011, 27th November

Notes:
  * added Tree, TreeNode, TreeNavigator
  * enhanced AssertLogger
  * enhanced DurationCapture statistic


```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.8</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.8|jar)

Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.7 ##

2011, 20th November

Notes:

  * added OSGI-Bundle generation via Springsource Bundlor Tool
  * added AssertLogger
  * added WeakCachedElement
  * added SoftCachedElement
  * improved CollectionUtils and ListUtils
  * added CaseinsensitiveMapDecorator
  * changed signature of ListUtils
  * changed name from XMLHelper to JAXBXMLHelper
  * Tested several decorators for JAXB compliance
  * enhanced and changed signature of AutowiredContainer
  * added AutowiredContainerDecorator
  * added AutowiredContainerUtils
  * enhanced AssertLogger
  * enhanced HttpSessionService
  * enhanced CaseinsensitiveMapDecorator
  * added LockingCollectionDecorator, LockingSetDecorator, LockingMapDecorator, LockingIteratorDecorator, LockingListIteratorDecorator
  * made CaseinsensitiveMapDecorator thread safe
  * moved ListUtils and SetUtils
  * added OperationComposite
  * enhanced IterableUtils
  * enhanced FutureTaskManager

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.7</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.7|jar)

Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.6 ##

2011, 13th November

Changes:

  * changed AutowiredContainer signature
  * changed DualMap signature
  * adapted LinkedHashDualMap
  * added decorators: ListDecorator, SetDecorator, CollectionDecorator, IteratorDecorator
  * renamed MonoHierarchy to TokenMonoHierarchy
  * added clone support to BeanUtils and BeanToNestedMapConverter
  * added ForEach and enhanced operation types
  * added MapBuilder
  * added HttpSessionService
  * added obligate HttpSessionFacade
  * renamed HttpSessionResolver to HttpSessionAndServletRequestResolverService
  * added new PropertyAccessOption option Capitalize
  * added new methods to ForEach Result
  * enhanced ObjectUtils defaultObject method with a Factory contstruct
  * HttpSessionFacadeFactory and HttpSessionService supports configuration of facades
  * Decorators now support JAXB transformation

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.6</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.6|jar)

Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.5 ##

2011, 06th November

This is a maintenance release related to the
  * PropertyFile
class to support BOMs within UTF-8 encoded files again.

New types or functions:
  * MonoHierarchy
  * updated to Apache commons-io version 2.1

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.5</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.5|jar)

Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.4 ##

2011, 05th November

New types or functions:
  * PatternUtils
  * MethodInvocationForwardingCapturer
  * MapToMapAdapter
  * RestClientFactory (including a Jersey based example implementation)
  * CachedElementTimed
  * OperationBattery
  * PropertynameMapToTypeAdapter now supports Converter and PropertyNameTemplate Annotations

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.4</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.4|jar)

Java: 1.6+

Optional dependencies: Spring Framework, ASM


## Release 0.1.3 ##

2011, 14th October

This is a maintenance release related to the
  * PropertyFile
class to support Linux line separators and not failing for "!" characters

Additional new classes:
  * PropertyAccessorToTypeAdapter

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.3</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.3|jar)

Java: 1.6+

## Release 0.1.2 ##

2011, 12th October

The release 0.1.2 is now available and can be retrieved from Maven Repo1.

  * Update to newest guava library v10
  * Added Spring library and some custom scopes like LocaleBeanScope
  * Added a HttpSessionFacadeFactory which produces proxies for a HttpSession
  * Added a BeanToNestedMapConverter which will convert Java Beans in nested Maps with primitives
  * And many more enhancements in many other classes

Codequality:
[Coverage report](http://utils-apl-derived.googlecode.com/svn/wiki/other/utils-apl-derived-0.1.2.html)

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.2</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.2|jar)

Java: 1.6+

## Release 0.1.1 ##

The release 0.1.1 is now available and can be retrieved from Maven Repo1.

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.1</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.1|jar)

(The asm library is now excluded by default)

Java: 1.6+

## Release 0.1.0 ##

The release 0.1.0 is now available and can be retrieved from Maven Repo1.

```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived</artifactId>
  <version>0.1.0</version>
</dependency>
```

  * [Maven Central](http://search.maven.org/#artifactdetails|org.omnaest.utils|utils-apl-derived|0.1.0|jar)

Java: 1.6+

Please exlude the asm library from the dependency since the asm library is licensed under a **BSD license**.

```
<exclusions>
  <exclusion>
    <artifactId>asm</artifactId>
    <groupId>asm</groupId>
  </exclusion>
</exclusions>
```

# SNAPSHOTS #

Actual project snapshots are available as Maven 2 Snapshots like the following


```
<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-spring</artifactId>
  <version>0.1.18-SNAPSHOT</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-core</artifactId>
  <version>0.1.18-SNAPSHOT</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table</artifactId>
  <version>0.1.18-SNAPSHOT</version>
</dependency>

<dependency>
  <groupId>org.omnaest.utils</groupId>
  <artifactId>utils-apl-derived-table-orientdbconnector</artifactId>
  <version>0.1.18-SNAPSHOT</version>
</dependency>

```


and are hosted at the following Sonatype repository:

```
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <name>Sonatype Nexus Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

