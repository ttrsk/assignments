package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import java.util.OptionalDouble;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregate;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

public final class MeanAggregator implements PriceAggregate {

  // simple concurrent implementation that uses locks to make concurrent changes atomic.
  Lock lock = new ReentrantLock();
  double sum = 0.0d;
  long count = 0L;

  @Override
  public OptionalDouble getAggregateValue() {
    return count > 0 ? OptionalDouble.of(sum / count) : OptionalDouble.empty();
  }

  @Override
  public void accept(InstrumentPriceUpdate update) {
    lock.lock();
    try {
      sum += update.price();
      count += 1;
    } finally {
      lock.unlock();
    }
  }

}
