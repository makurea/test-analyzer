package dev.makurea.testanalyzer.core;

import org.junit.jupiter.api.extension.ExtensionContext;

/*
 * Класс `RawTestExecutionData` предназначен для инкапсуляции и передачи
 * исходных данных о начале выполнения тестового метода.
 * Он используется расширением JUnit 5 для временного хранения контекста выполнения,
 * времени начала теста и объема используемой памяти до его старта.
 * Эти данные являются фундаментальными для последующего расчета
 * общей длительности выполнения тестового метода и измерения изменения потребления памяти
 * по завершении теста.
 */

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