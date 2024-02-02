package tatarskiy.assignments.wipro.calculator.engine.aggregation;

/*
  Chooses price aggregation rules depending on instrument.
  implementation must return a new aggregator instance for each request.
 */
@FunctionalInterface
public interface PriceAggregatorFactory {

  PriceAggregator create(String instrument);
}
