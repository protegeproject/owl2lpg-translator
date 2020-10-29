package edu.stanford.owl2lpg.client.bind.shortform;

import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormIndex;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.shortform.MultiLingualShortFormAccessor;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jMultiLingualShortFormIndex implements MultiLingualShortFormIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final MultiLingualShortFormAccessor multiLingualShortFormAccessor;

  @Inject
  public Neo4jMultiLingualShortFormIndex(@Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull MultiLingualShortFormAccessor multiLingualShortFormAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.multiLingualShortFormAccessor = checkNotNull(multiLingualShortFormAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getEntities(@Nonnull String entityName,
                                       @Nonnull List<DictionaryLanguage> languages) {
    return multiLingualShortFormAccessor.getEntities(entityName, languages, projectId, branchId).stream();
  }
}
