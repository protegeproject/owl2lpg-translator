package edu.stanford.owl2lpg.client.write.handlers;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VariableNameGenerator {

  private static final String VARIABLE_SYMBOL = "n";

  private final AtomicInteger counter = new AtomicInteger();

  @Nonnull
  public String generate() {
    return VARIABLE_SYMBOL + counter.getAndIncrement();
  }

  @Nonnull
  public String generate(@Nonnull String prefix) {
    return prefix + counter.getAndIncrement();
  }
}
