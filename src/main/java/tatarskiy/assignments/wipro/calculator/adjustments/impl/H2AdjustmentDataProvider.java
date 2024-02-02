package tatarskiy.assignments.wipro.calculator.adjustments.impl;

import java.util.OptionalDouble;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;
import tatarskiy.assignments.wipro.calculator.adjustments.AdjustmentDataProvider;
import tatarskiy.assignments.wipro.calculator.adjustments.repository.PriceModifierRepository;

@Component
public class H2AdjustmentDataProvider implements AdjustmentDataProvider {

  private final PriceModifierRepository repository;

  public H2AdjustmentDataProvider(PriceModifierRepository repository) {
    this.repository = repository;
  }

  @Override
  public OptionalDouble getMultiplier(String instrument) {
    return repository
        .findByName(instrument)
        .map(r -> OptionalDouble.of(r.getMultiplier()))
        .orElse(OptionalDouble.empty());
  }
}
