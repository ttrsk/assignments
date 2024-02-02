package tatarskiy.assignments.wipro.calculator.engine.impl;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import tatarskiy.assignments.wipro.calculator.engine.CalculatorEngine;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregatorFactory;
import tatarskiy.assignments.wipro.calculator.model.AggregationResult;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

/*
 * Implementation of CalculatorEngine:
 *   1. Iterates through input data
 *   2. Applies common filter, i.e. can filter out updates on weekends, etc.
 *   3. Sends matching data to results accumulator that aggregates the result per instrument.
 *      Actual aggregators are provided by PriceAggregatorFactory.
 */
public class CalculatorEngineImpl implements CalculatorEngine {

  private final PriceAggregatorFactory factory;
  private final Predicate<InstrumentPriceUpdate> commonFilter;

  public CalculatorEngineImpl(PriceAggregatorFactory factory, Predicate commonFilter) {
    Objects.requireNonNull(factory);
    Objects.requireNonNull(commonFilter);
    this.factory = factory;
    this.commonFilter = commonFilter;
  }

  @Override
  public List<AggregationResult> calculate(Stream<InstrumentPriceUpdate> updateStream) {
    CalculationState aggregateCollector = new CalculationState(factory);
    updateStream.filter(commonFilter).forEach(aggregateCollector::accept);
    return aggregateCollector.makeResultsList();
  }

}
