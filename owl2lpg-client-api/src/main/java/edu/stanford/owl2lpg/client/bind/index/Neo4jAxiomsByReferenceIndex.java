package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.index.AxiomsByReferenceIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

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
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jAxiomsByReferenceIndex(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAxiom> getReferencingAxioms(@Nonnull Collection<OWLEntity> collection,
                                               @Nonnull OntologyDocumentId ontDocId) {
    return collection.stream()
        .flatMap(entity ->
            Streams.concat(
                axiomAccessor.getAxiomsBySignature(entity, projectId, branchId, ontDocId).stream(),
                axiomAccessor.getAnnotationAxioms(entity.getIRI(), projectId, branchId, ontDocId).stream())
        ).distinct();
  }
}
