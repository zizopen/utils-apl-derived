# PropertyFile #

Since many configurations and internationalizations are realized by using Java property files, it is necessary from time to time to read such properties and write them back.

While the Java API allows you to read properties easily a full roundtrip parser and writer is not available.

The Apache Commons project did an approach on that but the implementation lacks still for having a full roundtrip.

Within this utils project a [PropertyFile](http://www.google.com/codesearch#agM0M4Gok3c/trunk/utils-apl-derived/src/main/java/org/omnaest/utils/propertyfile/PropertyFile.java&q=propertyfile%20package:http://utils-apl-derived%5C.googlecode%5C.com&type=cs&exact_package=http://utils-apl-derived.googlecode.com/svn) class is available which does exactly that job.

It allows to load and store property files and manipulate their content. But it is not designed to read replacement patterns and do a replacement process on them.