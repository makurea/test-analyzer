package dev.makurea.testanalyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

  private static final String OUTPUT_DIR = "build/test-analyzer-reports";
  private static final String JSON_FILENAME = "test-timer-results.json";
  private static final String HTML_FILENAME = "test-timer-report.html";

  public void generateReport(List<TestTimerExtension.TestResult> results) {
    try {
      File dir = new File(OUTPUT_DIR);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      File jsonFile = new File(dir, JSON_FILENAME);
      ObjectMapper mapper = new ObjectMapper();
      mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, results);

      File htmlFile = new File(dir, HTML_FILENAME);
      try (FileWriter writer = new FileWriter(htmlFile)) {
        writer.write(generateHtmlContent(JSON_FILENAME));
      }

      System.out.println("[TestTimer] Отчёт создан: " + htmlFile.getAbsolutePath());

    } catch (IOException e) {
      System.err.println("[TestTimer] Ошибка при создании отчёта");
      e.printStackTrace();
    }
  }
  private String generateHtmlContent(String jsonFileName) {
    return
        "<!DOCTYPE html>\n" +
            "<html lang=\"ru\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\" />\n" +
            "    <title>Test Timer Report</title>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
            "    <style>\n" +
            "        body { font-family: Arial, sans-serif; margin: 40px; }\n" +
            "        h1 { text-align: center; }\n" +
            "        canvas { max-width: 900px; margin: auto; display: block; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>Отчёт по времени тестов</h1>\n" +
            "    <canvas id=\"chart\"></canvas>\n" +
            "    <script>\n" +
            "        fetch(\"" + jsonFileName + "\")\n" +
            "            .then(response => response.json())\n" +
            "            .then(data => {\n" +
            "                const labels = [];\n" +
            "                const beforeEachData = [];\n" +
            "                const testMethodData = [];\n" +
            "\n" +
            "                const grouped = {};\n" +
            "                data.forEach(item => {\n" +
            "                    if (!grouped[item.testName]) {\n" +
            "                        grouped[item.testName] = { beforeEach: 0, testMethod: 0 };\n" +
            "                    }\n" +
            "                    if (item.phase === 'BeforeEach') {\n" +
            "                        grouped[item.testName].beforeEach += item.durationMs;\n" +
            "                    } else if (item.phase === 'TestMethod') {\n" +
            "                        grouped[item.testName].testMethod += item.durationMs;\n" +
            "                    }\n" +
            "                });\n" +
            "\n" +
            "                for (const testName in grouped) {\n" +
            "                    labels.push(testName);\n" +
            "                    beforeEachData.push(grouped[testName].beforeEach);\n" +
            "                    testMethodData.push(grouped[testName].testMethod);\n" +
            "                }\n" +
            "\n" +
            "                const ctx = document.getElementById('chart').getContext('2d');\n" +
            "                new Chart(ctx, {\n" +
            "                    type: 'bar',\n" +
            "                    data: {\n" +
            "                        labels: labels,\n" +
            "                        datasets: [\n" +
            "                            {\n" +
            "                                label: 'BeforeEach, мс',\n" +
            "                                data: beforeEachData,\n" +
            "                                backgroundColor: 'rgba(54, 162, 235, 0.7)'\n" +
            "                            },\n" +
            "                            {\n" +
            "                                label: 'TestMethod, мс',\n" +
            "                                data: testMethodData,\n" +
            "                                backgroundColor: 'rgba(255, 99, 132, 0.7)'\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    options: {\n" +
            "                        responsive: true,\n" +
            "                        scales: {\n" +
            "                            y: {\n" +
            "                                beginAtZero: true,\n" +
            "                                title: {\n" +
            "                                    display: true,\n" +
            "                                    text: 'Время (миллисекунды)'\n" +
            "                                }\n" +
            "                            },\n" +
            "                            x: {\n" +
            "                                title: {\n" +
            "                                    display: true,\n" +
            "                                    text: 'Название теста'\n" +
            "                                }\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                });\n" +
            "            })\n" +
            "            .catch(err => {\n" +
            "                document.body.innerHTML = '<h2>Ошибка загрузки данных отчёта</h2><pre>' + err + '</pre>';\n" +
            "            });\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>\n";
  }
}
