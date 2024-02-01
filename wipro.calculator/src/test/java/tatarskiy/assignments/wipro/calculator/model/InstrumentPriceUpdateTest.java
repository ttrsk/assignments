package tatarskiy.assignments.wipro.calculator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.SAMPLE_DATE;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.TEST_INSTRUMENT;

import java.time.LocalDate;
import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InstrumentPriceUpdateTest {

  @Test
  void Should_ThrowException_When_InstrumentIsNull() {
    assertThrows(NullPointerException.class,
        () -> new InstrumentPriceUpdate(null, SAMPLE_DATE, 1.0));
  }

  @Test
  void Should_ThrowException_When_DayeIsNull() {
    assertThrows(NullPointerException.class,
        () -> new InstrumentPriceUpdate(TEST_INSTRUMENT, null, 1.0));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " "})
  void Should_ThrowException_When_InstrumentIsBlank(String instrument) {
    assertThrows(IllegalArgumentException.class,
        () -> new InstrumentPriceUpdate(instrument, SAMPLE_DATE, 1.0));
  }

  @Test
  void Should_ThrowException_When_DateIsNull() {
    assertThrows(NullPointerException.class,
        () -> new InstrumentPriceUpdate(TEST_INSTRUMENT, null, 1.0));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0, -1})
  void Should_ThrowException_When_PriceIsNonPositive(double price) {
    assertThrows(IllegalArgumentException.class,
        () -> new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE, price));
  }


  @Test
  void Should_AdjustValue_When_AdjustmentIsProvided() {
    final double initialPrice = 1.0;
    final double multiplier = 5.0;
    final double adjustedPrice = 5.0;
    InstrumentPriceUpdate original = new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE,
        initialPrice);
    InstrumentPriceUpdate expected = new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE,
        adjustedPrice);
    assertEquals(expected, original.adjust(OptionalDouble.of(multiplier)));
  }

  @Test
  void Should_ReturnSameObject_When_AdjustmentIsEmpty() {
    InstrumentPriceUpdate original = new InstrumentPriceUpdate(TEST_INSTRUMENT, SAMPLE_DATE, 1.0);
    InstrumentPriceUpdate adjusted = original.adjust(OptionalDouble.empty());
    assertTrue(original == adjusted);
  }

  @Test
  void Should_ParsePriceUpdate_When_LineIsValid() {
    InstrumentPriceUpdate parsed = InstrumentPriceUpdate.parse("TEST_INSTRUMENT,08-Dec-2020,1.5");
    InstrumentPriceUpdate expected = new InstrumentPriceUpdate(TEST_INSTRUMENT,
        LocalDate.of(2020, 12, 8), 1.5);
    assertEquals(expected, parsed);
  }

  @Test
  void Should_ThrowException_When_LineIsNull() {
    assertThrows(NullPointerException.class, () -> InstrumentPriceUpdate.parse(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "A",
      ",,",
      "A,08-Dec-2020,x",
      "A,08-Dec-2020,1.5,x",
      "A,08-Dec-2020,-1.5",
      ",08-Dec-2020,1.5",
      "A,08-Gec-2020,1.5",
      "A,08 Dec 2020,1.5",
  })
  void Should_ThrowException_When_LineIsInvalid(String line) {
    assertThrows(RuntimeException.class, () -> InstrumentPriceUpdate.parse(line));
  }

}