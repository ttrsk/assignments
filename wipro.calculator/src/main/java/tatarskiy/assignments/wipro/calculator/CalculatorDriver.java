package tatarskiy.assignments.wipro.calculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tatarskiy.assignments.wipro.calculator.engine.CalculatorEngine;
import tatarskiy.assignments.wipro.calculator.model.AggregationResult;
import tatarskiy.assignments.wipro.calculator.model.InstrumentPriceUpdate;

// Reads input data, runs calculator engine and collects execution metrics (time and memory usage)
@Component
class CalculatorDriver {

  private final boolean isParallel;
  private final CalculatorEngine calculatorEngine;

  @Autowired
  public CalculatorDriver(
      @Autowired CalculatorEngine calculatorEngine,
      @Value("${calculator.parallel:true}") boolean isParallel) {
    this.calculatorEngine = calculatorEngine;
    this.isParallel = isParallel;
  }

  private Stream<String> createStream(Path sourceFilePath) throws IOException {
    Stream<String> lineStream = Files.lines(sourceFilePath);
    if (isParallel) {
      return lineStream.parallel();
    } else {
      return lineStream;
    }
  }

  public CalculationResults runCalculation(String sourceFile) throws IOException, RuntimeException {
    return runCalculation(Path.of(sourceFile));
  }

  public CalculationResults runCalculation(Path sourceFilePath)
      throws IOException, RuntimeException {
    Runtime runtime = Runtime.getRuntime();
    Instant start = Instant.now();
    long totalMemoryBefore = runtime.totalMemory();
    Stream<InstrumentPriceUpdate> updates = createStream(sourceFilePath)
        .filter(Predicate.not(String::isEmpty))
        .map(InstrumentPriceUpdate::parse);
    List<AggregationResult> aggregationResults = calculatorEngine.calculate(updates);
    Duration processingDuration = Duration.between(start, Instant.now());
    long totalMemoryAfter = runtime.totalMemory();
    return new CalculationResults(aggregationResults, processingDuration,
        Math.max(totalMemoryBefore, totalMemoryAfter));
  }
}
