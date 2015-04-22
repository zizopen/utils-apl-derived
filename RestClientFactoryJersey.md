# RestClientFactoryJersey #

The RestClientFactoryJersey is a derivate of the RestClientFactory and allows to produce cglib proxies which handle the REST communication internally.

This has two major advantages:
  * It simplyfies the use of REST services
  * It allows a single source of truth by using shared interfaces

The simplification comes from the fact, that it is not longer necessary to build a resource path manually. Instead the proxy will handles this.

Even subresources within a REST annotated interface are located and will return nested proxies.

The single source of truth is the most important aspect. Since server and client can share an interface which is annotated, a change on the server API will be determined with compiler errors on the client. This avoids unspotted broken REST interfaces.

An example:
```

RestClientFactoryJersey restClientFactoryJersey = new RestClientFactoryJersey( URL_BASE );
    
Resource resource = restClientFactoryJersey.newRestClient( ResourceParam.class );

```

## Limitation ##

Currently the client only supports
  * QueryParam
  * PathParam

  * Path
  * Consumes
  * Produces

  * GET
  * POST
  * PUT
  * DELETE

## Monitor example for XML ##

![http://utils-apl-derived.googlecode.com/svn-history/r257/wiki/images/RESTCommunicationMonitored.jpg](http://utils-apl-derived.googlecode.com/svn-history/r257/wiki/images/RESTCommunicationMonitored.jpg)

## Links ##

See the sister project [restful-webservice-demo-project](http://code.google.com/a/eclipselabs.org/p/restful-webservice-demo-project/)