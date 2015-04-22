# JAXB collection wrapper and delegates #

The JAXB is the currently most advanced XML mapping directly available within the JDK.

It allows to convert XML to an object graph and vice versa.

Unfortunately to the current point of time it does not allow to wrap collections, e.g. maps as root objects.

The wrapper classes like the [JAXBMap](http://www.google.com/codesearch#agM0M4Gok3c/trunk/utils-apl-derived/src/main/java/org/omnaest/utils/xml/JAXBMap.java&q=jaxbmap&type=cs) are delegates which wrap e.g. a map and implement the map interface at the same time. So the wrapper can be used as map implementation on the one hand and on the other hand can take another map implementation as underlying data structure.

See the [JAXBMapTest](http://www.google.com/codesearch#agM0M4Gok3c/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/xml/JAXBMapTest.java&q=jaxbmaptest&type=cs&l=26) and the package around to find some helpful wrapper implementations and use cases.