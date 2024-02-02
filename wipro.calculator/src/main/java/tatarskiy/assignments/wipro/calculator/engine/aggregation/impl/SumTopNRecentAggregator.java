package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalDouble;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Stream;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregator;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

public class SumTopNRecentAggregator implements PriceAggregator {

  private final int n;
  private final PriorityBlockingQueue<InstrumentPriceUpdate> latestUpdates;

  public SumTopNRecentAggregator(int n) {
    this.n = n;
    this.latestUpdates = new PriorityBlockingQueue<InstrumentPriceUpdate>(n,
        (u1, u2) -> u2.date().compareTo(u1.date()));
  }

  private Collection<InstrumentPriceUpdate> moveOrderedUpdates() {
    Collection<InstrumentPriceUpdate> orderedElements = new ArrayList<>(latestUpdates.size());
    latestUpdates.drainTo(orderedElements);
    return orderedElements;
  }

  @Override
  public OptionalDouble getAggregateValue() {
    Collection<InstrumentPriceUpdate> orderedUpdates = moveOrderedUpdates();
    Stream<InstrumentPriceUpdate> topNStream = orderedUpdates.stream();
    // skip extra records if they available due to the lack of atomicity in accept operations
    if (orderedUpdates.isEmpty()) {
      return OptionalDouble.empty();
    }

    if (orderedUpdates.size() > n) {
      topNStream = topNStream.skip(n - orderedUpdates.size());
    }

    double aggregate = topNStream.mapToDouble(InstrumentPriceUpdate::price).sum();
    // restore state
    orderedUpdates.forEach(this::accept);
    return OptionalDouble.of(aggregate);
  }

  private boolean isNewerThanOldest(LocalDate updateDate) {
    return latestUpdates.peek().date().isAfter(updateDate);
  }

  private boolean isCapacityReached() {
    return latestUpdates.size() >= n;
  }

  @Override
  public void accept(InstrumentPriceUpdate update) {
    if (isCapacityReached()) {
      if (isNewerThanOldest(update.date())) {
        latestUpdates.offer(update);
        latestUpdates.poll();
      }
    } else {
      // double-add is possible during concurrent execution
      // we will filter extra records when aggregating
      latestUpdates.offer(update);
    }
  }

}
