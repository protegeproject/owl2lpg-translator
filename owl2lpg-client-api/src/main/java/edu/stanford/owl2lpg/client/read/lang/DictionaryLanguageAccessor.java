package edu.stanford.owl2lpg.client.read.lang;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface DictionaryLanguageAccessor {

  DictionaryLanguageUsageSummary getUsageSummary(@Nonnull ProjectId projectId,
                                                 @Nonnull BranchId branchId,
                                                 @Nonnull OntologyDocumentId ontoDocId);
}
