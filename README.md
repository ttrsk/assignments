# Price aggregation calculator - a W-company assignment

## Building

In order to build calculator you should have JDK 17+ installed and  `JAVA_HOME` environment variable
should point to that JDK.

Project uses Maven Wrapper.

To create executable jar run `./mvnw package` command.

## Running

In order to run calculator you should have Java Runtime Environment 17+ installed and `PATH`
environment variable should point to `java` executable.

### Running Jar Directly

After building executable jar you can launch it using following command:

```commandline
    java -jar target/wipro.calculator-0.1.0-exec.jar --input=<input_file_path> [<optional_args>]
```

Where arguments are following:

* **--input=<input_path>** - path to input file. Required parameter.
* **--calculator.silent=true** - suppress user messages. Useful for batch files.
* **--calculator.parallel=false** - Disable parallel data processing.
* **--pause=<N>** - Insert pause of N seconds before calculation. Useful to attach to price modifier
  database.
* **--calculator.logging.enabled=true** - Enable Spring logging. Useful for debugging.

### Via mvnw (Maven wrapper)

You can also use mvnw to run application. To do that use `mvnw spring-boot:run` command
with `-Dspring-boot.run.arguments` parameter set to a comma-separated list of all calculator
arguments.

```
mvnw spring-boot:run -Dspring-boot.run.arguments=<comma_separated_arg_list>
```

**NOTE:** You will need to meet build prerequisites to use Maven Wrapper (JDK installed
and `JAVA_HOME`
set)

### Modifying prices

To allow editing `INSTRUMENT_PRICE_MODIFIER` table calculator application exposes H2 database as
a tcp server running at `jdbc:h2:tcp://localhost:9090/mem:multipliers`. Username is `sa` and
password is `password`.

To save some time connecting to the TCP server use `--pause` parameter to wait before computation
starts.

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

## Benchmarks

Parallel run on 4.2GB file (118M lines, 8000 instruments) on Intel 1260P processor over SSD disk
with Java heap limited to 2GiB takes 35-55 seconds. 