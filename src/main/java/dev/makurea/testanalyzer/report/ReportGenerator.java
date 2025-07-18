package dev.makurea.testanalyzer.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makurea.testanalyzer.core.TestResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class ReportGenerator {

  private static final String OUTPUT_DIR = "build/test-analyzer-reports";
  private static final String JSON_FILENAME = "test-analyzer-results.json";
  private static final String HTML_FILENAME = "test-analyzer-report.html";
  private static final String HTML_TEMPLATE_PATH = "report-template.html";

  public void generateReport(List<TestResult> results) {
    try {
      File dir = new File(OUTPUT_DIR);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      File jsonFile = new File(dir, JSON_FILENAME);
      ObjectMapper mapper = new ObjectMapper();
      mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, results);

      File htmlFile = new File(dir, HTML_FILENAME);
      String htmlContent = loadHtmlTemplate(JSON_FILENAME);

      try (FileWriter writer = new FileWriter(htmlFile, StandardCharsets.UTF_8)) {
        writer.write(htmlContent);
      }

      System.out.println("[TestAnalyzer] Отчёт создан: " + htmlFile.getAbsolutePath());

    } catch (IOException e) {
      System.err.println("[TestAnalyzer] Ошибка при создании отчёта");
      e.printStackTrace();
    }
  }

  private String loadHtmlTemplate(String jsonFileName) throws IOException {
    try (InputStream is = getClass().getResourceAsStream(HTML_TEMPLATE_PATH);
        Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
      String template = scanner.useDelimiter("\\A").next();
      return template.replace("", jsonFileName);
    } catch (NullPointerException e) {
      throw new IOException("HTML template not found: " + HTML_TEMPLATE_PATH + ". Make sure it's in the same package as ReportGenerator.java or adjust path.", e);
    }
  }
}