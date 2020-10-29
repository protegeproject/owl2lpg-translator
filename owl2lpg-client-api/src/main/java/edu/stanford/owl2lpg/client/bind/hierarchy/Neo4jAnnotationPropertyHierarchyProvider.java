package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.AnnotationPropertyHierarchyProvider;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.hierarchy.AnnotationPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationPropertyHierarchyProvider implements AnnotationPropertyHierarchyProvider {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final AnnotationPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jAnnotationPropertyHierarchyProvider(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull ProjectAccessor projectAccessor,
                                                  @Nonnull AnnotationPropertyHierarchyAccessor hierarchyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLAnnotationProperty> getRoots() {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getRoots(projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getChildren(OWLAnnotationProperty owlAnnotationProperty) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getChildren(owlAnnotationProperty, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isLeaf(OWLAnnotationProperty owlAnnotationProperty) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .anyMatch(ontDocId -> hierarchyAccessor.isLeaf(owlAnnotationProperty, projectId, branchId, ontDocId));
  }

  @Override
  public Collection<OWLAnnotationProperty> getDescendants(OWLAnnotationProperty owlAnnotationProperty) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getDescendants(owlAnnotationProperty, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getParents(OWLAnnotationProperty owlAnnotationProperty) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getParents(owlAnnotationProperty, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getAncestors(OWLAnnotationProperty owlAnnotationProperty) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getAncestors(owlAnnotationProperty, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<List<OWLAnnotationProperty>> getPathsToRoot(OWLAnnotationProperty owlAnnotationProperty) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontDocId -> hierarchyAccessor.getPathsToRoot(owlAnnotationProperty, projectId, branchId, ontDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLAnnotationProperty parent, OWLAnnotationProperty child) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .anyMatch(ontDocId -> hierarchyAccessor.isAncestor(parent, child, projectId, branchId, ontDocId));
  }

  @Override
  public void handleChanges(List<OntologyChange> list) {
    // Do nothing
  }
}
