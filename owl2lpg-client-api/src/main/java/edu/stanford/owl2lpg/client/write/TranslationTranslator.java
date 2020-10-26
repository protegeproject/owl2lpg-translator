package edu.stanford.owl2lpg.client.write;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import edu.stanford.owl2lpg.model.Translation;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TranslationTranslator {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final QueryBuilderFactory queryBuilderFactory;

  @Inject
  public TranslationTranslator(@Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull DocumentIdMap documentIdMap,
                               @Nonnull QueryBuilderFactory queryBuilderFactory) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.queryBuilderFactory = checkNotNull(queryBuilderFactory);
    this.documentIdMap = checkNotNull(documentIdMap);
  }

  @Nonnull
  public ImmutableList<String> translateToCypherCreateQuery(@Nonnull OWLOntologyID ontologyId,
                                                            @Nonnull Translation translation) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    var createQueryBuilder = queryBuilderFactory.getCreateQueryBuilder(projectId, branchId, documentId, ontologyId);
    translation.accept(createQueryBuilder);
    return createQueryBuilder.build();
  }

  @Nonnull
  public ImmutableList<String> translateToCypherDeleteQuery(@Nonnull OWLOntologyID ontologyId,
                                                            @Nonnull Translation translation) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    var deleteQueryBuilder = queryBuilderFactory.getDeleteQueryBuilder(projectId, branchId, documentId, ontologyId);
    translation.accept(deleteQueryBuilder);
    return deleteQueryBuilder.build();
  }
}
