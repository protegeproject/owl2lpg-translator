package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AnnotationAssertionAxiomsByValueIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.AnnotationAssertionAxiomAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationAssertionAxiomsByValueIndex implements AnnotationAssertionAxiomsByValueIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor;

  @Inject
  public Neo4jAnnotationAssertionAxiomsByValueIndex(@Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId,
                                                    @Nonnull DocumentIdMap documentIdMap,
                                                    @Nonnull AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.annotationAssertionAxiomAccessor = checkNotNull(annotationAssertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotationAssertionAxiom> getAxiomsByValue(@Nonnull OWLAnnotationValue owlAnnotationValue,
                                                              @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return annotationAssertionAxiomAccessor.getAxiomsByValue(owlAnnotationValue, projectId, branchId, documentId).stream();
  }
}
