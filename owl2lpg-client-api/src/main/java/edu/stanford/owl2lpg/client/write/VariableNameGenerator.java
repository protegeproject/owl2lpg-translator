package edu.stanford.owl2lpg.client.write;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VariableNameGenerator {

  private static final String DEFAULT_VARIABLE_PREFIX = "n";

  private final AtomicInteger counter = new AtomicInteger();

  @Inject
  public VariableNameGenerator() {
  }

  @Nonnull
  public String generate() {
    return generate(DEFAULT_VARIABLE_PREFIX);
  }

  @Nonnull
  public String generate(@Nonnull String prefix) {
    return prefix + counter.getAndIncrement();
  }
}
