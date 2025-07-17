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
            "        body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; color: #333; }\n" +
            "        body.dark-mode { background-color: #2c2c2c; color: #f0f0f0; }\n" +
            "        h1 { text-align: center; color: #0056b3; margin-bottom: 30px; }\n" +
            "        body.dark-mode h1 { color: #87cefa; }\n" +
            "        .container { max-width: 1000px; margin: auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); }\n" +
            "        body.dark-mode .container { background: #3c3c3c; box-shadow: 0 0 15px rgba(0,0,0,0.3); }\n" +
            "        .summary { text-align: center; margin-bottom: 20px; font-size: 1.1em; font-weight: bold; }\n" +
            "        .controls { display: flex; justify-content: center; gap: 10px; margin-bottom: 20px; flex-wrap: wrap; }\n" +
            "        .controls button, .controls input { padding: 10px 15px; border-radius: 5px; border: 1px solid #ccc; cursor: pointer; background-color: #f0f0f0; }\n" +
            "        body.dark-mode .controls button, body.dark-mode .controls input { background-color: #555; color: #f0f0f0; border-color: #777; }\n" +
            "        .controls button:hover { background-color: #e0e0e0; }\n" +
            "        body.dark-mode .controls button:hover { background-color: #666; }\n" +
            "        #searchInput { width: 300px; }\n" +
            "        table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n" +
            "        th, td { padding: 12px 15px; border: 1px solid #ddd; text-align: left; }\n" +
            "        th { background-color: #f2f2f2; font-weight: bold; }\n" +
            "        body.dark-mode th { background-color: #4a4a4a; }\n" +
            "        .duration-short { background-color: #e6ffe6; }\n" +
            "        body.dark-mode .duration-short { background-color: #284a28; }\n" +
            "        .duration-medium { background-color: #fffacd; }\n" +
            "        body.dark-mode .duration-medium { background-color: #5a5a2a; }\n" +
            "        .duration-long { background-color: #ffe6e6; }\n" +
            "        body.dark-mode .duration-long { background-color: #6a2a2a; }\n" +
            "        /* Изменения для размера графика */\n" +
            "        canvas {\n" +
            "            max-width: 600px; /* Уменьшаем максимальную ширину */\n" +
            "            max-height: 400px; /* Добавляем максимальную высоту */\n" +
            "            width: 100%; /* Сохраняем адаптивность */\n" +
            "            height: auto; /* Сохраняем пропорции */\n" +
            "            margin: 40px auto; \n" +
            "            display: block; \n" +
            "            border: 1px solid #eee; \n" +
            "            border-radius: 8px; \n" +
            "            background: #fff; \n" +
            "        }\n" +
            "        body.dark-mode canvas { background: #3c3c3c; border-color: #555; }\n" +
            "        #themeToggle { position: absolute; top: 20px; right: 20px; padding: 8px 12px; font-size: 1em; }\n" +
            "        .json-download { text-align: center; margin-top: 30px; }\n" +
            "        .json-download button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 1em; }\n" +
            "        .json-download button:hover { background-color: #45a049; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <button id=\"themeToggle\">Переключить тему</button>\n" +
            "    <div class=\"container\">\n" +
            "        <h1>Отчёт по времени тестов</h1>\n" +
            "        <p class=\"summary\" id=\"totalTime\"></p>\n" +
            "\n" +
            "        <div class=\"controls\">\n" +
            "            <input type=\"text\" id=\"searchInput\" placeholder=\"Поиск по имени теста...\" />\n" +
            "            <button onclick=\"sortTests('duration')\">Сортировать по времени</button>\n" +
            "            <button onclick=\"sortTests('name')\">Сортировать по имени</button>\n" +
            "        </div>\n" +
            "\n" +
            "        <table>\n" +
            "            <thead>\n" +
            "                <tr>\n" +
            "                    <th>Имя теста</th>\n" +
            "                    <th>Фаза</th>\n" +
            "                    <th>Длительность (мс)</th>\n" +
            "                </tr>\n" +
            "            </thead>\n" +
            "            <tbody id=\"testResultsTableBody\">\n" +
            "            </tbody>\n" +
            "        </table>\n" +
            "\n" +
            "        <h2>График длительности тестов</h2>\n" +
            "        <canvas id=\"chart\"></canvas>\n" +
            "\n" +
            "        <div class=\"json-download\">\n" +
            "            <button onclick=\"downloadJson()\">Скачать JSON отчёт</button>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "\n" +
            "    <script>\n" +
            "        let allTestData = []; // Store original data\n" +
            "\n" +
            "        // Theme toggle\n" +
            "        const themeToggle = document.getElementById('themeToggle');\n" +
            "        const currentTheme = localStorage.getItem('theme');\n" +
            "        if (currentTheme === 'dark') {\n" +
            "            document.body.classList.add('dark-mode');\n" +
            "        } else if (currentTheme === 'light') {\n" +
            "             document.body.classList.remove('dark-mode');\n" +
            "        } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {\n" +
            "            document.body.classList.add('dark-mode');\n" +
            "        }\n" +
            "\n" +
            "        themeToggle.addEventListener('click', () => {\n" +
            "            document.body.classList.toggle('dark-mode');\n" +
            "            if (document.body.classList.contains('dark-mode')) {\n" +
            "                localStorage.setItem('theme', 'dark');\n" +
            "            } else {\n" +
            "                localStorage.setItem('theme', 'light');\n" +
            "            }\n" +
            "            updateChartColors(); // Update chart colors on theme change\n" +
            "        });\n" +
            "\n" +
            "        function getDurationColorClass(duration) {\n" +
            "            if (duration < 500) {\n" +
            "                return 'duration-short';\n" +
            "            } else if (duration >= 500 && duration <= 1000) {\n" +
            "                return 'duration-medium';\n" +
            "            } else {\n" +
            "                return 'duration-long';\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        function renderTable(dataToRender) {\n" +
            "            const tableBody = document.getElementById('testResultsTableBody');\n" +
            "            tableBody.innerHTML = ''; // Clear existing rows\n" +
            "            let totalDuration = 0;\n" +
            "\n" +
            "            // Group by test name and sum durations for BeforeEach and TestMethod\n" +
            "            const grouped = {};\n" +
            "            dataToRender.forEach(item => {\n" +
            "                if (!grouped[item.testName]) {\n" +
            "                    grouped[item.testName] = { beforeEach: 0, testMethod: 0 };\n" +
            "                }\n" +
            "                if (item.phase === 'BeforeEach') {\n" +
            "                    grouped[item.testName].beforeEach += item.durationMs;\n" +
            "                } else if (item.phase === 'TestMethod') {\n" +
            "                    grouped[item.testName].testMethod += item.durationMs;\n" +
            "                }\n" +
            "            });\n" +
            "\n" +
            "            // Create a flat array for rendering and sorting, with combined durations\n" +
            "            const combinedTests = [];\n" +
            "            for (const testName in grouped) {\n" +
            "                const beforeEachDuration = grouped[testName].beforeEach;\n" +
            "                const testMethodDuration = grouped[testName].testMethod;\n" +
            "                const totalTestDuration = beforeEachDuration + testMethodDuration;\n" +
            "                totalDuration += totalTestDuration;\n" +
            "\n" +
            "                combinedTests.push({\n" +
            "                    testName: testName,\n" +
            "                    phase: 'BeforeEach',\n" +
            "                    durationMs: beforeEachDuration,\n" +
            "                    totalTestDuration: totalTestDuration // Store combined for sorting\n" +
            "                });\n" +
            "                combinedTests.push({\n" +
            "                    testName: testName,\n" +
            "                    phase: 'TestMethod',\n" +
            "                    durationMs: testMethodDuration,\n" +
            "                    totalTestDuration: totalTestDuration // Store combined for sorting\n" +
            "                });\n" +
            "            }\n" +
            "\n" +
            "            // Sort combinedTests by totalTestDuration descending by default for display\n" +
            "            combinedTests.sort((a, b) => b.totalTestDuration - a.totalTestDuration);\n" +
            "\n" +
            "            combinedTests.forEach(item => {\n" +
            "                const row = tableBody.insertRow();\n" +
            "                const colorClass = getDurationColorClass(item.durationMs);\n" +
            "                row.classList.add(colorClass);\n" +
            "\n" +
            "                row.insertCell().textContent = item.testName;\n" +
            "                row.insertCell().textContent = item.phase;\n" +
            "                row.insertCell().textContent = item.durationMs;\n" +
            "            });\n" +
            "\n" +
            "            document.getElementById('totalTime').textContent = `Всего времени на выполнение тестов: ${totalDuration} мс`;\n" +
            "        }\n" +
            "\n" +
            "        let myChart;\n" +
            "\n" +
            "        function renderChart(data) {\n" +
            "            const labels = [];\n" +
            "            const beforeEachData = [];\n" +
            "            const testMethodData = [];\n" +
            "            const totalDurations = {}; // To store total duration per testName for sorting\n" +
            "\n" +
            "            const grouped = {};\n" +
            "            data.forEach(item => {\n" +
            "                if (!grouped[item.testName]) {\n" +
            "                    grouped[item.testName] = { beforeEach: 0, testMethod: 0 };\n" +
            "                }\n" +
            "                if (item.phase === 'BeforeEach') {\n" +
            "                    grouped[item.testName].beforeEach += item.durationMs;\n" +
            "                } else if (item.phase === 'TestMethod') {\n" +
            "                    grouped[item.testName].testMethod += item.durationMs;\n" +
            "                }\n" +
            "            });\n" +
            "\n" +
            "            for (const testName in grouped) {\n" +
            "                totalDurations[testName] = grouped[testName].beforeEach + grouped[testName].testMethod;\n" +
            "            }\n" +
            "\n" +
            "            // Sort test names based on total duration descending for the chart\n" +
            "            const sortedTestNames = Object.keys(totalDurations).sort((a, b) => totalDurations[b] - totalDurations[a]);\n" +
            "\n" +
            "            sortedTestNames.forEach(testName => {\n" +
            "                labels.push(testName);\n" +
            "                beforeEachData.push(grouped[testName].beforeEach);\n" +
            "                testMethodData.push(grouped[testName].testMethod);\n" +
            "            });\n" +
            "\n" +
            "            const ctx = document.getElementById('chart').getContext('2d');\n" +
            "            if (myChart) {\n" +
            "                myChart.destroy(); // Destroy previous chart instance\n" +
            "            }\n" +
            "            myChart = new Chart(ctx, {\n" +
            "                type: 'bar',\n" +
            "                data: {\n" +
            "                    labels: labels,\n" +
            "                    datasets: [\n" +
            "                        {\n" +
            "                            label: 'BeforeEach, мс',\n" +
            "                            data: beforeEachData,\n" +
            "                            backgroundColor: 'rgba(54, 162, 235, 0.7)'\n" +
            "                        },\n" +
            "                        {\n" +
            "                            label: 'TestMethod, мс',\n" +
            "                            data: testMethodData,\n" +
            "                            backgroundColor: 'rgba(255, 99, 132, 0.7)'\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                options: {\n" +
            "                    responsive: true,\n" +
            "                    maintainAspectRatio: false, // Отключаем поддержание аспектного соотношения по умолчанию\n" +
            "                    scales: {\n" +
            "                        y: {\n" +
            "                            beginAtZero: true,\n" +
            "                            title: {\n" +
            "                                display: true,\n" +
            "                                text: 'Время (миллисекунды)',\n" +
            "                                color: document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333'\n" +
            "                            },\n" +
            "                            ticks: {\n" +
            "                                color: document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333'\n" +
            "                            },\n" +
            "                            grid: {\n" +
            "                                color: document.body.classList.contains('dark-mode') ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)'\n" +
            "                            }\n" +
            "                        },\n" +
            "                        x: {\n" +
            "                            title: {\n" +
            "                                display: true,\n" +
            "                                text: 'Название теста',\n" +
            "                                color: document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333'\n" +
            "                            },\n" +
            "                            ticks: {\n" +
            "                                color: document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333'\n" +
            "                            },\n" +
            "                            grid: {\n" +
            "                                color: document.body.classList.contains('dark-mode') ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)'\n" +
            "                            }\n" +
            "                        }\n" +
            "                    },\n" +
            "                    plugins: {\n" +
            "                        legend: {\n" +
            "                            labels: {\n" +
            "                                color: document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333'\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                }\n" +
            "            });\n" +
            "        }\n" +
            "\n" +
            "        function updateChartColors() {\n" +
            "            if (myChart) {\n" +
            "                myChart.options.scales.y.title.color = document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333';\n" +
            "                myChart.options.scales.y.ticks.color = document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333';\n" +
            "                myChart.options.scales.y.grid.color = document.body.classList.contains('dark-mode') ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)';\n" +
            "                myChart.options.scales.x.title.color = document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333';\n" +
            "                myChart.options.scales.x.ticks.color = document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333';\n" +
            "                myChart.options.scales.x.grid.color = document.body.classList.contains('dark-mode') ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)';\n" +
            "                myChart.options.plugins.legend.labels.color = document.body.classList.contains('dark-mode') ? '#f0f0f0' : '#333';\n" +
            "                myChart.update();\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        fetch(\"" + jsonFileName + "\")\n" +
            "            .then(response => response.json())\n" +
            "            .then(data => {\n" +
            "                allTestData = data;\n" +
            "                renderTable(allTestData);\n" +
            "                renderChart(allTestData);\n" +
            "            })\n" +
            "            .catch(err => {\n" +
            "                document.body.innerHTML = '<h2>Ошибка загрузки данных отчёта</h2><pre>' + err + '</pre>';\n" +
            "            });\n" +
            "\n" +
            "        function sortTests(criteria) {\n" +
            "            let sortedData = [...allTestData]; // Use a copy\n" +
            "            if (criteria === 'duration') {\n" +
            "                // Group to calculate total duration for sorting\n" +
            "                const groupedDurations = {};\n" +
            "                allTestData.forEach(item => {\n" +
            "                    if (!groupedDurations[item.testName]) {\n" +
            "                        groupedDurations[item.testName] = 0;\n" +
            "                    }\n" +
            "                    groupedDurations[item.testName] += item.durationMs;\n" +
            "                });\n" +
            "\n" +
            "                // Sort the original data based on the calculated total durations\n" +
            "                sortedData.sort((a, b) => {\n" +
            "                    return groupedDurations[b.testName] - groupedDurations[a.testName];\n" +
            "                });\n" +
            "            } else if (criteria === 'name') {\n" +
            "                sortedData.sort((a, b) => a.testName.localeCompare(b.testName));\n" +
            "            }\n" +
            "            renderTable(sortedData);\n" +
            "            renderChart(sortedData);\n" +
            "        }\n" +
            "\n" +
            "        document.getElementById('searchInput').addEventListener('keyup', function() {\n" +
            "            const searchTerm = this.value.toLowerCase();\n" +
            "            const filteredData = allTestData.filter(item => \n" +
            "                item.testName.toLowerCase().includes(searchTerm)\n" +
            "            );\n" +
            "            renderTable(filteredData);\n" +
            "            renderChart(filteredData);\n" +
            "        });\n" +
            "\n" +
            "        function downloadJson() {\n" +
            "            fetch(\"" + jsonFileName + "\")\n" +
            "                .then(response => response.json())\n" +
            "                .then(data => {\n" +
            "                    const jsonString = JSON.stringify(data, null, 2);\n" +
            "                    const blob = new Blob([jsonString], { type: 'application/json' });\n" +
            "                    const url = URL.createObjectURL(blob);\n" +
            "                    const a = document.createElement('a');\n" +
            "                    a.href = url;\n" +
            "                    a.download = 'test-timer-report.json';\n" +
            "                    document.body.appendChild(a);\n" +
            "                    a.click();\n" +
            "                    document.body.removeChild(a);\n" +
            "                    URL.revokeObjectURL(url);\n" +
            "                })\n" +
            "                .catch(err => {\n" +
            "                    alert('Ошибка при скачивании JSON: ' + err);\n" +
            "                });\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>\n";
  }
}