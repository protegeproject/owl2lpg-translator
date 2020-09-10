package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAxiomsIndex;
import edu.stanford.owl2lpg.client.read.ontology.OntologyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
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
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final OntologyAccessor ontologyAccessor;

  public Neo4jOntologyAxiomsIndex(@Nonnull ProjectId projectId,
                                  @Nonnull BranchId branchId,
                                  @Nonnull OntologyDocumentId ontoDocId,
                                  @Nonnull OntologyAccessor ontologyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.ontologyAccessor = checkNotNull(ontologyAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAxiom> getAxioms(@Nonnull OWLOntologyID owlOntologyID) {
    return ontologyAccessor.getAllAxioms(projectId, branchId, ontoDocId).stream();
  }

  @Override
  public boolean containsAxiom(@Nonnull OWLAxiom owlAxiom, @Nonnull OWLOntologyID owlOntologyID) {
    return ontologyAccessor.getAxiomsByType(owlAxiom.getAxiomType(), projectId, branchId, ontoDocId)
        .stream()
        .anyMatch(owlAxiom::equals);
  }

  @Override
  public boolean containsAxiomIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom, @Nonnull OWLOntologyID owlOntologyID) {
    return containsAxiom(owlAxiom, owlOntologyID);
  }
}
