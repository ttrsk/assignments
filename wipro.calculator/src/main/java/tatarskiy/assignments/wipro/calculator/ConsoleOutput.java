package tatarskiy.assignments.wipro.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ConsoleOutput implements Output {

  private final boolean verbose;

  @Autowired
  public ConsoleOutput(@Value("${calculator.silent:false}") boolean silent) {
    this.verbose = !silent;
  }

  @Override
  public void userMessage(String message) {
    if (verbose) {
      output(message);
    }
  }

  @Override
  public void output(String message) {
    System.out.println(message);
  }

  @Override
  public void error(String message) {
    System.err.println("ERROR: " + message);
  }

  public void error(String message, Throwable ex) {
    error(message);
    if (ex != null) {
      ex.printStackTrace(System.err);
    } else {
      error("Error details are not available, capturing current stack trace", new Exception());
    }
  }

}
