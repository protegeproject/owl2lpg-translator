package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyDomainAxiomsIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jObjectPropertyDomainAxiomsIndex implements ObjectPropertyDomainAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jObjectPropertyDomainAxiomsIndex(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(@Nonnull OWLObjectProperty owlObjectProperty,
                                                                            @Nonnull OntologyDocumentId ontDocId) {
    return axiomAccessor.getAxiomsBySubject(owlObjectProperty, projectId, branchId, ontDocId)
        .stream()
        .filter(OWLObjectPropertyDomainAxiom.class::isInstance)
        .map(OWLObjectPropertyDomainAxiom.class::cast);
  }
}
