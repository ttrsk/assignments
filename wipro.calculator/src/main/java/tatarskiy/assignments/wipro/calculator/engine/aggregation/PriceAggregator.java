package tatarskiy.assignments.wipro.calculator.engine.aggregation;

import java.util.OptionalDouble;
import java.util.function.Consumer;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

// Encapsulates aggregation rules for particular instrument and resulting aggregated value
// Aggregation rules are:
// - Specific aggregation algorithm executed by accept(InstrumentPriceUpdate)
// - Method to get aggregate value
public interface PriceAggregator
    extends Consumer<InstrumentPriceUpdate> {

  OptionalDouble getAggregateValue();

}
