package dev.makurea.testanalyzer.metrics;

import java.util.HashMap;
import java.util.Map;

public class RetryAnalyzerRegistry {

  private static final Map<String, Integer> retriesMap = new HashMap<>();

  public static void registerRetry(String methodName) {
    retriesMap.put(methodName, retriesMap.getOrDefault(methodName, 0) + 1);
  }

  public static int getRetries(String methodName) {
    return retriesMap.getOrDefault(methodName, 0);
  }

  public static void reset() {
    retriesMap.clear();
  }
}
