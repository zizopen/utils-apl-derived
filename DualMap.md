# DualMap #

A DualMap is a Map derivate interface which forces implementations to create indexes for keys and values.

It is very similar to the BiMap of Googles guava library or the Apache Commons lib, but differs from them by forcing subtypes to store key to value and value to key maps independly. This does remove the restriction to have one to one relationships between keys and values.