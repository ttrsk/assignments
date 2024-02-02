package tatarskiy.assignments.wipro.calculator;

public interface Output {

  // Report user message that will not be shown
  public void userMessage(String message);

  // Generate output
  public void output(String message);

  // Display error message
  public void error(String message);

  // Display error message with exception details
  public void error(String message, Throwable ex);

  public void errOutput(String message);
}
