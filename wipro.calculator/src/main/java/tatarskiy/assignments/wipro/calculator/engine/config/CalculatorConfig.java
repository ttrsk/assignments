package tatarskiy.assignments.wipro.calculator.engine.config;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregator;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregatorFactory;

public record CalculatorConfig(
    Supplier<? extends PriceAggregator> defaultAggregateSupplier,
    Map<String, Supplier<? extends PriceAggregator>> instrumentAggregateSuppliers) implements
    PriceAggregatorFactory {

  public CalculatorConfig {
    Objects.requireNonNull(defaultAggregateSupplier);
    Objects.requireNonNull(instrumentAggregateSuppliers);
  }

  public CalculatorConfig(Supplier<? extends PriceAggregator> defaultAggregateSupplier) {
    this(defaultAggregateSupplier, Collections.emptyMap());
  }

  @Override
  public PriceAggregator create(String instrument) {
    return instrumentAggregateSuppliers.getOrDefault(instrument, defaultAggregateSupplier).get();
  }
}
