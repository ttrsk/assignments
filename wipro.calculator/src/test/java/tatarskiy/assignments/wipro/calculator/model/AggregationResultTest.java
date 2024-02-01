package tatarskiy.assignments.wipro.calculator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.TEST_INSTRUMENT;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class AggregationResultTest {

  @Test
  void Should_SortUpdatesAscengingByInstrument_WhenNaturalSortOrderIsSpecified() {
    AggregationResult first = new AggregationResult(TEST_INSTRUMENT, OptionalDouble.empty());
    AggregationResult second = new AggregationResult(TEST_INSTRUMENT + "1", OptionalDouble.empty());
    AggregationResult third = new AggregationResult(TEST_INSTRUMENT + "2", OptionalDouble.empty());
    List<AggregationResult> sorted = List.of(first, second, third);

    List<AggregationResult> reversedThenSorted = IntStream
        .range(0, sorted.size())
        .mapToObj(i -> sorted.get(sorted.size() - 1 - i))
        .sorted()
        .collect(Collectors.toList());

    assertEquals(sorted, reversedThenSorted);
  }

  @Test
  void Should_TruncateDecimals_WhenDecimalsAreLong() {
    String actual = new AggregationResult(TEST_INSTRUMENT,
        OptionalDouble.of(1.12345678914)).getUserString();
    String expected = TEST_INSTRUMENT + " - 1.1234567891";
    assertEquals(expected, actual);
  }

  @Test
  void Should_ShowEmptyValueInUserString_WhenResultIsEmpty() {
    String actual = new AggregationResult(TEST_INSTRUMENT, OptionalDouble.empty()).getUserString();
    String expected = TEST_INSTRUMENT + " - EMPTY";
    assertEquals(expected, actual);
  }

  @Test
  void Should_FormatUserString_WhenResultIsAvailable() {
    String actual = new AggregationResult(TEST_INSTRUMENT,
        OptionalDouble.of(10.123)).getUserString();
    String expected = TEST_INSTRUMENT + " - 10.123";
    assertEquals(expected, actual);
  }


  @Test
  void Should_ThrowException_WhenResultIsNull() {
    assertThrows(NullPointerException.class,
        () -> new AggregationResult(TEST_INSTRUMENT, null));
  }

  @Test
  void Should_ThrowException_WhenInstrumentIsNull() {
    assertThrows(NullPointerException.class,
        () -> new AggregationResult(null, OptionalDouble.of(1.0)));
  }
}