package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.EntitiesInOntologySignatureByIriIndex;
import edu.stanford.owl2lpg.client.read.signature.OntologySignatureAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jEntitiesInOntologySignatureByIriIndex implements EntitiesInOntologySignatureByIriIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final OntologySignatureAccessor ontologySignatureAccessor;

  @Inject
  public Neo4jEntitiesInOntologySignatureByIriIndex(@Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId,
                                                    @Nonnull OntologyDocumentId ontoDocId,
                                                    @Nonnull OntologySignatureAccessor ontologySignatureAccessor) {
    this.projectId = projectId;
    this.branchId = branchId;
    this.ontoDocId = ontoDocId;
    this.ontologySignatureAccessor = ontologySignatureAccessor;
  }
  
  @Nonnull
  @Override
  public Stream<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri, @Nonnull OWLOntologyID owlOntologyID) {
    return ontologySignatureAccessor.getEntitiesInSignature(iri, projectId, branchId, ontoDocId).stream();
  }
}
