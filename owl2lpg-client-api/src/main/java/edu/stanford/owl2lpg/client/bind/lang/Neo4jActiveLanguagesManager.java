package edu.stanford.owl2lpg.client.bind.lang;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.lang.ActiveLanguagesManager;
import edu.stanford.bmir.protege.web.shared.lang.DictionaryLanguageUsage;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.lang.DictionaryLanguageAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Map.Entry.comparingByValue;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jActiveLanguagesManager implements ActiveLanguagesManager {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final DictionaryLanguageAccessor dictionaryLanguageAccessor;

  @Inject
  public Neo4jActiveLanguagesManager(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId,
                                     @Nonnull DictionaryLanguageAccessor dictionaryLanguageAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.dictionaryLanguageAccessor = checkNotNull(dictionaryLanguageAccessor);
  }

  @Nonnull
  @Override
  public ImmutableList<DictionaryLanguage> getLanguagesRankedByUsage() {
    return getLanguageUsage()
        .stream()
        .map(DictionaryLanguageUsage::getDictionaryLanguage)
        .collect(ImmutableList.toImmutableList());
  }

  @Nonnull
  @Override
  public ImmutableList<DictionaryLanguageUsage> getLanguageUsage() {
    return dictionaryLanguageAccessor.getUsageSummary(projectId, branchId, ontoDocId)
        .stream()
        .sorted(Collections.reverseOrder(comparingByValue()))
        .map(entry -> DictionaryLanguageUsage.get(entry.getKey(), entry.getValue()))
        .collect(ImmutableList.toImmutableList());
  }

  @Override
  public void handleChanges(@Nonnull List<OntologyChange> list) {
    // NO-OP
  }
}
