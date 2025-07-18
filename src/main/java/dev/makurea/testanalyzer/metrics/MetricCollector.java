package dev.makurea.testanalyzer.metrics;

import dev.makurea.testanalyzer.core.TestResult;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface MetricCollector {
  void collect(ExtensionContext context, TestResult.Builder builder);
}

