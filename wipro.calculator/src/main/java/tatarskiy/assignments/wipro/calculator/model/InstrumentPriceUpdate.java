package tatarskiy.assignments.wipro.calculator.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.OptionalDouble;

public record InstrumentPriceUpdate(String instrument, LocalDate date, double price) {

  public InstrumentPriceUpdate {
    Objects.requireNonNull(instrument, "Instrument cannot be null");
    Objects.requireNonNull(date, "Date cannot be null");
    if (price <= 0.0) {
      throw new IllegalArgumentException("Price must be positive value, got " + price);
    }
    if (instrument.isBlank()) {
      throw new IllegalArgumentException("Instrument value cannot be blank");
    }
  }

  private static final DateTimeFormatter DD_MMM_YYYY_FORMATTER = DateTimeFormatter.ofPattern(
      "dd-MMM-yyyy");

  public static InstrumentPriceUpdate parse(String line) {
    Objects.requireNonNull(line, "String value cannot be null");
    String[] strValues = line.split(",");
    if (strValues.length != 3) {
      throw new IllegalArgumentException("Invalid line format, should contain 3 comma-separated"
          + " values - instrument, date and price value  while actual value was " + line);
    }
    return new InstrumentPriceUpdate(strValues[0],
        LocalDate.parse(strValues[1], DD_MMM_YYYY_FORMATTER),
        Double.parseDouble(strValues[2]));
  }

  public InstrumentPriceUpdate adjust(OptionalDouble multiplier) {
    if (multiplier.isPresent()) {
      return new InstrumentPriceUpdate(instrument, date, price * multiplier.getAsDouble());
    } else {
      return this;
    }
  }

}
