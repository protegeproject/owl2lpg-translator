package edu.stanford.owl2lpg.client.bind.shortform;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.shortform.MultiLingualShortFormAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jMultiLingualShortFormDictionary implements MultiLingualShortFormDictionary {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final MultiLingualShortFormAccessor multiLingualShortFormAccessor;

  @Inject
  public Neo4jMultiLingualShortFormDictionary(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull MultiLingualShortFormAccessor multiLingualShortFormAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.multiLingualShortFormAccessor = checkNotNull(multiLingualShortFormAccessor);
  }

  @Nonnull
  @Override
  public String getShortForm(@Nonnull OWLEntity owlEntity,
                             @Nonnull List<DictionaryLanguage> languages,
                             @Nonnull String defaultShortForm) {
    return multiLingualShortFormAccessor.getShortForm(owlEntity, languages, defaultShortForm, projectId, branchId);
  }

  @Nonnull
  @Override
  public ImmutableMap<DictionaryLanguage, String> getShortForms(@Nonnull OWLEntity owlEntity,
                                                                @Nonnull List<DictionaryLanguage> languages) {
    return multiLingualShortFormAccessor.getShortForms(owlEntity, languages, projectId, branchId);
  }
}
