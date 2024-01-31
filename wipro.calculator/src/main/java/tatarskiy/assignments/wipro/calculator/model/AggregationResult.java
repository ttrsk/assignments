package tatarskiy.assignments.wipro.calculator.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.OptionalDouble;

public record AggregationResult(String instrument, OptionalDouble result)
    implements Comparable<AggregationResult> {

  private static String EMPTY = "EMPTY";
  private static String DELIMITER = " - ";

  private static ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<>() {
    @Override
    public NumberFormat initialValue() {
      return new DecimalFormat("#.##########");
    }
  };

  @Override
  public int compareTo(AggregationResult o) {
    return instrument.compareTo(o.instrument);
  }

  public String getUserString() {
    return instrument + DELIMITER
        + (result.isPresent() ? numberFormat.get().format(result.getAsDouble()) : EMPTY);
  }
}
