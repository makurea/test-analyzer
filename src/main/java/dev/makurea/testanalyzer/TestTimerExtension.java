package dev.makurea.testanalyzer;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.extension.*;

public class TestTimerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback,
    BeforeEachCallback, AfterEachCallback, AfterAllCallback {

  private final List<TestResult> results = new ArrayList<>();
  private final ReportGenerator reportGenerator = new ReportGenerator();

  private static final String START_TIME_TEST = "start-time-test";
  private static final String START_TIME_BEFORE_EACH = "start-time-before-each";

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
    results.add(new TestResult(context.getDisplayName(), "BeforeEach", duration));
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
    results.add(new TestResult(context.getDisplayName(), "TestMethod", duration));
  }

  @Override
  public void afterAll(ExtensionContext context) {
    reportGenerator.generateReport(results);
  }

  public static class TestResult {
    public String testName;
    public String phase;
    public long durationMs;

    public TestResult(String testName, String phase, long durationMs) {
      this.testName = testName;
      this.phase = phase;
      this.durationMs = durationMs;
    }
  }
}
