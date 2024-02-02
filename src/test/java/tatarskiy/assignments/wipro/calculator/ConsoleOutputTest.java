package tatarskiy.assignments.wipro.calculator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConsoleOutputTest {

  final static String LINESEP = System.lineSeparator();

  private void assertOutput(String expectedOut, String expectedErr, Executable e) {
    final PrintStream savedOut = System.out;
    final PrintStream savedErr = System.err;
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    try (
        final PrintStream fakeOut = new PrintStream(outStream);
        final PrintStream fakeErr = new PrintStream(errStream);
    ) {
      System.setOut(fakeOut);
      System.setErr(fakeErr);
      e.execute();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    } finally {
      System.setOut(savedOut);
      System.setErr(savedErr);
    }
    Assertions.assertEquals(expectedOut, outStream.toString());
    Assertions.assertEquals(expectedErr, errStream.toString());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void Should_OutputMessageToStdout_When_SilentIsTrueOrFalse(boolean silent) {
    ConsoleOutput output = new ConsoleOutput(silent);
    final String message = "Sample message";
    assertOutput(message + System.lineSeparator(), "", () -> {
      output.output(message);
    });
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void Should_OutputErrorMessageToStderr_When_SilentIsTrueOrFalse(boolean silent) {
    final ConsoleOutput output = new ConsoleOutput(silent);
    final String errorMessage = "Sample error message";
    assertOutput("", "ERROR: " + errorMessage + LINESEP, () -> output.error(errorMessage));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void Should_OutputRawMessageToStderr_When_ErrOutputIsCalled(boolean silent) {
    final ConsoleOutput output = new ConsoleOutput(silent);
    final String errorMessage = "Sample error message";
    assertOutput("", errorMessage + LINESEP, () -> output.errOutput(errorMessage));
  }

  @Test
  void Should_OutputUserMessageToStdout_When_SilentIsFalse() {
    final ConsoleOutput output = new ConsoleOutput(false);
    final String message = "Sample user message";
    assertOutput(message + LINESEP, "", () -> output.userMessage(message));
  }

  @Test
  void Should_SkipUserMessage_When_SilentIsTrue() {
    final ConsoleOutput output = new ConsoleOutput(true);
    final String message = "Sample user message";
    assertOutput("", "", () -> output.userMessage(message));
  }


}
