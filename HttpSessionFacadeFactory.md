# HttpSessionFacadeFactory #

Allows to create proxies (cglib) for the HttpSession based on arbitrary types. This allows to create an interface which specifies whats stored within the HttpSession and use it to access the HttpSession.

## Performance ##

The performance was measured by simple time capturing on millisecond basis and one hundred invocations.
That result in a massive increase compared to less than 50 invocations.
The used Hot Spot JVM seems to optimize reflection calls only beginning with about 50 or more cycles.

For hundred instantiations of a facade proxy:

![http://utils-apl-derived.googlecode.com/svn-history/wiki/images/HttpSessionFacadeFactoryPerformanceInstantiation.png](http://utils-apl-derived.googlecode.com/svn-history/wiki/images/HttpSessionFacadeFactoryPerformanceInstantiation.png)


For hundred read and write cycles from and to a facade proxy:

![http://utils-apl-derived.googlecode.com/svn-history/wiki/images/HttpSessionFacadeFactoryPerformanceReadAndWrite.png](http://utils-apl-derived.googlecode.com/svn-history/wiki/images/HttpSessionFacadeFactoryPerformanceReadAndWrite.png)