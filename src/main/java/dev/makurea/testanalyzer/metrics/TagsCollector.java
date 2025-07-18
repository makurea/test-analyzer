package dev.makurea.testanalyzer.metrics;

import dev.makurea.testanalyzer.core.TestResult;
import java.util.Collections;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagsCollector implements MetricCollector {

  @Override
  public void collect(ExtensionContext context, TestResult.Builder builder) {
    List<String> tags = context.getElement()
        .map(el -> Arrays.stream(el.getAnnotationsByType(Tag.class))
            .map(Tag::value)
            .collect(Collectors.toList()))
        .orElse(Collections.emptyList());

    builder.tags(tags);
  }
}
