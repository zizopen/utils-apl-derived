# LocaleBeanScope #

The [LocaleBeanScope](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/main/java/org/omnaest/utils/spring/scope/LocaleBeanScope.java) is a Spring custom bean scope which can be configured by the CustomScopeConfigurer.

It will resolve a Locale using a LocaleResolver for every thread.

With this locale it will try to resolve a bean instance related to this locale.
The resolving process will look within the following places and in the following order for bean instances:
  * Internal cache of the scope
  * Application context: predeclared beans with the locale pattern as trailing identifier added to the name of the bean. E.g. localeBean\_en\_US
  * Otherwise create a new single instance related to this Locale via the BeanFactory of the application context

This behvior allows several things:
  * Dynamically extend the set of bean instances during runtime simply by creating threads with new locales
  * Manually declare beans which are used for special locales

Threads with the same locale will use the same Spring bean if it is declared as locale scoped.

If the Runnable or Callable decorators from the LocaleBeanScope are used, the locale is cleared for the thread after the execution ends. This allows to use ThreadPools without conflicts.


An example of the behavior can be found within  [LocaleBeanScopeTest](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/java/org/omnaest/utils/spring/scope/LocaleBeanScopeTest.java) and the related application context at [LocaleBeanScopeTestAC](http://code.google.com/p/utils-apl-derived/source/browse/trunk/utils-apl-derived/src/test/resources/org/omnaest/utils/spring/scope/LocaleBeanScopeTestAC.xml)

## Overview ##

![http://utils-apl-derived.googlecode.com/svn-history/wiki/images/LocaleBeanScope.jpg](http://utils-apl-derived.googlecode.com/svn-history/wiki/images/LocaleBeanScope.jpg)