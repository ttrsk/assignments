package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

public class MonthlyMeanAggregator extends FilteredAggregator {

  public MonthlyMeanAggregator(final YearMonth yearMonth) {
    super(update -> isYearOfMonth(update.date(), yearMonth), new MeanAggregator());
    Objects.requireNonNull(yearMonth, "yearMonth cannot be null");
  }

  static boolean isYearOfMonth(LocalDate localDate, YearMonth yearMonth) {
    return yearMonth.getYear() == localDate.getYear()
        && yearMonth.getMonth() == localDate.getMonth();
  }
}
