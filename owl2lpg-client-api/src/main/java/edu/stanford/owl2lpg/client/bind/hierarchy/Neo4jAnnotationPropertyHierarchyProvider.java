package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.AnnotationPropertyHierarchyProvider;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.hierarchy.AnnotationPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
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
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AnnotationPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jAnnotationPropertyHierarchyProvider(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull DocumentIdMap documentIdMap,
                                                  @Nonnull AnnotationPropertyHierarchyAccessor hierarchyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLAnnotationProperty> getRoots() {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getRoots(projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getChildren(OWLAnnotationProperty owlAnnotationProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getChildren(owlAnnotationProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isLeaf(OWLAnnotationProperty owlAnnotationProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .anyMatch(documentId -> hierarchyAccessor.isLeaf(owlAnnotationProperty, projectId, branchId, documentId));
  }

  @Override
  public Collection<OWLAnnotationProperty> getDescendants(OWLAnnotationProperty owlAnnotationProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getDescendants(owlAnnotationProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getParents(OWLAnnotationProperty owlAnnotationProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getParents(owlAnnotationProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getAncestors(OWLAnnotationProperty owlAnnotationProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getAncestors(owlAnnotationProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<List<OWLAnnotationProperty>> getPathsToRoot(OWLAnnotationProperty owlAnnotationProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getPathsToRoot(owlAnnotationProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLAnnotationProperty parent, OWLAnnotationProperty child) {
    return documentIdMap.get(projectId)
        .stream()
        .anyMatch(documentId -> hierarchyAccessor.isAncestor(parent, child, projectId, branchId, documentId));
  }

  @Override
  public void handleChanges(List<OntologyChange> list) {
    // Do nothing
  }
}
