package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.SubAnnotationPropertyAxiomsBySubPropertyIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSubAnnotationPropertyAxiomsBySubPropertyIndex implements SubAnnotationPropertyAxiomsBySubPropertyIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jSubAnnotationPropertyAxiomsBySubPropertyIndex(@Nonnull ProjectId projectId,
                                                            @Nonnull BranchId branchId,
                                                            @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLSubAnnotationPropertyOfAxiom> getSubPropertyOfAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                                        @Nonnull OntologyDocumentId ontDocId) {
    return axiomAccessor.getAxiomsBySubject(owlAnnotationProperty, projectId, branchId, ontDocId)
        .stream()
        .filter(OWLSubAnnotationPropertyOfAxiom.class::isInstance)
        .map(OWLSubAnnotationPropertyOfAxiom.class::cast);
  }
}
