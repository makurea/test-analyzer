package dev.makurea.testanalyzer;

import org.junit.jupiter.api.extension.*;

public class TestTimerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final String START_TIME = "start-time";

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    long startTime = System.currentTimeMillis();
    context.getStore(ExtensionContext.Namespace.create(getClass()))
        .put(START_TIME, startTime);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    long endTime = System.currentTimeMillis();
    long startTime = context.getStore(ExtensionContext.Namespace.create(getClass()))
        .remove(START_TIME, long.class);
    long duration = endTime - startTime;

    String testName = context.getDisplayName();
    System.out.printf("[TestTimer] %s executed in %d ms%n", testName, duration);
  }
}
