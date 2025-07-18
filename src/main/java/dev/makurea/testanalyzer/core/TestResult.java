package dev.makurea.testanalyzer.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class TestResult {
  private final String testName;
  private final String phase;
  private final long durationMs;
  private final String status;
  private final int retryCount;
  private final List<String> tags;
  private final boolean isNegative;
  private final long memoryBefore;
  private final long memoryAfter;

  private TestResult(Builder builder) {
    this.testName = Objects.requireNonNull(builder.testName, "Test name cannot be null");
    this.phase = Objects.requireNonNull(builder.phase, "Phase cannot be null");
    this.durationMs = builder.durationMs;
    this.status = builder.status;
    this.retryCount = builder.retryCount;
    this.tags = builder.tags != null ? Collections.unmodifiableList(builder.tags) : Collections.emptyList();
    this.isNegative = builder.isNegative;
    this.memoryBefore = builder.memoryBefore;
    this.memoryAfter = builder.memoryAfter;
  }

  public static Builder builder() {
    return new Builder();
  }

  // Геттеры
  public String getTestName() {
    return testName;
  }

  public String getPhase() {
    return phase;
  }

  public long getDurationMs() {
    return durationMs;
  }

  public String getStatus() {
    return status;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public List<String> getTags() {
    return tags;
  }

  public boolean isNegative() {
    return isNegative;
  }

  public long getMemoryBefore() {
    return memoryBefore;
  }

  public long getMemoryAfter() {
    return memoryAfter;
  }

  // Паттерн Builder
  public static class Builder {
    private String testName;
    private String phase;
    private long durationMs;
    private String status = "UNKNOWN"; // Default status
    private int retryCount = 0;
    private List<String> tags;
    private boolean isNegative = false;
    private long memoryBefore = 0;
    private long memoryAfter = 0;

    private Builder() {}

    public Builder testName(String testName) {
      this.testName = testName;
      return this;
    }

    public Builder phase(String phase) {
      this.phase = phase;
      return this;
    }

    public Builder durationMs(long durationMs) {
      this.durationMs = durationMs;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Builder retryCount(int retryCount) {
      this.retryCount = retryCount;
      return this;
    }

    public Builder tags(List<String> tags) {
      this.tags = tags;
      return this;
    }

    public Builder isNegative(boolean isNegative) {
      this.isNegative = isNegative;
      return this;
    }

    public Builder memoryBefore(long memoryBefore) {
      this.memoryBefore = memoryBefore;
      return this;
    }

    public Builder memoryAfter(long memoryAfter) {
      this.memoryAfter = memoryAfter;
      return this;
    }

    public TestResult build() {
      return new TestResult(this);
    }
  }
}