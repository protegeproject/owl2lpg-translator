package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AxiomsByReferenceIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomBySubjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAxiomsByReferenceIndex implements AxiomsByReferenceIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AxiomBySubjectAccessor axiomBySubjectAccessor;

  @Inject
  public Neo4jAxiomsByReferenceIndex(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId,
                                     @Nonnull AxiomBySubjectAccessor axiomBySubjectAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.axiomBySubjectAccessor = checkNotNull(axiomBySubjectAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAxiom> getReferencingAxioms(@Nonnull Collection<OWLEntity> collection,
                                               @Nonnull OWLOntologyID owlOntologyID) {
    return axiomBySubjectAccessor.getAxiomsForSubjects(collection, projectId, branchId, ontoDocId).stream();
  }
}
