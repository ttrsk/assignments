# Price update calculator - a W-company assignment

## TODO
* Add aggregations from H2 database + caching.
* Command-line parsing.
* Update documentation.

## Building

TBD

## Running

### Directly

### Via mvnw (Maven wrappers)

## Design and implementation details

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
* Price modification is relatively expensive operation as it might query a database, so processing
  should be done in following order: filter data, adjust prices, update aggregates.
* Our custom aggregation code should be thread-safe for aggregation to allow parallel processing.
* Application should provide way and time to access in-memory H2 database from external programs.

### Assumptions

1. This should be a console application with logging disabled by default. There should be option to
   enable logging for debugging though.
1. Spring is a preferred implementation technology as it is listed in the technology requirements
   for the position and saves some of the implementation time.
1. We consider instrument price to be double.
   Rationale:
   * Numbers have up to 9 decimal digits, this is more than fixed point price usually has.
   * Multiplier is also double.
1. Aggregate output values are limited to 10 decimal points (maximum precision in the sample input
   file), this allows cleaner output.
1. Requirements say 'Please assume that the values in the INSTRUMENT_PRICE_MODIFIER table can change
   frequently ... but not more often than once every 5 seconds'. I assume that results can be cached
   for 5 seconds.
   Rationale: although text does not say exactly that there will be certain latency for
1. It is ok to fail fast on incorrectly formatted data printing error message.

