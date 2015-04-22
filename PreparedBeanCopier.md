# PreparedBeanCopier #

The PreparedBeanCopier allows to prepare Java Reflection based deep copy operations in so far, that any copy operation
will internally only use two Method.invoke(...) calls to copy values of properties.

Additionally the PreparedBeanCopier allows to map different types to each other, so that e.g. a Domain class can be mapped to its DomainDTO related type.

Code example:
```
PreparedBeanCopier<BeanFrom, BeanTo> preparedBeanCopier   = new PreparedBeanCopier<BeanFrom, BeanTo>(
	BeanFrom.class,
	BeanTo.class,
	new Configuration().addTypeToTypeMapping( BeanFrom.class,
                                                  BeanTo.class )
);
BeanTo clone1 = preparedBeanCopier.deepCloneProperties( beanFrom );
BeanTo clone2 = preparedBeanCopier.deepCloneProperties( beanFrom );
...
BeanTo cloneN = preparedBeanCopier.deepCloneProperties( beanFrom );

```

The preparedBeanCopier is **thread safe** by default, so its instance can be shared between multiple threads.

## Performance (Microbenchmark) ##

The PreparedBeanCopier is time costly once when created but very fast in creating clone instances.

It is about **5-10 times** slower than simple **nested getter and setter** calls, but can handle still **about 1000000** clone operations within **10 seconds**.
This makes it about **5-10 times faster** than its Apache Commons counterpart, the [BeanUtils.copyProperties(...)](http://commons.apache.org/beanutils/api/index.html|).

![http://utils-apl-derived.googlecode.com/svn-history/wiki/images/PreparedBeanCopierPerformance.jpg](http://utils-apl-derived.googlecode.com/svn-history/wiki/images/PreparedBeanCopierPerformance.jpg)