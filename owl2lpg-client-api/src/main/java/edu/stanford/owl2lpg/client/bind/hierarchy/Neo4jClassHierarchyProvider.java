package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
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
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Nonnull
  private final ClassHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jClassHierarchyProvider(@Nonnull @ClassHierarchyRoot OWLClass root,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull ProjectAccessor projectAccessor,
                                     @Nonnull EntityAccessor entityAccessor,
                                     @Nonnull ClassHierarchyAccessor hierarchyAccessor) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.entityAccessor = checkNotNull(entityAccessor);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
    hierarchyAccessor.setRoot(root);
  }

  @Override
  public Collection<OWLClass> getRoots() {
    return ImmutableSet.of(root);
  }

  @Override
  public Collection<OWLClass> getChildren(OWLClass owlClass) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return projectAccessor.getOntologyDocumentIds(projectId, branchId)
          .stream()
          .flatMap(ontDocId -> hierarchyAccessor.getTopChildren(projectId, branchId, ontDocId).stream())
          .collect(ImmutableSet.toImmutableSet());
    } else {
      return projectAccessor.getOntologyDocumentIds(projectId, branchId)
          .stream()
          .flatMap(ontDocId -> hierarchyAccessor.getChildren(owlClass, projectId, branchId, ontDocId).stream())
          .collect(ImmutableSet.toImmutableSet());
    }
  }

  @Override
  public boolean isLeaf(OWLClass owlClass) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .anyMatch(ontDocId -> hierarchyAccessor.isLeaf(owlClass, projectId, branchId, ontDocId));
  }

  @Override
  public Collection<OWLClass> getDescendants(OWLClass owlClass) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return getAllClasses();
    } else {
      return projectAccessor.getOntologyDocumentIds(projectId, branchId)
          .stream()
          .flatMap(ontDocId -> hierarchyAccessor.getDescendants(owlClass, projectId, branchId, ontDocId).stream())
          .collect(ImmutableSet.toImmutableSet());
    }
  }

  @Nonnull
  private ImmutableSet<OWLClass> getAllClasses() {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> entityAccessor.getEntitiesByType(EntityType.CLASS, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLClass> getParents(OWLClass owlClass) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getParents(owlClass, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLClass> getAncestors(OWLClass owlClass) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getAncestors(owlClass, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<List<OWLClass>> getPathsToRoot(OWLClass owlClass) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getPathsToRoot(owlClass, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLClass parent, OWLClass child) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .anyMatch(ontDocId -> hierarchyAccessor.isAncestor(parent, child, projectId, branchId, ontDocId));
  }

  @Override
  public void handleChanges(List<OntologyChange> list) {
    // Do nothing
  }
}
