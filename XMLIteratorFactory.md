# XMLIteratorFactory #

> org.omnaest.utils.xml.XMLIteratorFactory


The XMLIteratorFactory is a wrapper around StAX and JAXB which allows to split a given xml InputStream content into Object, Map or String content chunks.



Example:

Code using the XMLIteratorFactory to create an Iterator instance for all book elements:
```
 Iterator<Book> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames().newIterator( Book.class );
```


XML snippet:
```
  <Books>
     <Book>
         <Title>Simple title</Title>
         <author>an author</author>
     </Book>
     <Book>
         <Title>Second simple title</Title>
         <Author>Second author</Author>
     </Book>
  </Books>
```

JAXB annotated class:
```
 @XmlRootElement(name = "book")
 @XmlType(name = "book")
 @XmlAccessorType(XmlAccessType.FIELD)
 protected static class Book
 {
   @XmlElement(name = "title")
   private String title;
   
   @XmlElement(name = "author")
   private String author;
 }
```

There are several Iterator types offered:

String based: newIterator(QName)
Map based: newIteratorMapBased(QName)
Class type based: newIterator(Class)
Those types are faster in traversal of the original stream from top to bottom, whereby the slower ones can get some performance improvement by using parallel processing. The Iterator instances are thread safe by default and the Iterator.next() function can be called until an NoSuchElementException is thrown.
In normal circumstances an Iterator is not usable in multithreaded environments, since Iterator.hasNext() and Iterator.next() produce imminent gaps within the Lock of an element. This gap can be circumvented by calling
doCreateThreadsafeIterators(boolean)
which will force Iterator instances to use ThreadLocals internally. Otherwise do not use the Iterator.hasNext() method, since any other Thread can clear the Iterator before the call to Iterator.next() occurs.

The XMLIteratorFactory allows to modify the underlying event stream using e.g.:

doLowerCaseXMLTagAndAttributeNames()
doUpperCaseXMLTagAndAttributeNames()
doRemoveNamespacesForXMLTagAndAttributeNames()
doAddXMLEventTransformer(XMLEventTransformer)


If the XMLIteratorFactory should only operate on a subset of xml tags within a larger stream the concept of sopes is available, which can be instrumented by calling doAddXMLTagScope(QName).
If no scope's start tag is passed no reading of events will occur and the reading into a single Iterator will stop immediately when an end tag of a scope is matched.