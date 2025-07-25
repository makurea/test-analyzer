package dev.makurea.testanalyzer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Класс `TestRunAggregator` предназначен для сбора и управления
 * всеми результатами выполнения тестов (`TestResult`) в рамках одного тестового запуска.
 * Он действует как централизованное хранилище для метрик каждого тестового метода
 * и связанных с ним фаз (например, BeforeEach, TestMethod).
 *
 * Этот агрегатор обеспечивает добавление новых результатов и предоставление
 * неизменяемого списка всех собранных результатов, что гарантирует целостность данных.
 * Также предусмотрена возможность очистки всех собранных результатов.
 */
public class TestRunAggregator {
  private final List<TestResult> results;

  /**
   * Конструктор для создания нового экземпляра `TestRunAggregator`.
   * Инициализирует внутренний список для хранения результатов тестов.
   */
  public TestRunAggregator() {
    this.results = new ArrayList<>();
  }

  /**
   * Добавляет один результат выполнения теста в агрегатор.
   * Если переданный результат равен null, он игнорируется.
   *
   * @param result Объект {@link TestResult}, содержащий метрики выполненной фазы теста.
   */
  public void addResult(TestResult result) {
    if (result != null) {
      this.results.add(result);
    }
  }

  /**
   * Возвращает неизменяемый список всех собранных результатов тестов.
   * Это предотвращает внешние модификации списка результатов, обеспечивая безопасность данных.
   *
   * @return Неизменяемый {@link List} объектов {@link TestResult}.
   */
  public List<TestResult> getAllResults() {
    return Collections.unmodifiableList(results);
  }

  /**
   * Очищает все собранные результаты тестов из агрегатора.
   * Этот метод полезен для подготовки агрегатора к новому тестовому прогону.
   */
  public void clearResults() {
    this.results.clear();
  }
}