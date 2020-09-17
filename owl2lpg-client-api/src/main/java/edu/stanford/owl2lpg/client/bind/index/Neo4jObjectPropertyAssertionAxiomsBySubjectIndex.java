package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyAssertionAxiomsBySubjectIndex;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jObjectPropertyAssertionAxiomsBySubjectIndex implements ObjectPropertyAssertionAxiomsBySubjectIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AssertionAxiomAccessor assertionAxiomAccessor;

  @Inject
  public Neo4jObjectPropertyAssertionAxiomsBySubjectIndex(@Nonnull ProjectId projectId,
                                                          @Nonnull BranchId branchId,
                                                          @Nonnull OntologyDocumentId ontoDocId,
                                                          @Nonnull AssertionAxiomAccessor assertionAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.assertionAxiomAccessor = checkNotNull(assertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertions(@Nonnull OWLIndividual owlIndividual,
                                                                             @Nonnull OWLOntologyID owlOntologyID) {
    return assertionAxiomAccessor
        .getObjectPropertyAssertionsBySubject(owlIndividual, projectId, branchId, ontoDocId)
        .stream();
  }
}
