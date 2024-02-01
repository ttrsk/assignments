package tatarskiy.assignments.wipro.calculator.model;

import java.time.LocalDate;
import java.util.OptionalDouble;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregate;

public class TestUtils {

  public static String TEST_INSTRUMENT = "TEST_INSTRUMENT";
  public static String OTHER_TEST_INSTRUMENT = "OTHER_TEST_INSTRUMENT";
  public static LocalDate SAMPLE_DATE = LocalDate.now();

  public static Stream<InstrumentPriceUpdate> sampleUpdates(String instrument, LocalDate endDate,
      double... values) {
    return IntStream.range(0, values.length).mapToObj(i ->
        new InstrumentPriceUpdate(instrument, endDate.minusDays(i), values[i])
    );
  }

  public static Stream<InstrumentPriceUpdate> singleValueUpdates(int length, String instrument,
      LocalDate endDate,
      double value) {
    return IntStream.range(0, length).mapToObj(i ->
        new InstrumentPriceUpdate(instrument, endDate.minusDays(i), value)
    );
  }

  public static Stream<InstrumentPriceUpdate> sampleUpdates(double... values) {
    return sampleUpdates(TEST_INSTRUMENT, SAMPLE_DATE, values);
  }

  public static OptionalDouble aggregateAndGet(PriceAggregate aggregator,
      Stream<InstrumentPriceUpdate> updates) {
    updates.forEach(aggregator::accept);
    return aggregator.getAggregateValue();
  }
}
