package edu.stanford.owl2lpg.client.write.handlers;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VariableNameGeneratorImpl implements VariableNameGenerator {

  private static final String DEFAULT_VARIABLE_PREFIX = "n";

  private final AtomicInteger counter = new AtomicInteger();

  @Inject
  public VariableNameGeneratorImpl() {
  }

  @Nonnull
  @Override
  public String generate() {
    return generate(DEFAULT_VARIABLE_PREFIX);
  }

  @Nonnull
  @Override
  public String generate(@Nonnull String prefix) {
    return prefix + counter.getAndIncrement();
  }
}
