package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAnnotationsIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jOntologyAnnotationsIndex implements OntologyAnnotationsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final OntologyAnnotationsAccessor ontologyAnnotationsAccessor;

  @Inject
  public Neo4jOntologyAnnotationsIndex(@Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull DocumentIdMap documentIdMap,
                                       @Nonnull OntologyAnnotationsAccessor ontologyAnnotationsAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.ontologyAnnotationsAccessor = checkNotNull(ontologyAnnotationsAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotation> getOntologyAnnotations(@Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return ontologyAnnotationsAccessor.getOntologyAnnotations(projectId, branchId, documentId).stream();
  }

  @Override
  public boolean containsAnnotation(@Nonnull OWLAnnotation owlAnnotation, @Nonnull OWLOntologyID owlOntologyID) {
    return getOntologyAnnotations(owlOntologyID)
        .anyMatch(owlAnnotation::equals);
  }
}
