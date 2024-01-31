Price update calculator - a Wipro assignment
============================================

Building
--------
TBD

Runnning
--------
TBD

Design and implementation details
---------------------------------

### General design considerations

* Application should read input data and compute per-instrument aggregates with optional data
  filtering and applying multipliers from the database.
* This is a classic stream processing task where stream content is split (aka grouped by) instrument
  name and then
  aggregated.
* Unlike of classic Stream<T> collectors collection rules are context-dependent (computation rules
  depend on
  instrument name), so we will not try to implement collectors, just feed stream data to aggregating
  consumer in the
  forEach method.
* Our custom aggregation code should be thread-safe for aggregation to allow parallel processing.
* Application should provide way and time to access in-memory H2 database from external programs

### Assumptions
TBD

