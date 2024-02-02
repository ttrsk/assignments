package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.SAMPLE_DATE;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.TEST_INSTRUMENT;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.aggregateAndGet;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.sampleUpdates;

import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregator;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;


class TransformingAggregatorTest {

  private PriceAggregator downstreamMock = Mockito.mock(PriceAggregator.class);
  private UnaryOperator<InstrumentPriceUpdate> transformMock = Mockito.mock(UnaryOperator.class);

  @BeforeEach
  void setUp() {
    Mockito.when(transformMock.apply(any(InstrumentPriceUpdate.class)))
        .thenAnswer(i -> i.getArguments()[0]);
  }

  @Test
  void Should_PassDataToDownstream_When_MatchingDataIsProvided() {
    final double[] priceUpdates = new double[]{1, 1, 3, 4, 3, 5};
    final double[] sortedPricesValuesOverTwo = new double[]{3.0, 4.0, 5.0, 3.0};
    Arrays.sort(sortedPricesValuesOverTwo);
    Stream<InstrumentPriceUpdate> sampleData = sampleUpdates(priceUpdates);
    ArgumentCaptor<InstrumentPriceUpdate> updatesCaptor = ArgumentCaptor.forClass(
        InstrumentPriceUpdate.class);
    TransformingAggregator transformingAggregator = new TransformingAggregator(
        downstreamMock, update -> update.price() > 2.0
    );
    sampleData.forEach(transformingAggregator::accept);

    verify(downstreamMock, times(4)).accept(updatesCaptor.capture());
    final double[] sortedActualUpdates = updatesCaptor.getAllValues().stream()
        .mapToDouble(update -> update.price()).sorted().toArray();
    assertArrayEquals(sortedPricesValuesOverTwo, sortedActualUpdates);
  }

  void Should_NotTransformData_When_NoMatchingDataIsProvided() {
    TransformingAggregator transformingAggregator = new TransformingAggregator(
        transformMock, downstreamMock, update -> update.price() > 3.0
    );
    InstrumentPriceUpdate update = new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE, 1);
    transformingAggregator.accept(update);
    verify(transformMock, times(0));
  }

  @Test
  void Should_TransformData_When_MatchingDataIsProvided() {
    TransformingAggregator transformingAggregator = new TransformingAggregator(
        transformMock, downstreamMock, update -> update.price() > 3.0
    );
    InstrumentPriceUpdate update = new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE, 4);
    transformingAggregator.accept(update);
    verify(transformMock, times(1)).apply(update);
  }

  @Test
  void Should_ThrowException_When_TransformIsNull() {
    assertThrows(NullPointerException.class, () -> {
      TransformingAggregator transformingAggregator = new TransformingAggregator(null,
          downstreamMock, update -> true);
    });
  }

  @Test
  void Should_ThrowException_When_PredicateIsNull() {
    assertThrows(NullPointerException.class, () -> {
      TransformingAggregator transformingAggregator = new TransformingAggregator(null,
          downstreamMock);
    });
  }

  @Test
  void Should_ThrowException_When_DownstreamIsNull() {
    assertThrows(NullPointerException.class, () -> {
      TransformingAggregator transformingAggregator = new TransformingAggregator(null,
          update -> true);
    });
  }

  @Test
  void Should_CacheValue_When_NoMoreMatchingDataIsProvided() {
    final double[] priceUpdates = new double[]{1, 2, 3};
    TransformingAggregator unfilteredAggregator = new TransformingAggregator(downstreamMock,
        update -> true);
    aggregateAndGet(unfilteredAggregator, sampleUpdates(priceUpdates));
    unfilteredAggregator.getAggregateValue();
    unfilteredAggregator.getAggregateValue();
    verify(downstreamMock, times(1)).getAggregateValue();
  }

  @Test
  void Should_ResetCache_When_MatchingDataIsProvided() {
    final double[] priceUpdates = new double[]{1, 2, 3};
    TransformingAggregator unfilteredAggregator = new TransformingAggregator(downstreamMock,
        update -> true);
    aggregateAndGet(unfilteredAggregator, sampleUpdates(priceUpdates));
    unfilteredAggregator.accept(
        new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE.minusDays(50), 1.0));
    unfilteredAggregator.getAggregateValue();
    verify(downstreamMock, times(2)).getAggregateValue();
  }

}