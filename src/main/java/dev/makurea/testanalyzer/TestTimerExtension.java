package dev.makurea.testanalyzer;

import org.junit.jupiter.api.extension.*;

public class TestTimerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback,
    BeforeEachCallback, AfterEachCallback {

  private static final String START_TIME_TEST = "start-time-test";
  private static final String START_TIME_BEFORE_EACH = "start-time-before-each";
  private static final String START_TIME_AFTER_EACH = "start-time-after-each";

  @Override
  public void beforeEach(ExtensionContext context) {
    context.getStore(ExtensionContext.Namespace.create(getClass()))
        .put(START_TIME_BEFORE_EACH, System.currentTimeMillis());
  }

  @Override
  public void afterEach(ExtensionContext context) {
    long start = context.getStore(ExtensionContext.Namespace.create(getClass()))
        .remove(START_TIME_BEFORE_EACH, long.class);
    long duration = System.currentTimeMillis() - start;
    System.out.printf("[TestTimer] %s BeforeEach executed in %d ms%n", context.getDisplayName(), duration);
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    context.getStore(ExtensionContext.Namespace.create(getClass()))
        .put(START_TIME_TEST, System.currentTimeMillis());
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    long start = context.getStore(ExtensionContext.Namespace.create(getClass()))
        .remove(START_TIME_TEST, long.class);
    long duration = System.currentTimeMillis() - start;
    System.out.printf("[TestTimer] %s Test method executed in %d ms%n", context.getDisplayName(), duration);
  }
}

