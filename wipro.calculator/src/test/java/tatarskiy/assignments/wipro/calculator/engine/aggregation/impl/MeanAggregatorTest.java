package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.aggregateAndGet;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.sampleUpdates;

import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class MeanAggregatorTest {

  private MeanAggregator aggregator = new MeanAggregator();

  @Test
  void Should_ReturnEmptyValue_When_InputIsEmpty() {
    assertEquals(OptionalDouble.empty(), aggregator.getAggregateValue());
  }

  @Test
  void Should_CalculateMeanPriceValue_When_DataIsProvided() {
    final double[] priceUpdates = new double[]{1, 1, 2, 2};
    final OptionalDouble average = OptionalDouble.of(1.5);
    OptionalDouble aggregateValue = aggregateAndGet(aggregator, sampleUpdates(priceUpdates));
    assertEquals(average, aggregateValue);
  }

}