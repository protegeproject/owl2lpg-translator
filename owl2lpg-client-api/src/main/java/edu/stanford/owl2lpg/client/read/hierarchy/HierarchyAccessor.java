package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HierarchyAccessor<E extends OWLEntity> {

  @Nonnull
  Collection<E> getAncestors(@Nonnull E entity,
                             @Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Collection<E> getDescendants(@Nonnull E entity,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Collection<E> getParents(@Nonnull E entity,
                           @Nonnull ProjectId projectId,
                           @Nonnull BranchId branchId,
                           @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Collection<E> getChildren(@Nonnull E entity,
                            @Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Collection<List<E>> getPathsToRoot(@Nonnull E entity,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId);

  boolean isAncestor(@Nonnull E parent,
                     @Nonnull E child,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontoDocId);

  boolean isLeaf(@Nonnull E entity,
                 @Nonnull ProjectId projectId,
                 @Nonnull BranchId branchId,
                 @Nonnull OntologyDocumentId ontoDocId);
}
