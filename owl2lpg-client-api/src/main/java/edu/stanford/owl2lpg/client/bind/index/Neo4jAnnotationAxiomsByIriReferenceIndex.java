package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AnnotationAxiomsByIriReferenceIndex;
import edu.stanford.owl2lpg.client.read.axiom.AnnotationAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

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
  private final AxiomContext axiomContext;

  @Nonnull
  private final AnnotationAxiomAccessor annotationAxiomAccessor;

  @Inject
  public Neo4jAnnotationAxiomsByIriReferenceIndex(@Nonnull AxiomContext axiomContext,
                                                  @Nonnull AnnotationAxiomAccessor annotationAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.annotationAxiomAccessor = checkNotNull(annotationAxiomAccessor);
  }

  @Override
  public Stream<OWLAnnotationAxiom> getReferencingAxioms(@Nonnull IRI iri, @Nonnull OWLOntologyID owlOntologyID) {
    return annotationAxiomAccessor.getAnnotationAxioms(iri,
        axiomContext.getProjectId(),
        axiomContext.getBranchId(),
        axiomContext.getOntologyDocumentId()).stream();
  }
}
