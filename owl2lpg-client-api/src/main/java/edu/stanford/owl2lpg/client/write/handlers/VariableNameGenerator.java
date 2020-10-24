package edu.stanford.owl2lpg.client.write.handlers;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface VariableNameGenerator {

  @Nonnull
  String generate();

  @Nonnull
  String generate(@Nonnull String prefix);
}
