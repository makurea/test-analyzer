package dev.makurea.testanalyzer.metrics;

import dev.makurea.testanalyzer.core.TestResult;
import org.junit.jupiter.api.extension.ExtensionContext;

public class StatusCollector implements MetricCollector {

  @Override
  public void collect(ExtensionContext context, TestResult.Builder builder) {
    boolean failed = context.getExecutionException().isPresent();
    builder.status(failed ? "FAILED" : "PASSED");
  }
}