package edu.stanford.owl2lpg.client.read.entity;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface EntityAccessor {

  @Nonnull
  ImmutableSet<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLEntity> getEntitiesByIri(@Nonnull IRI entityIri,
                                           @Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  <E extends OWLEntity> ImmutableSet<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                          @Nonnull ProjectId projectId,
                                                          @Nonnull BranchId branchId,
                                                          @Nonnull OntologyDocumentId ontoDocId);
}
