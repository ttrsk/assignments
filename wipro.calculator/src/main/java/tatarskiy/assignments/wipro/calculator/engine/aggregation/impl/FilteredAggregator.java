package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregate;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

// Provides aggregator that filters source events per some criteria (Predicate) and delegates
// actual aggregation to downstream. As final aggregation step might involve extra computation
// in downstream aggregation result is cached.

public class FilteredAggregator implements PriceAggregate {

  private final Predicate<InstrumentPriceUpdate> filter;

  private final PriceAggregate downstream;

  private AtomicReference<OptionalDouble> aggregateValueCache = new AtomicReference(null);

  public FilteredAggregator(Predicate<InstrumentPriceUpdate> filter, PriceAggregate downstream) {
    Objects.requireNonNull(filter, "Filter cannot be null");
    Objects.requireNonNull(downstream, "Downstream aggregator cannot be null");
    this.filter = filter;
    this.downstream = downstream;
  }

  @Override
  public OptionalDouble getAggregateValue() {
    OptionalDouble result = aggregateValueCache.get();
    if (result == null) {
      result = downstream.getAggregateValue();
      aggregateValueCache.set(result);
    }
    return result;
  }

  private void invalidateResultCache() {
    if (aggregateValueCache.get() != null) {
      aggregateValueCache.set(null);
    }
  }

  @Override
  public void accept(InstrumentPriceUpdate instrumentPriceUpdate) {
    if (filter.test(instrumentPriceUpdate)) {
      downstream.accept(instrumentPriceUpdate);
      invalidateResultCache();
    }
  }
}
