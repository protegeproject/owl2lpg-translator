package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AnnotationPropertyHierarchyAccessor extends HierarchyAccessor<OWLAnnotationProperty> {

  @Nonnull
  Collection<OWLAnnotationProperty> getRoots(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull OntologyDocumentId ontoDocId);
}
