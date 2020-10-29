package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.DisjointClassesAxiomsIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jDisjointClassesAxiomsIndex implements DisjointClassesAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jDisjointClassesAxiomsIndex(@Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLDisjointClassesAxiom> getDisjointClassesAxioms(@Nonnull OWLClass owlClass,
                                                                  @Nonnull OntologyDocumentId ontDocId) {
    return axiomAccessor.getAxiomsBySubject(owlClass, projectId, branchId, ontDocId)
        .stream()
        .filter(OWLDisjointClassesAxiom.class::isInstance)
        .map(OWLDisjointClassesAxiom.class::cast);
  }
}
