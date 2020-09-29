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

  @Nonnull
  private final CreateQueryBuilderFactory createQueryBuilderFactory;

  @Inject
  public TranslationTranslator(@Nonnull CreateQueryBuilderFactory createQueryBuilderFactory) {
    this.createQueryBuilderFactory = checkNotNull(createQueryBuilderFactory);
  }

//  @Nonnull
//  private final DeleteQueryBuilderFactory deleteQueryBuilderFactory;

  @Nonnull
  public String translateToCypherCreateQuery(@Nonnull Translation translation) {
    var createQueryBuilder = createQueryBuilderFactory.getBuilder();
    translation.accept(createQueryBuilder);
    return createQueryBuilder.build();
  }

  @Nonnull
  public String translateToCypherDeleteQuery(@Nonnull Translation translation) {
//    var deleteQueryBuilder = deleteQueryBuilderFactory.getBuilder();
//    translation.accept(deleteQueryBuilder);
//    return deleteQueryBuilder.build();
    return "";
  }
}
