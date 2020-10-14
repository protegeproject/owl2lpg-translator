package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.DataFactory.getOWLThing;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jClassHierarchyProvider implements ClassHierarchyProvider {

  @Nonnull
  private final OWLClass root;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Nonnull
  private final ClassHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jClassHierarchyProvider(@Nonnull @ClassHierarchyRoot OWLClass root,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId,
                                     @Nonnull EntityAccessor entityAccessor,
                                     @Nonnull ClassHierarchyAccessor hierarchyAccessor) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.entityAccessor = checkNotNull(entityAccessor);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLClass> getRoots() {
    return ImmutableSet.of(root);
  }

  @Override
  public Collection<OWLClass> getChildren(OWLClass owlClass) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return hierarchyAccessor.getTopChildren(projectId, branchId, ontoDocId);
    } else {
      return hierarchyAccessor.getChildren(owlClass, projectId, branchId, ontoDocId);
    }
  }

  @Override
  public boolean isLeaf(OWLClass owlClass) {
    return hierarchyAccessor.isLeaf(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLClass> getDescendants(OWLClass owlClass) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return getAllClasses();
    } else {
      return hierarchyAccessor.getDescendants(owlClass, projectId, branchId, ontoDocId);
    }
  }

  @Nonnull
  private ImmutableSet<OWLClass> getAllClasses() {
    return entityAccessor.getEntitiesByType(EntityType.CLASS, projectId, branchId, ontoDocId)
        .stream()
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLClass> getParents(OWLClass owlClass) {
    return hierarchyAccessor.getParents(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLClass> getAncestors(OWLClass owlClass) {
    return hierarchyAccessor.getAncestors(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<List<OWLClass>> getPathsToRoot(OWLClass owlClass) {
    return hierarchyAccessor.getPathsToRoot(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isAncestor(OWLClass parent, OWLClass child) {
    return hierarchyAccessor.isAncestor(parent, child, projectId, branchId, ontoDocId);
  }

  @Override
  public void handleChanges(List<OntologyChange> list) {
    // Do nothing
  }
}
