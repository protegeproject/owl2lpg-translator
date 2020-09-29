package edu.stanford.owl2lpg.client.write.handlers;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class QueryBuilderFactory {

  @Inject
  public QueryBuilderFactory() {
  }

  @Nonnull
  public CreateQueryBuilder getCreateQueryBuilder() {
    return new CreateQueryBuilder(new VariableNameGenerator());
  }

  @Nonnull
  public DeleteQueryBuilder getDeleteQueryBuilder() {
    return new DeleteQueryBuilder(new VariableNameGenerator());
  }
}
