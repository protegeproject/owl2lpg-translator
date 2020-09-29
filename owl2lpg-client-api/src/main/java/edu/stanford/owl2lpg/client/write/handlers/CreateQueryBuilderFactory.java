package edu.stanford.owl2lpg.client.write.handlers;

import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CreateQueryBuilderFactory {

  @Inject
  public CreateQueryBuilderFactory() {
  }

  public CreateQueryBuilder getBuilder() {
    return new CreateQueryBuilder(new VariableNameGenerator());
  }
}
