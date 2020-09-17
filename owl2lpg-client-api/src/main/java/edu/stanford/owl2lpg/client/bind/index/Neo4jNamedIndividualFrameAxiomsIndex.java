package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.NamedIndividualFrameAxiomIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jNamedIndividualFrameAxiomsIndex implements NamedIndividualFrameAxiomIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jNamedIndividualFrameAxiomsIndex(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId ontoDocId,
                                              @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Set<OWLAxiom> getNamedIndividualFrameAxioms(@Nonnull OWLNamedIndividual owlNamedIndividual) {
    return axiomAccessor.getAxiomsBySubject(owlNamedIndividual, projectId, branchId, ontoDocId);
  }
}
