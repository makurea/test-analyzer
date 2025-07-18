package dev.makurea.testanalyzer.core;

import org.junit.jupiter.api.extension.ExtensionContext;


public class RawTestExecutionData {
  private final ExtensionContext extensionContext;
  private final long startTime;
  private final long memoryBefore;

  public RawTestExecutionData(ExtensionContext extensionContext, long startTime, long memoryBefore) {
    this.extensionContext = extensionContext;
    this.startTime = startTime;
    this.memoryBefore = memoryBefore;
  }

  public ExtensionContext getExtensionContext() {
    return extensionContext;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getMemoryBefore() {
    return memoryBefore;
  }
}