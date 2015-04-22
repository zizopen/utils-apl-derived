# AssertLogger #

The AssertLogger holds a reference on a SLF4 Logger instance and allows to log typical assert method calls directly and avoids the need of exception handling.

This is useful if an application should behave stable and exception prone, but still should log errors.

A nice example is the following:

```

 AssertLogger assertLogger = new AssertLogger( this.logger );

 assertLogger.debug.assertThis.isTrue( expression );
 assertLogger.warn.assertThis.isNotNull( object, "Additional message" );
 assertLogger.info.assertThis.fails( "Additional message" );

```

As you see, no need for a try{...}catch(...){...} and still a hint within the logs that something went wrong even in production scenarios.