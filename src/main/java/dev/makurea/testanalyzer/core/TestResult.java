package dev.makurea.testanalyzer.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*
 *
 * Immutable-класс `TestResult` предназначен для хранения всех собранных метрик
 * и атрибутов, связанных с выполнением одного этапа (фазы) теста.
 * Он инкапсулирует такие данные, как имя теста, фаза выполнения (например, BeforeEach, TestMethod),
 * длительность, статус, количество повторных попыток, связанные теги,
 * а также потребление памяти до и после выполнения.
 *
 * Этот класс использует паттерн "Строитель" (Builder) для удобного и безопасного создания своих экземпляров.
 * Это позволяет создавать объекты `TestResult` с большим количеством параметров,
 * делая код более читаемым и менее подверженным ошибкам,
 * так как параметры можно устанавливать в любом порядке, а обязательные поля
 * проверяются при вызове метода `build()`.
 */
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

  /**
   * Приватный конструктор для создания `TestResult` через Builder.
   * Валидирует обязательные поля.
   *
   * @param builder Экземпляр Builder, содержащий все установленные поля.
   * @throws NullPointerException если testName или phase равны null.
   */
  private TestResult(Builder builder) {
    this.testName = Objects.requireNonNull(builder.testName, "Test name cannot be null");
    this.phase = Objects.requireNonNull(builder.phase, "Phase cannot be null");
    this.durationMs = builder.durationMs;
    this.status = builder.status;
    this.retryCount = builder.retryCount;
    // Создаем неизменяемый список тегов, если он не null, иначе пустой список.
    this.tags = builder.tags != null ? Collections.unmodifiableList(builder.tags) : Collections.emptyList();
    this.isNegative = builder.isNegative;
    this.memoryBefore = builder.memoryBefore;
    this.memoryAfter = builder.memoryAfter;
  }

  /**
   * Возвращает новый экземпляр Builder для создания `TestResult`.
   *
   * @return Новый {@link Builder} для `TestResult`.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Возвращает имя теста.
   *
   * @return Имя теста.
   */
  public String getTestName() {
    return testName;
  }

  /**
   * Возвращает фазу выполнения теста (например, "BeforeEach" или "TestMethod").
   *
   * @return Фаза выполнения теста.
   */
  public String getPhase() {
    return phase;
  }

  /**
   * Возвращает длительность выполнения фазы теста в миллисекундах.
   *
   * @return Длительность в мс.
   */
  public long getDurationMs() {
    return durationMs;
  }

  /**
   * Возвращает статус выполнения фазы теста (например, "PASSED", "FAILED", "SKIPPED", "UNKNOWN").
   *
   * @return Статус выполнения.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Возвращает количество повторных попыток выполнения теста.
   *
   * @return Количество повторных попыток.
   */
  public int getRetryCount() {
    return retryCount;
  }

  /**
   * Возвращает неизменяемый список тегов, связанных с тестом.
   *
   * @return {@link List} тегов.
   */
  public List<String> getTags() {
    return tags;
  }

  /**
   * Проверяет, является ли тест негативным сценарием.
   * (В данной версии функционал может быть не реализован полностью)
   *
   * @return true, если тест негативный; false в противном случае.
   */
  public boolean isNegative() {
    return isNegative;
  }

  /**
   * Возвращает объем используемой памяти в байтах до выполнения данной фазы теста.
   *
   * @return Объем памяти до.
   */
  public long getMemoryBefore() {
    return memoryBefore;
  }

  /**
   * Возвращает объем используемой памяти в байтах после выполнения данной фазы теста.
   *
   * @return Объем памяти после.
   */
  public long getMemoryAfter() {
    return memoryAfter;
  }

  /**
   * Builder-класс для создания экземпляров `TestResult`.
   * Позволяет последовательно устанавливать параметры и создавать объект `TestResult`.
   */
  public static class Builder {
    private String testName;
    private String phase;
    private long durationMs;
    private String status = "UNKNOWN"; // Значение по умолчанию
    private int retryCount = 0; // Значение по умолчанию
    private List<String> tags;
    private boolean isNegative = false; // Значение по умолчанию
    private long memoryBefore = 0; // Значение по умолчанию
    private long memoryAfter = 0; // Значение по умолчанию

    /**
     * Приватный конструктор для Builder.
     * Доступен только через статический метод `TestResult.builder()`.
     */
    private Builder() {}

    /**
     * Устанавливает имя теста.
     *
     * @param testName Имя теста.
     * @return Текущий экземпляр Builder.
     */
    public Builder testName(String testName) {
      this.testName = testName;
      return this;
    }

    /**
     * Устанавливает фазу выполнения теста (например, "BeforeEach" или "TestMethod").
     *
     * @param phase Фаза выполнения теста.
     * @return Текущий экземпляр Builder.
     */
    public Builder phase(String phase) {
      this.phase = phase;
      return this;
    }

    /**
     * Устанавливает длительность выполнения фазы теста в миллисекундах.
     *
     * @param durationMs Длительность в мс.
     * @return Текущий экземпляр Builder.
     */
    public Builder durationMs(long durationMs) {
      this.durationMs = durationMs;
      return this;
    }

    /**
     * Устанавливает статус выполнения фазы теста.
     *
     * @param status Статус выполнения.
     * @return Текущий экземпляр Builder.
     */
    public Builder status(String status) {
      this.status = status;
      return this;
    }

    /**
     * Устанавливает количество повторных попыток выполнения теста.
     *
     * @param retryCount Количество повторных попыток.
     * @return Текущий экземпляр Builder.
     */
    public Builder retryCount(int retryCount) {
      this.retryCount = retryCount;
      return this;
    }

    /**
     * Устанавливает список тегов, связанных с тестом.
     *
     * @param tags {@link List} тегов.
     * @return Текущий экземпляр Builder.
     */
    public Builder tags(List<String> tags) {
      this.tags = tags;
      return this;
    }

    /**
     * Устанавливает, является ли тест негативным сценарием.
     *
     * @param isNegative true, если тест негативный; false в противном случае.
     * @return Текущий экземпляр Builder.
     */
    public Builder isNegative(boolean isNegative) {
      this.isNegative = isNegative;
      return this;
    }

    /**
     * Устанавливает объем используемой памяти в байтах до выполнения данной фазы теста.
     *
     * @param memoryBefore Объем памяти до.
     * @return Текущий экземпляр Builder.
     */
    public Builder memoryBefore(long memoryBefore) {
      this.memoryBefore = memoryBefore;
      return this;
    }

    /**
     * Устанавливает объем используемой памяти в байтах после выполнения данной фазы теста.
     *
     * @param memoryAfter Объем памяти после.
     * @return Текущий экземпляр Builder.
     */
    public Builder memoryAfter(long memoryAfter) {
      this.memoryAfter = memoryAfter;
      return this;
    }

    /**
     * Строит и возвращает новый экземпляр `TestResult` с заданными параметрами.
     * Выполняет валидацию обязательных полей.
     *
     * @return Новый объект {@link TestResult}.
     */
    public TestResult build() {
      return new TestResult(this);
    }
  }
}