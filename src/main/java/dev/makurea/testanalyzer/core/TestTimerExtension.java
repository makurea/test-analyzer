package dev.makurea.testanalyzer.core;

import dev.makurea.testanalyzer.metrics.*;
import dev.makurea.testanalyzer.report.ReportGenerator;
import org.junit.jupiter.api.extension.*;

import java.util.List;

public class TestTimerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback,
    BeforeEachCallback, AfterEachCallback, AfterAllCallback {


  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TestTimerExtension.class);
  private static final String RAW_TEST_DATA_KEY = "raw-test-data";
  private static final String BEFORE_EACH_START_TIME_KEY = "before-each-start-time";

  private final TestRunAggregator aggregator = new TestRunAggregator();
  private final ReportGenerator reportGenerator = new ReportGenerator();

  private final List<MetricCollector> collectors = List.of(
      new StatusCollector(),
      new RetryCollector(),
      new TagsCollector()
  );

  @Override
  public void beforeEach(ExtensionContext context) {
    context.getStore(NAMESPACE).put(BEFORE_EACH_START_TIME_KEY, System.currentTimeMillis());
  }

  @Override
  public void afterEach(ExtensionContext context) {
    long start = context.getStore(NAMESPACE).remove(BEFORE_EACH_START_TIME_KEY, long.class);
    long duration = System.currentTimeMillis() - start;

    System.out.printf("[TestAnalyzer] %s BeforeEach executed in %d ms%n", context.getDisplayName(), duration);


    TestResult.Builder builder = TestResult.builder()
        .testName(context.getDisplayName())
        .phase("BeforeEach")
        .durationMs(duration);


    collectMetrics(context, builder);
    aggregator.addResult(builder.build());
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    long startTime = System.currentTimeMillis();
    long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    RawTestExecutionData rawData = new RawTestExecutionData(context, startTime, memoryBefore);
    context.getStore(NAMESPACE).put(RAW_TEST_DATA_KEY, rawData);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    RawTestExecutionData rawData = context.getStore(NAMESPACE).remove(RAW_TEST_DATA_KEY, RawTestExecutionData.class);

    long endTime = System.currentTimeMillis();
    long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    long duration = endTime - rawData.getStartTime();

    System.out.printf("[TestAnalyzer] %s Test method executed in %d ms%n", context.getDisplayName(), duration);

    TestResult.Builder builder = TestResult.builder()
        .testName(context.getDisplayName())
        .phase("TestMethod")
        .durationMs(duration)
        .memoryBefore(rawData.getMemoryBefore())
        .memoryAfter(memoryAfter);


    collectMetrics(context, builder);
    aggregator.addResult(builder.build());
  }

  @Override
  public void afterAll(ExtensionContext context) {
    reportGenerator.generateReport(aggregator.getAllResults());
    aggregator.clearResults();
  }

  private void collectMetrics(ExtensionContext context, TestResult.Builder builder) {
    for (MetricCollector collector : collectors) {
      collector.collect(context, builder);
    }
  }
}