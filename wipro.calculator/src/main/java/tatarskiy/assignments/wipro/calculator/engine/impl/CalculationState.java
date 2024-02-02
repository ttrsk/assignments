package tatarskiy.assignments.wipro.calculator.engine.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregator;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregatorFactory;
import tatarskiy.assignments.wipro.calculator.model.AggregationResult;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

/*
 Processes individual price updates and accumulates calculation engine state,
 i.e. aggregated values grouped by instrument.

 Actual per-instrument aggregation rules are provided by PriceAggregatorFactory.

 This implementation is thread-safe for update processing to allow parallel stream
 aggregation and is not thread-safe for retrieving aggregated value.

 */
class CalculationState implements Consumer<InstrumentPriceUpdate> {

  // Used to resolve instruments to aggregation rules
  private final PriceAggregatorFactory priceAggregatorFactory;
  private Map<String, PriceAggregator> instrumentAggregates = new ConcurrentHashMap<>();

  public CalculationState(PriceAggregatorFactory priceAggregatorFactory) {
    this.priceAggregatorFactory = priceAggregatorFactory;
  }

  // Not thread-safe, creates new result every time.
  public List<AggregationResult> makeResultsList() {
    List<AggregationResult> resultList = new ArrayList<>(instrumentAggregates.size());
    instrumentAggregates.forEach((k, v) -> {
      resultList.add(new AggregationResult(k, v.getAggregateValue()));
    });
    resultList.sort(null);
    return resultList;
  }

  // Thread-safe for parallel result aggregation
  @Override
  public void accept(InstrumentPriceUpdate instrumentPriceUpdate) {
    var aggregate = instrumentAggregates.computeIfAbsent(instrumentPriceUpdate.instrument(),
        k -> priceAggregatorFactory.create(k));
    aggregate.accept(instrumentPriceUpdate);
  }
}
