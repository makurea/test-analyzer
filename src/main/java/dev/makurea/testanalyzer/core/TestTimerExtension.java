package dev.makurea.testanalyzer.core;

import dev.makurea.testanalyzer.metrics.*;
import dev.makurea.testanalyzer.report.ReportGenerator;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;
import java.util.List;

/*
 * TestTimerExtension
 *
 * `TestTimerExtension` — это основное расширение JUnit 5,
 * которое измеряет и собирает метрики производительности для каждого тестового метода.
 * Оно реализует различные интерфейсы обратного вызова JUnit 5
 * для перехвата событий жизненного цикла теста, таких как начало и конец фаз `BeforeEach`
 * и `TestMethod`.
 *
 * Это расширение фиксирует длительность выполнения, статус, теги,
 * количество повторных попыток и потребление памяти для каждого этапа теста.
 * Собранные данные агрегируются и, по завершении всех тестов,
 * используются для генерации интерактивного HTML-отчёта.
 */

public class TestTimerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback,
    BeforeEachCallback, AfterEachCallback, AfterAllCallback {

  // Пространство имен для хранения данных в ExtensionContext.Store
  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TestTimerExtension.class);
  // Ключ для хранения необработанных данных о выполнении тестового метода
  private static final String RAW_TEST_DATA_KEY = "raw-test-data";
  // Ключ для хранения времени начала фазы BeforeEach
  private static final String BEFORE_EACH_START_TIME_KEY = "before-each-start-time";

  // Агрегатор для сбора всех результатов тестов за один прогон
  private final TestRunAggregator aggregator = new TestRunAggregator();
  // Генератор для создания HTML-отчета
  private final ReportGenerator reportGenerator = new ReportGenerator();

  // Список сборщиков метрик, которые будут собирать дополнительную информацию о тесте
  List<MetricCollector> collectors = Arrays.asList(
      new StatusCollector(), // Собирает статус выполнения теста
      new RetryCollector(),  // Собирает информацию о повторных попытках
      new TagsCollector()    // Собирает теги теста
  );

  /**
   * Вызывается JUnit 5 непосредственно перед выполнением любого метода `@BeforeEach`.
   * Фиксирует текущее системное время, чтобы измерить длительность фазы BeforeEach.
   *
   * @param context Контекст выполнения JUnit, предоставляющий доступ к информации о тесте.
   */
  @Override
  public void beforeEach(ExtensionContext context) {
    context.getStore(NAMESPACE).put(BEFORE_EACH_START_TIME_KEY, System.currentTimeMillis());
  }

  /**
   * Вызывается JUnit 5 сразу после выполнения любого метода `@AfterEach`.
   * Рассчитывает длительность фазы BeforeEach и логирует её.
   * Собирает метрики и добавляет результат фазы BeforeEach в агрегатор.
   *
   * @param context Контекст выполнения JUnit, предоставляющий доступ к информации о тесте.
   */
  @Override
  public void afterEach(ExtensionContext context) {
    long start = context.getStore(NAMESPACE).remove(BEFORE_EACH_START_TIME_KEY, long.class);
    long duration = System.currentTimeMillis() - start;

    System.out.printf("[TestAnalyzer] %s BeforeEach executed in %d ms%n", context.getDisplayName(), duration);

    TestResult.Builder builder = TestResult.builder()
        .testName(context.getDisplayName())
        .phase("BeforeEach") // Указываем фазу "BeforeEach"
        .durationMs(duration);

    collectMetrics(context, builder); // Собираем дополнительные метрики
    aggregator.addResult(builder.build()); // Добавляем построенный результат в агрегатор
  }

  /**
   * Вызывается JUnit 5 непосредственно перед выполнением самого тестового метода (`@Test`).
   * Фиксирует время начала выполнения тестового метода и объем используемой памяти
   * до его старта. Эти данные сохраняются в хранилище контекста.
   *
   * @param context Контекст выполнения JUnit, предоставляющий доступ к информации о тесте.
   */
  @Override
  public void beforeTestExecution(ExtensionContext context) {
    long startTime = System.currentTimeMillis();
    // Рассчитываем используемую память: общая память - свободная память
    long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    RawTestExecutionData rawData = new RawTestExecutionData(context, startTime, memoryBefore);
    context.getStore(NAMESPACE).put(RAW_TEST_DATA_KEY, rawData);
  }

  /**
   * Вызывается JUnit 5 сразу после выполнения тестового метода (`@Test`).
   * Рассчитывает длительность выполнения тестового метода и изменение потребления памяти.
   * Логирует эти данные и добавляет результат фазы TestMethod в агрегатор.
   *
   * @param context Контекст выполнения JUnit, предоставляющий доступ к информации о тесте.
   */
  @Override
  public void afterTestExecution(ExtensionContext context) {
    // Извлекаем необработанные данные, сохраненные перед выполнением теста
    RawTestExecutionData rawData = context.getStore(NAMESPACE).remove(RAW_TEST_DATA_KEY, RawTestExecutionData.class);

    long endTime = System.currentTimeMillis();
    // Рассчитываем используемую память после выполнения
    long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    long duration = endTime - rawData.getStartTime(); // Длительность выполнения метода

    System.out.printf("[TestAnalyzer] %s Test method executed in %d ms%n", context.getDisplayName(), duration);

    TestResult.Builder builder = TestResult.builder()
        .testName(context.getDisplayName())
        .phase("TestMethod") // Указываем фазу "TestMethod"
        .durationMs(duration)
        .memoryBefore(rawData.getMemoryBefore()) // Память до
        .memoryAfter(memoryAfter); // Память после

    collectMetrics(context, builder); // Собираем дополнительные метрики
    aggregator.addResult(builder.build()); // Добавляем построенный результат в агрегатор
  }

  /**
   * Вызывается JUnit 5 один раз после выполнения всех тестов в текущем тестовом классе.
   * Запускает генерацию HTML-отчета на основе всех собранных результатов
   * и очищает агрегатор для следующего возможного запуска.
   *
   * @param context Контекст выполнения JUnit (на уровне класса).
   */
  @Override
  public void afterAll(ExtensionContext context) {
    reportGenerator.generateReport(aggregator.getAllResults()); // Генерируем отчет
    aggregator.clearResults(); // Очищаем агрегатор
  }

  /**
   * Вспомогательный метод для сбора дополнительных метрик с помощью зарегистрированных `MetricCollector`'ов.
   * Каждый сборщик добавляет свою специфическую информацию в Builder.
   *
   * @param context Контекст выполнения JUnit.
   * @param builder Builder {@link TestResult}, в который будут добавлены метрики.
   */
  private void collectMetrics(ExtensionContext context, TestResult.Builder builder) {
    for (MetricCollector collector : collectors) {
      collector.collect(context, builder);
    }
  }
}