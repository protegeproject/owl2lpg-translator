package edu.stanford.owl2lpg.client.read.ontology;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ProjectAccessor {

  @Nonnull
  ImmutableSet<OntologyDocumentId> getOntologyDocumentIds(@Nonnull ProjectId projectId,
                                                          @Nonnull BranchId branchId);

  @Nonnull
  ImmutableSet<OWLOntologyID> getOntologyIds(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId);

  @Nonnull
  ImmutableMap<OntologyDocumentId, OWLOntologyID> getOntologyDocumentIdMap(@Nonnull ProjectId projectId,
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
}
