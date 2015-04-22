# OperationBattery #

The OperationBattery is a threadsafe dispatcher implementation for Operation instances.

It allows to scale out a not thread safe implemented operation by using multiple not thread safe instances and decorate them with ReentrantLock instances.

Of course it is also possible to use already thread safe operation which simply perform bad when invoked multiple times within the same time period. E.g. caused by the use of synchronize or another bad choice of synchronization algorithm.