package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex.AnnotationsTreatment.EXCLUDE_ANNOTATIONS;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jClassFrameAxiomsIndex implements ClassFrameAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jClassFrameAxiomsIndex(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull DocumentIdMap documentIdMap,
                                    @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Override
  public Set<OWLAxiom> getFrameAxioms(OWLClass owlClass, AnnotationsTreatment annotationsTreatment) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> axiomAccessor.getAxiomsBySubject(owlClass, projectId, branchId, documentId).stream())
        .filter(axiom -> {
          var accepted = true;
          if (annotationsTreatment.equals(EXCLUDE_ANNOTATIONS)) {
            accepted = !(axiom instanceof OWLAnnotationAssertionAxiom);
          }
          return accepted;
        })
        .collect(ImmutableSet.toImmutableSet());
  }
}
