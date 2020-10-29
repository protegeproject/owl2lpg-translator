package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.DifferentIndividualsAxiomsIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jDifferentIndividualsAxiomsIndex implements DifferentIndividualsAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jDifferentIndividualsAxiomsIndex(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLDifferentIndividualsAxiom> getDifferentIndividualsAxioms(@Nonnull OWLIndividual owlIndividual,
                                                                            @Nonnull OntologyDocumentId ontDocId) {
    // TODO Handle the case when the instance is anonymous
    return (owlIndividual.isNamed()) ?
        axiomAccessor.getAxiomsBySubject(owlIndividual.asOWLNamedIndividual(), projectId, branchId, ontDocId)
            .stream()
            .filter(OWLDifferentIndividualsAxiom.class::isInstance)
            .map(OWLDifferentIndividualsAxiom.class::cast) :
        Stream.empty();
  }
}
