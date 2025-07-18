package dev.makurea.testanalyzer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestRunAggregator {
  private final List<TestResult> results;

  public TestRunAggregator() {
    this.results = new ArrayList<>();
  }

  public void addResult(TestResult result) {
    if (result != null) {
      this.results.add(result);
    }
  }

  public List<TestResult> getAllResults() {
    return Collections.unmodifiableList(results);
  }

  public void clearResults() {
    this.results.clear();
  }
}
