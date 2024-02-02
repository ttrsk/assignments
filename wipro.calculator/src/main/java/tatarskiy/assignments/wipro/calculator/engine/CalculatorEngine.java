package tatarskiy.assignments.wipro.calculator.engine;

import java.util.List;
import java.util.stream.Stream;
import tatarskiy.assignments.wipro.calculator.model.AggregationResult;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

// Processes data and calculates
public interface CalculatorEngine {

  // Calculates aggregate values for instrument price updates.
  // Returns list of AggregationResult (a pair of instrument name and optional aggregate value)
  // for instruments present in original input sorted by instrument name.
  // If present events were filtered out then aggregate value is OptionalDouble.empty();
  List<AggregationResult> calculate(Stream<InstrumentPriceUpdate> updateStream);

}