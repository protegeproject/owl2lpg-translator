package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ProjectClassAssertionAxiomsByIndividualIndex;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
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
public class Neo4jProjectClassAssertionAxiomsByIndividualIndex implements ProjectClassAssertionAxiomsByIndividualIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final AssertionAxiomAccessor assertionAxiomAccessor;

  @Inject
  public Neo4jProjectClassAssertionAxiomsByIndividualIndex(@Nonnull ProjectId projectId,
                                                           @Nonnull BranchId branchId,
                                                           @Nonnull ProjectAccessor projectAccessor,
                                                           @Nonnull AssertionAxiomAccessor assertionAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.assertionAxiomAccessor = checkNotNull(assertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLClassAssertionAxiom> getClassAssertionAxioms(@Nonnull OWLIndividual owlIndividual) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontoDocId -> assertionAxiomAccessor
            .getClassAssertionsBySubject(owlIndividual, projectId, branchId, ontoDocId)
            .stream());
  }
}
