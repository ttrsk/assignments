package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregator;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

// Provides aggregator that filters source events per some criteria (Predicate),
// optionally transforms data and delegates actual aggregation to downstream.
// As final aggregation step might involve extra computation in downstream, aggregation
// result is cached.
public class TransformingAggregator implements PriceAggregator {

  private final Predicate<InstrumentPriceUpdate> filter;

  private final PriceAggregator downstream;
  private final UnaryOperator<InstrumentPriceUpdate> transform;

  private AtomicReference<OptionalDouble> aggregateValueCache = new AtomicReference(null);

  protected TransformingAggregator(PriceAggregator downstream,
      Predicate<InstrumentPriceUpdate> filter) {
    this(UnaryOperator.identity(), downstream, filter);
    Objects.requireNonNull(filter, "filter cannot be null");
  }

  public TransformingAggregator(UnaryOperator<InstrumentPriceUpdate> transform,
      PriceAggregator downstream) {
    this(transform, downstream, null);
  }


  public TransformingAggregator(UnaryOperator<InstrumentPriceUpdate> transform,
      PriceAggregator downstream, Predicate<InstrumentPriceUpdate> filter) {
    Objects.requireNonNull(transform, "transform cannot be null");
    Objects.requireNonNull(downstream, "downstream cannot be null");
    this.filter = filter;
    this.transform = transform;
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
    if (filter == null || filter.test(instrumentPriceUpdate)) {
      downstream.accept(
          transform == null ? instrumentPriceUpdate : transform.apply(instrumentPriceUpdate));
      invalidateResultCache();
    }
  }
}
