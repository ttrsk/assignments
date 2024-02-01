package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.TEST_INSTRUMENT;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.aggregateAndGet;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.OptionalDouble;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregate;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;
import tatarskiy.assignments.wipro.calculator.model.TestUtils;

class MonthlyMeanAggregatorTest {


  private Stream<InstrumentPriceUpdate> buildTestStream(final YearMonth aggregationMonth,
      final double targetValue,
      final boolean onlyNonmatchingData) {
    final LocalDate monthStartDate = aggregationMonth.atDay(1);
    final LocalDate monthEndDate = aggregationMonth.atEndOfMonth();
    final int additionalDays = 50;

    // data not within the month is 10x of target value
    final Stream extraData = Stream.concat(
        TestUtils.singleValueUpdates(additionalDays, TEST_INSTRUMENT,
            monthStartDate.minusDays(1),
            targetValue * 10),
        TestUtils.singleValueUpdates(additionalDays, TEST_INSTRUMENT,
            monthEndDate.plusDays(additionalDays + 1), targetValue * 10)
    );
    if (onlyNonmatchingData) {
      return extraData;
    } else {
      return Stream.concat(extraData,
          TestUtils.singleValueUpdates(aggregationMonth.lengthOfMonth(), TEST_INSTRUMENT,
              monthEndDate, targetValue));
    }
  }

  @Test
  void Should_CalculateMeanForUpdatesInGivenMonth() {
    final YearMonth aggregationMonth = YearMonth.of(2024, 1);
    final double targetValue = 1.0;
    final Stream<InstrumentPriceUpdate> updateStream = buildTestStream(aggregationMonth,
        targetValue, false);
    PriceAggregate aggregator = new MonthlyMeanAggregator(aggregationMonth);
    OptionalDouble actualValue = aggregateAndGet(aggregator, updateStream);
    assertEquals(OptionalDouble.of(targetValue), actualValue);
  }

  @Test
  void Should_ReturnEmptyValue_When_DataForTargetMonthIsNotAvailable() {
    final YearMonth aggregationMonth = YearMonth.of(2024, 1);
    final double targetValue = 2.0;
    final Stream<InstrumentPriceUpdate> updateStream = buildTestStream(aggregationMonth,
        targetValue, true);
    PriceAggregate aggregator = new MonthlyMeanAggregator(aggregationMonth);
    OptionalDouble actualValue = aggregateAndGet(aggregator, updateStream);
    assertEquals(OptionalDouble.empty(), actualValue);
  }

  @Test
  void Should_ThrowException_When_YearMonthIsNull() {
    assertThrows(NullPointerException.class, () -> {
      MonthlyMeanAggregator filteredAggregator = new MonthlyMeanAggregator(null);
    });
  }

}