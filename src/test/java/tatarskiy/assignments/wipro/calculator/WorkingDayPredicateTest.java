package tatarskiy.assignments.wipro.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

class WorkingDayPredicateTest {

  private static final int SAMPLE_YEAR = 2024;
  private static final String TEST_INSTRUMENT = "TEST";

  private static LocalDate firstDayOfWeekInAYear(int year, DayOfWeek dow) {
    LocalDate firstDayOfYear = LocalDate.of(year, Month.JANUARY, 1);
    return firstDayOfYear.with(dow);
  }

  private static Stream<InstrumentPriceUpdate> getWeekendUpdates() {
    return
        Stream.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            .map(WorkingDayPredicateTest::buildUpdateForDayOfWeek);
  }

  private static InstrumentPriceUpdate buildUpdateForDayOfWeek(DayOfWeek dayOfWeek) {
    return new InstrumentPriceUpdate(
        TEST_INSTRUMENT,
        firstDayOfWeekInAYear(SAMPLE_YEAR, dayOfWeek),
        1.0
    );
  }

  private static Stream<InstrumentPriceUpdate> getWeekdayUpdates() {
    return Stream.of(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    ).map(WorkingDayPredicateTest::buildUpdateForDayOfWeek);
  }

  @ParameterizedTest
  @MethodSource("getWeekendUpdates")
  void Should_ReturnFalse_When_DateIsWeekend(InstrumentPriceUpdate weekendUpdate) {
    assertEquals(false, new WorkingDayPredicate().test(weekendUpdate));

  }

  @ParameterizedTest
  @MethodSource("getWeekdayUpdates")
  void Should_ReturnTrue_When_DateIsWorkingDay(InstrumentPriceUpdate weekdayUpdate) {
    assertEquals(true, new WorkingDayPredicate().test(weekdayUpdate));
  }
}