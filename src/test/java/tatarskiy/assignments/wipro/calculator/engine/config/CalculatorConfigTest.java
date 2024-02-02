package tatarskiy.assignments.wipro.calculator.engine.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.OTHER_TEST_INSTRUMENT;
import static tatarskiy.assignments.wipro.calculator.model.TestUtils.TEST_INSTRUMENT;

import java.util.Collections;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import tatarskiy.assignments.wipro.calculator.engine.aggregation.PriceAggregator;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

class CalculatorConfigTest {

  private final Supplier<PriceAggregator> defaultSupplier = Mockito.mock(Supplier.class);

  @BeforeEach
  void setUp() {
    Mockito.when(defaultSupplier.get()).thenAnswer(new Answer<PriceAggregator>() {
      @Override
      public PriceAggregator answer(InvocationOnMock invocationOnMock) throws Throwable {
        return new PriceAggregator() {
          @Override
          public OptionalDouble getAggregateValue() {
            return OptionalDouble.empty();
          }

          @Override
          public void accept(InstrumentPriceUpdate instrumentPriceUpdate) {
          }
        };
      }
    });
  }

  @Test
  void Should_CallDefaultSupplier_When_DefaultInstanceIsRequested() {
    CalculatorConfig config = new CalculatorConfig(defaultSupplier);
    PriceAggregator pa1 = config.create(TEST_INSTRUMENT);
    PriceAggregator pa2 = config.create(OTHER_TEST_INSTRUMENT);
    Mockito.verify(defaultSupplier, times(2)).get();
    assertTrue(pa1 != pa2);
  }

  @Test
  void Should_ThrowException_When_DefaultSupplierIsNull() {
    assertThrows(NullPointerException.class, () -> {
      new CalculatorConfig(null, Collections.emptyMap());
    });
  }

  @Test
  void Should_ThrowException_When_SupplierMapIsNull() {
    assertThrows(NullPointerException.class, () -> {
      new CalculatorConfig(defaultSupplier, null);
    });
  }

}