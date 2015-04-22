# StatelessValidatorBean #

The StatelessValidatorBean validates any existing bean within the same application context to be stateless if the bean is annotated with the StatelessValidation annotation.

Stateless means not to have any field except for final static fields.

Configuration:

```
<context:annotation-config />
<bean class="org.omnaest.utils.spring.stateless.StatelessValidatorBean" />
```