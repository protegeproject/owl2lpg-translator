package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.DisjointDataPropertiesAxiomsIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jDisjointDataPropertiesAxiomsIndex implements DisjointDataPropertiesAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jDisjointDataPropertiesAxiomsIndex(@Nonnull ProjectId projectId,
                                                @Nonnull BranchId branchId,
                                                @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(@Nonnull OWLDataProperty owlDataProperty,
                                                                                @Nonnull OntologyDocumentId ontDocId) {
    return axiomAccessor.getAxiomsBySubject(owlDataProperty, projectId, branchId, ontDocId)
        .stream()
        .filter(OWLDisjointDataPropertiesAxiom.class::isInstance)
        .map(OWLDisjointDataPropertiesAxiom.class::cast);
  }
}
