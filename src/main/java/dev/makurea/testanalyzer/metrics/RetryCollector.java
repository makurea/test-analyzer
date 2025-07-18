package dev.makurea.testanalyzer.metrics;

import dev.makurea.testanalyzer.core.TestResult;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RetryCollector implements MetricCollector {

  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(RetryCollector.class);
  private static final String RETRY_COUNT_KEY_PREFIX = "retry-count-";

  @Override
  public void collect(ExtensionContext context, TestResult.Builder builder) {
    String uniqueMethodId = context.getRequiredTestMethod().getName() + "_" + context.getUniqueId();
    ExtensionContext.Store store = context.getStore(NAMESPACE);

    Integer currentRetryCount = store.get(RETRY_COUNT_KEY_PREFIX + uniqueMethodId, int.class);
    int retryCount = (currentRetryCount != null) ? currentRetryCount : 0;

    if (context.getExecutionException().isPresent()) {
      store.put(RETRY_COUNT_KEY_PREFIX + uniqueMethodId, retryCount + 1);
    } else {
      store.put(RETRY_COUNT_KEY_PREFIX + uniqueMethodId, 0);
    }

    builder.retryCount(retryCount);
  }
}