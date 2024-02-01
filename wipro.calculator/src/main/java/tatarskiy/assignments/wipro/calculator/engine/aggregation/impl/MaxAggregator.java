package tatarskiy.assignments.wipro.calculator.engine.aggregation.impl;

import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicLong;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregate;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

public class MaxAggregator implements PriceAggregate {

  // There is no atomic doubles, so we emulate them storing long bit data.
  // All double values have bit representation of 0x<sign><exponent><mantissa>, so:
  // - bit representation of positive values (sign = 0) maintains natural order
  // - bit representation of negative values (sign = 1) maintains reverse natural order
  // The solution is to xor value with sign bit except for sign bit itself,
  // i.e (bits >> 64 & 0x7fffffffffffffffL).
  //  - positive values have sign bit of 1, the rest remains as is
  //  - negative values have sign bit of 0 and the rest of their bits are inverted
  // this effectively reverses sort order
  //  - all positive values > all negative values due to sign bit
  //  - after sort order for negative is inverted both negative and positive values
  //  maintain natural sort order.
  // xor-ing operation reverses by itself.
  // NAN bits are used to indicate unset values

  private final static long UNSET_BITS = Double.doubleToLongBits(Double.NaN);

  private final AtomicLong maxSortableBits = new AtomicLong(UNSET_BITS);

  private static long optionalDoubleMax(long a, long b) {
    return (a == UNSET_BITS) ? b : Math.max(a, b);
  }

  @Override
  public OptionalDouble getAggregateValue() {
    final long bits = maxSortableBits.get();
    if (bits == UNSET_BITS) {
      return OptionalDouble.empty();
    } else {
      return OptionalDouble.of(fromSortableBits(bits));
    }
  }

  private long xorWithSign(long bits) {
    return bits ^= (bits >> 63) & 0x7fffffffffffffffL;
  }

  private double fromSortableBits(long bits) {
    return Double.longBitsToDouble(xorWithSign(bits));
  }

  private long toSortableBits(double value) {
    return xorWithSign(Double.doubleToLongBits(value));
  }

  @Override
  public void accept(InstrumentPriceUpdate instrumentPriceUpdate) {
    maxSortableBits.getAndAccumulate(toSortableBits(instrumentPriceUpdate.price()),
        MaxAggregator::optionalDoubleMax);
  }
}
