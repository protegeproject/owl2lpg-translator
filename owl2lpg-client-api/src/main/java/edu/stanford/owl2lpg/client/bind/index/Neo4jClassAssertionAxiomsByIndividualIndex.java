package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ClassAssertionAxiomsByIndividualIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jClassAssertionAxiomsByIndividualIndex implements ClassAssertionAxiomsByIndividualIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AssertionAxiomAccessor assertionAxiomAccessor;

  @Inject
  public Neo4jClassAssertionAxiomsByIndividualIndex(@Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId,
                                                    @Nonnull AssertionAxiomAccessor assertionAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.assertionAxiomAccessor = checkNotNull(assertionAxiomAccessor);
  }

  @Override
  public Stream<OWLClassAssertionAxiom> getClassAssertionAxioms(@Nonnull OWLIndividual owlIndividual,
                                                                @Nonnull OntologyDocumentId ontDocId) {
    return assertionAxiomAccessor
        .getClassAssertionsBySubject(owlIndividual, projectId, branchId, ontDocId)
        .stream();
  }
}
