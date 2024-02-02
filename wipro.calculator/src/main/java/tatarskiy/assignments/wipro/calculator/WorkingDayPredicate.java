package tatarskiy.assignments.wipro.calculator;

import java.time.DayOfWeek;
import java.util.function.Predicate;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

public class WorkingDayPredicate implements Predicate<InstrumentPriceUpdate> {

  private static WorkingDayPredicate INSTANCE = new WorkingDayPredicate();

  public static WorkingDayPredicate instance() {
    return INSTANCE;
  }

  @Override
  public boolean test(InstrumentPriceUpdate update) {
    DayOfWeek dow = update.date().getDayOfWeek();
    return dow != DayOfWeek.SATURDAY
        && dow != DayOfWeek.SUNDAY;
  }
}
