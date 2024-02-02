package tatarskiy.assignments.wipro.calculator.adjustments;

import java.util.OptionalDouble;

@FunctionalInterface
public interface AdjustmentDataProvider {

  public static AdjustmentDataProvider NO_OP_PROVIDER = instrument -> OptionalDouble.empty();

  // returns optional multiplier to adjust price update
  OptionalDouble getMultiplier(String instrument);
}
