package tatarskiy.assignments.wipro.calculator.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record InstrumentPriceUpdate(String instrument, PriceUpdate priceUpdate) {

  private static final DateTimeFormatter DD_MMM_YYYY_FORMATTER = DateTimeFormatter.ofPattern(
      "dd-MMM-yyyy");

  public InstrumentPriceUpdate(String instrument, LocalDate date, double price) {
    this(instrument, new PriceUpdate(date, price));
  }

  public static InstrumentPriceUpdate parse(String line) {
    String[] strValues = line.split(",");
    return new InstrumentPriceUpdate(strValues[0],
        LocalDate.parse(strValues[1], DD_MMM_YYYY_FORMATTER),
        Double.parseDouble(strValues[2]));

  }

  public double price() {
    return priceUpdate.price();
  }

  public LocalDate date() {
    return priceUpdate.date();
  }

}
