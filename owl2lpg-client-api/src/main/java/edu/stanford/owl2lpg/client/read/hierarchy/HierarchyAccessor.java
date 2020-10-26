package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
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
  ImmutableSet<E> getAncestors(@Nonnull E entity,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<E> getDescendants(@Nonnull E entity,
                                 @Nonnull ProjectId projectId,
                                 @Nonnull BranchId branchId,
                                 @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<E> getParents(@Nonnull E entity,
                             @Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<E> getChildren(@Nonnull E entity,
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
