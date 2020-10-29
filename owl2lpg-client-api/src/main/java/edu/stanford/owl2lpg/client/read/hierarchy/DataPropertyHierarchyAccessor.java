package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface DataPropertyHierarchyAccessor extends HierarchyAccessor<OWLDataProperty> {

  void setRoot(@Nonnull OWLDataProperty root);

  @Nonnull
  ImmutableSet<OWLDataProperty> getTopChildren(@Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId);
}
