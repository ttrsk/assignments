package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.SAMPLE_DATE;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.TEST_INSTRUMENT;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.aggregateAndGet;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.sampleUpdates;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregate;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;


class FilteredAggregatorTest {

  private PriceAggregate downstreamMock = Mockito.mock(PriceAggregate.class);
  @Test
  void Should_PassDataToDownstream_When_MatchingDataIsProvided() {
    final double[] priceUpdates = new double[]{1, 1, 3, 4, 3, 5};
    final double[] sortedPricesValuesOverTwo = new double[]{3.0, 4.0, 5.0, 3.0};
    Arrays.sort(sortedPricesValuesOverTwo);
    Stream<InstrumentPriceUpdate> sampleData = sampleUpdates(priceUpdates);
    ArgumentCaptor<InstrumentPriceUpdate> updatesCaptor = ArgumentCaptor.forClass(
        InstrumentPriceUpdate.class);
    FilteredAggregator filteredAggregator = new FilteredAggregator(
        update -> update.price() > 2.0, downstreamMock
    );
    sampleData.forEach(filteredAggregator::accept);

    verify(downstreamMock, times(4)).accept(updatesCaptor.capture());
    final double[] sortedActualUpdates = updatesCaptor.getAllValues().stream()
        .mapToDouble(update -> update.price()).sorted().toArray();
    assertArrayEquals(sortedPricesValuesOverTwo, sortedActualUpdates);

  }

  @Test
  void Should_ThrowException_When_PredicateIsNull() {
    assertThrows(NullPointerException.class, () -> {
      FilteredAggregator filteredAggregator = new FilteredAggregator(
          null, Mockito.mock(PriceAggregate.class)
      );
    });
  }

  @Test
  void Should_ThrowException_When_DownstreamIsNull() {
    assertThrows(NullPointerException.class, () -> {
      FilteredAggregator filteredAggregator = new FilteredAggregator(update -> true, null);
    });
  }

  @Test
  void Should_CacheValue_When_NoMoreMatchingDataIsProvided() {
    final double[] priceUpdates = new double[]{1, 2, 3};
    FilteredAggregator unfilteredAggregator = new FilteredAggregator(update -> true,
        downstreamMock);
    aggregateAndGet(unfilteredAggregator, sampleUpdates(priceUpdates));
    unfilteredAggregator.getAggregateValue();
    unfilteredAggregator.getAggregateValue();
    verify(downstreamMock, times(1)).getAggregateValue();
  }

  @Test
  void Should_ResetCache_When_MatchingDataIsProvided() {
    final double[] priceUpdates = new double[]{1, 2, 3};
    FilteredAggregator unfilteredAggregator = new FilteredAggregator(update -> true,
        downstreamMock);
    aggregateAndGet(unfilteredAggregator, sampleUpdates(priceUpdates));
    unfilteredAggregator.accept(
        new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE.minusDays(50), 1.0));
    unfilteredAggregator.getAggregateValue();
    verify(downstreamMock, times(2)).getAggregateValue();
  }

}