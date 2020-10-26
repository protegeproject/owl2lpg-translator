package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.index.AxiomsByReferenceIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
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
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jAxiomsByReferenceIndex(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull DocumentIdMap documentIdMap,
                                     @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAxiom> getReferencingAxioms(@Nonnull Collection<OWLEntity> collection,
                                               @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return collection.stream()
        .flatMap(entity ->
            Streams.concat(
                axiomAccessor.getAxiomsBySignature(entity, projectId, branchId, documentId).stream(),
                axiomAccessor.getAnnotationAxioms(entity.getIRI(), projectId, branchId, documentId).stream())
        ).distinct();
  }
}
