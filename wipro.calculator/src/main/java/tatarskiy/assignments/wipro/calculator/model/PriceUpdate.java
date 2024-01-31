package tatarskiy.assignments.wipro.calculator.model;

import java.time.LocalDate;
import java.util.OptionalDouble;

public record PriceUpdate(LocalDate date, double price) {

  public PriceUpdate adjust(OptionalDouble multiplier) {
    if (multiplier.isPresent()) {
      return new PriceUpdate(date, price * multiplier.getAsDouble());
    } else {
      return this;
    }
  }
}
