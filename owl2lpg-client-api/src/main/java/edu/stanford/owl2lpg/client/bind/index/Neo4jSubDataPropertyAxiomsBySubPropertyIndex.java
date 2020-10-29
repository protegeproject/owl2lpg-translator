package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.SubDataPropertyAxiomsBySubPropertyIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSubDataPropertyAxiomsBySubPropertyIndex implements SubDataPropertyAxiomsBySubPropertyIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jSubDataPropertyAxiomsBySubPropertyIndex(@Nonnull ProjectId projectId,
                                                      @Nonnull BranchId branchId,
                                                      @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLSubDataPropertyOfAxiom> getSubPropertyOfAxioms(@Nonnull OWLDataProperty owlDataProperty,
                                                                  @Nonnull OntologyDocumentId ontDocId) {
    return axiomAccessor.getAxiomsBySubject(owlDataProperty, projectId, branchId, ontDocId)
        .stream()
        .filter(OWLSubDataPropertyOfAxiom.class::isInstance)
        .map(OWLSubDataPropertyOfAxiom.class::cast);
  }
}
