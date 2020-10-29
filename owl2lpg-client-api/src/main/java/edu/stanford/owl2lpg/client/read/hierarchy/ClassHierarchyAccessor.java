package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ClassHierarchyAccessor extends HierarchyAccessor<OWLClass> {

  void setRoot(@Nonnull OWLClass root);

  @Nonnull
  ImmutableSet<OWLClass> getTopChildren(@Nonnull ProjectId projectId,
                                        @Nonnull BranchId branchId,
                                        @Nonnull OntologyDocumentId ontoDocId);
}
