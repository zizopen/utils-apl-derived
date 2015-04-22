# HttpSessionService #

The HttpSessionService allows to manage the HttpSession using a HttpSessionFacade or directly manipulate attributes using a Map view.

```
<context:annotation-config />
<bean class="org.omnaest.utils.spring.session.implementation.HttpSessionAndServletRequestResolverServiceBean" />
<bean class="org.omnaest.utils.spring.session.implementation.HttpSessionServiceBean" />
```