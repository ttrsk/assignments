package tatarskiy.assignments.wipro.calculator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tatarskiy.assignments.wipro.calculator.adjustments.AdjustmentDataProvider;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregator;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.impl.MaxAggregator;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.impl.MeanAggregator;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.impl.SumTopNRecentAggregator;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.impl.TransformingAggregator;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

@Component
class AggregatorBuilder {

  private final AdjustmentDataProvider adjustmentDataProvider;

  public AggregatorBuilder(@Autowired AdjustmentDataProvider adjustmentDataProvider) {
    this.adjustmentDataProvider = adjustmentDataProvider;
  }

  public Supplier<PriceAggregator> adjustedTopTenRecentSumSupplier() {
    return adjustedAggregatorSupplier(() -> new SumTopNRecentAggregator(10));
  }

  public Supplier<PriceAggregator> adjustedMedianSupplier() {
    return adjustedAggregatorSupplier(MeanAggregator::new);
  }

  public Supplier<PriceAggregator> adjustedMaxSupplier() {
    return adjustedAggregatorSupplier(MaxAggregator::new);
  }


  private boolean isDateInYearMonth(LocalDate date, YearMonth yearMonth) {
    return date.getYear() == yearMonth.getYear() && date.getMonth() == yearMonth.getMonth();
  }

  public Supplier<PriceAggregator> adjustedMonthlyMedianSupplier(
      YearMonth yearMonth) {
    return adjustedAggregatorSupplier(() -> new SumTopNRecentAggregator(10),
        update -> isDateInYearMonth(update.date(), yearMonth));
  }

  private Supplier<PriceAggregator> adjustedAggregatorSupplier(
      Supplier<PriceAggregator> downstreamSupplier) {
    return adjustedAggregatorSupplier(downstreamSupplier, null);
  }

  private Supplier<PriceAggregator> adjustedAggregatorSupplier(
      Supplier<PriceAggregator> downstreamSupplier,
      Predicate<InstrumentPriceUpdate> filter
  ) {
    return () -> new TransformingAggregator(
        update -> update.adjust(
            adjustmentDataProvider.getMultiplier(update.instrument())
        ),
        downstreamSupplier.get(),
        filter
    );
  }
}
