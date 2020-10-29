package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAxiomsIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.ontology.OntologyDocumentAccessor;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jOntologyAxiomsIndex implements OntologyAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentAccessor ontologyDocumentAccessor;

  @Inject
  public Neo4jOntologyAxiomsIndex(@Nonnull ProjectId projectId,
                                  @Nonnull BranchId branchId,
                                  @Nonnull OntologyDocumentAccessor ontologyDocumentAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontologyDocumentAccessor = checkNotNull(ontologyDocumentAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAxiom> getAxioms(@Nonnull OntologyDocumentId ontDocId) {
    return ontologyDocumentAccessor.getAllAxioms(projectId, branchId, ontDocId).stream();
  }

  @Override
  public boolean containsAxiom(@Nonnull OWLAxiom owlAxiom, @Nonnull OntologyDocumentId ontDocId) {
    return ontologyDocumentAccessor.containsAxiom(owlAxiom, projectId, branchId, ontDocId);
  }

  @Override
  public boolean containsAxiomIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom, @Nonnull OntologyDocumentId ontDocId) {
    return containsAxiom(owlAxiom, ontDocId);
  }
}
