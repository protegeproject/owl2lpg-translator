package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.model.Translation;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TranslationTranslator {

  public enum QueryType {
    CREATE, DELETE
  }

  @Nonnull
  private final QueryBuilderFactory queryBuilderFactory;

  @Inject
  public TranslationTranslator(@Nonnull QueryBuilderFactory queryBuilderFactory) {
    this.queryBuilderFactory = checkNotNull(queryBuilderFactory);
  }

  @Nonnull
  public String translateToCypher(@Nonnull Translation translation, @Nonnull QueryType queryType) {
    switch (queryType) {
      case CREATE:
        return translateToCypherCreateQuery(translation);
      case DELETE:
        return translateToCypherDeleteQuery(translation);
      default:
        return "";
    }
  }

  @Nonnull
  private String translateToCypherCreateQuery(@Nonnull Translation translation) {
    var createQueryBuilder = queryBuilderFactory.getCreateQueryBuilder();
    translation.accept(createQueryBuilder);
    return createQueryBuilder.build();
  }

  @Nonnull
  private String translateToCypherDeleteQuery(@Nonnull Translation translation) {
    var deleteQueryBuilder = queryBuilderFactory.getDeleteQueryBuilder();
    translation.accept(deleteQueryBuilder);
    return deleteQueryBuilder.build();
  }
}
