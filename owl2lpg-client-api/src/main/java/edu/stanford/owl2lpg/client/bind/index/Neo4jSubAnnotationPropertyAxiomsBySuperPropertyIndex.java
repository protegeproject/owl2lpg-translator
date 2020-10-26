package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.SubAnnotationPropertyAxiomsBySuperPropertyIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSubAnnotationPropertyAxiomsBySuperPropertyIndex implements SubAnnotationPropertyAxiomsBySuperPropertyIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jSubAnnotationPropertyAxiomsBySuperPropertyIndex(@Nonnull ProjectId projectId,
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
  public Stream<OWLSubAnnotationPropertyOfAxiom> getAxiomsForSuperProperty(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                                           @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return axiomAccessor
        .getSubAnnotationPropertyOfAxiomsBySuperProperty(owlAnnotationProperty, projectId, branchId, documentId)
        .stream();
  }
}
