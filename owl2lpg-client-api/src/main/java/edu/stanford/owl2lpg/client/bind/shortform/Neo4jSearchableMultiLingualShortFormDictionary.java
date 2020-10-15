package edu.stanford.owl2lpg.client.bind.shortform;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.server.shortform.EntityShortFormMatches;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.shortform.SearchableMultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.search.EntitySearchFilter;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.shortform.MultiLingualShortFormAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSearchableMultiLingualShortFormDictionary implements SearchableMultiLingualShortFormDictionary {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final MultiLingualShortFormAccessor multiLingualShortFormAccessor;

  @Inject
  public Neo4jSearchableMultiLingualShortFormDictionary(@Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull MultiLingualShortFormAccessor multiLingualShortFormAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.multiLingualShortFormAccessor = checkNotNull(multiLingualShortFormAccessor);
  }


  @Nonnull
  @Override
  public Page<EntityShortFormMatches> getShortFormsContaining(@Nonnull List<SearchString> searchStrings,
                                                              @Nonnull Set<EntityType<?>> entityTypes,
                                                              @Nonnull List<DictionaryLanguage> languages,
                                                              @Nonnull ImmutableList<EntitySearchFilter> searchFilters,
                                                              @Nonnull PageRequest pageRequest) {
    return multiLingualShortFormAccessor.getShortFormsContaining(
        searchStrings, entityTypes, languages, pageRequest, projectId, branchId);
  }
}
