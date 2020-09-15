package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.PropertyAssertionAxiomsBySubjectIndex;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jPropertyAssertionAxiomsBySubjectIndex implements PropertyAssertionAxiomsBySubjectIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor;

  @Inject
  public Neo4jPropertyAssertionAxiomsBySubjectIndex(@Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId,
                                                    @Nonnull OntologyDocumentId ontoDocId,
                                                    @Nonnull AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.assertionAxiomBySubjectAccessor = checkNotNull(assertionAxiomBySubjectAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAxiom> getPropertyAssertions(@Nonnull OWLIndividual owlIndividual,
                                                @Nonnull OWLOntologyID owlOntologyID) {
    return assertionAxiomBySubjectAccessor
        .getPropertyAssertionsForSubject(owlIndividual, projectId, branchId, ontoDocId)
        .stream();
  }
}
