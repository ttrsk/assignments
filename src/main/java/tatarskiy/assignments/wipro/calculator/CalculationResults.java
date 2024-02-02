package tatarskiy.assignments.wipro.calculator;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import tatarskiy.assignments.wipro.calculator.model.AggregationResult;

record CalculationResults(List<AggregationResult> aggregatedValues,
                          Duration processingDuration,
                          long totalMemoryMaximum) {

  public CalculationResults {
    Objects.requireNonNull(aggregatedValues);
    Objects.requireNonNull(processingDuration);
  }

  public String getMemoryUsageMessage() {
    return "Maximum total memory was " + totalMemoryMaximum + " bytes";
  }

  public String getProcessingTimeMessage() {
    return "Processing took "
        + processingDuration.getSeconds() + "."
        + processingDuration.getNano() / 1_000_000 + "s";
  }
}
