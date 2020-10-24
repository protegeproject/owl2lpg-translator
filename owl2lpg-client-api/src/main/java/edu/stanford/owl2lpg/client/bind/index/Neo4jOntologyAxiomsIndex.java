package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAxiomsIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.ontology.OntologyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

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
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final OntologyAccessor ontologyAccessor;

  @Inject
  public Neo4jOntologyAxiomsIndex(@Nonnull ProjectId projectId,
                                  @Nonnull BranchId branchId,
                                  @Nonnull DocumentIdMap documentIdMap,
                                  @Nonnull OntologyAccessor ontologyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.ontologyAccessor = checkNotNull(ontologyAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAxiom> getAxioms(@Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return ontologyAccessor.getAllAxioms(projectId, branchId, documentId).stream();
  }

  @Override
  public boolean containsAxiom(@Nonnull OWLAxiom owlAxiom, @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return ontologyAccessor.containsAxiom(owlAxiom, projectId, branchId, documentId);
  }

  @Override
  public boolean containsAxiomIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom, @Nonnull OWLOntologyID owlOntologyID) {
    return containsAxiom(owlAxiom, owlOntologyID);
  }
}
