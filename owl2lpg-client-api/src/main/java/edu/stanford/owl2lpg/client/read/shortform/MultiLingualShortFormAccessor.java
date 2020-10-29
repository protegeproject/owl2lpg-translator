package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.shortform.EntityShortFormMatches;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface MultiLingualShortFormAccessor {

  @Nonnull
  ImmutableSet<OWLEntity> getEntities(@Nonnull String entityName,
                                      @Nonnull List<DictionaryLanguage> languages,
                                      @Nonnull ProjectId projectId,
                                      @Nonnull BranchId branchId);

  @Nonnull
  String getShortForm(@Nonnull OWLEntity owlEntity,
                      @Nonnull List<DictionaryLanguage> languages,
                      @Nonnull String defaultShortForm,
                      @Nonnull ProjectId projectId,
                      @Nonnull BranchId branchId);

  @Nonnull
  ImmutableMap<DictionaryLanguage, String> getShortForms(@Nonnull OWLEntity owlEntity,
                                                         @Nonnull List<DictionaryLanguage> languages,
                                                         @Nonnull ProjectId projectId,
                                                         @Nonnull BranchId branchId);

  @Nonnull
  Page<EntityShortFormMatches> getShortFormsContaining(@Nonnull List<SearchString> searchStrings,
                                                       @Nonnull Set<EntityType<?>> entityTypes,
                                                       @Nonnull List<DictionaryLanguage> languages,
                                                       @Nonnull PageRequest pageRequest,
                                                       @Nonnull ProjectId projectId,
                                                       @Nonnull BranchId branchId);
}
