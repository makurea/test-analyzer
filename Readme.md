# test-analyzer

Java библиотека для измерения и логирования времени выполнения тестов с помощью JUnit 5 Extension.

---

## Что это?

`test-analyzer` — это лёгкая и удобная библиотека, которая позволяет автоматически измерять время выполнения каждого теста в JUnit 5 и выводить эту информацию в консоль. Это помогает быстро выявлять медленные тесты и улучшать качество автотестов.

---

## Как использовать?

### 1. Подключите зависимость к своему проекту

(Пока библиотека не опубликована в Maven Central, подключайте локально или через JitPack)

```groovy
// build.gradle
dependencies {
    implementation 'dev.makurea:test-analyzer:0.1.0'
}
```
### 2. Добавьте расширение к вашим тестам
```java
   import dev.makurea.testanalyzer.TestTimerExtension;
   import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestTimerExtension.class)
public class YourTestClass {

    @Test
    void exampleTest() throws InterruptedException {
        Thread.sleep(200);
        Assertions.assertTrue(true);
    }
}
```

### 3. Запустите тесты и смотрите вывод в консоли
```scss
   [TestTimer] exampleTest() executed in 200 ms
```

###  Как собрать и подключить библиотеку локально?
   Клонируйте репозиторий:

```bash
git clone https://github.com/makurea/test-analyzer.git
cd test-analyzer
```

### Соберите библиотеку:
```bash
./gradlew build
```
Подключите локальную библиотеку в другом проекте, указав путь к .jar из build/libs

### Планы на будущее

 - Сохранение логов в JSON-файл

 - Генерация HTML-отчётов

 - Интеграция с CI-системами

 - Анализ flaky-тестов

### Лицензия
MIT License © makurea

