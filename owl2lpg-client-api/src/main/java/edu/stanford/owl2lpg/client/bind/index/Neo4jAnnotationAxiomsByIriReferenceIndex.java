package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AnnotationAxiomsByIriReferenceIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationAxiomsByIriReferenceIndex implements AnnotationAxiomsByIriReferenceIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jAnnotationAxiomsByIriReferenceIndex(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Override
  public Stream<OWLAnnotationAxiom> getReferencingAxioms(@Nonnull IRI iri, @Nonnull OntologyDocumentId ontDocId) {
    return axiomAccessor.getAnnotationAxioms(iri, projectId, branchId, ontDocId).stream();
  }
}
