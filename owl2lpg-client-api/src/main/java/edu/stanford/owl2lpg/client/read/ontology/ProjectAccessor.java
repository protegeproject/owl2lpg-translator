package edu.stanford.owl2lpg.client.read.ontology;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ProjectAccessor {

  @Nonnull
  Set<OntologyDocumentId> getOntologyDocumentIds(@Nonnull ProjectId projectId,
                                                 @Nonnull BranchId branchId);

  @Nonnull
  Set<IRI> getOntologyIris(@Nonnull ProjectId projectId,
                           @Nonnull BranchId branchId);

  @Nonnull
  Set<IRI> getVersionIris(@Nonnull ProjectId projectId,
                          @Nonnull BranchId branchId);

  @Nonnull
  Stream<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId);

  @Nonnull
  Stream<OWLEntity> getEntitiesByIri(@Nonnull IRI entityIri,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId);

  @Nonnull
  <E extends OWLEntity> Stream<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                    @Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId);

  boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId);
}
