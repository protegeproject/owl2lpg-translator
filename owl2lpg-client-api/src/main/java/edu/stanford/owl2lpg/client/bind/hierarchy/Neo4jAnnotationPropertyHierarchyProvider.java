package edu.stanford.owl2lpg.client.bind.hierarchy;

import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.AnnotationPropertyHierarchyProvider;
import edu.stanford.owl2lpg.client.read.hierarchy.AnnotationPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
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
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AnnotationPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jAnnotationPropertyHierarchyProvider(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId,
                                                  @Nonnull AnnotationPropertyHierarchyAccessor hierarchyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Inject

  @Override
  public Collection<OWLAnnotationProperty> getRoots() {
    return hierarchyAccessor.getRoots(projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLAnnotationProperty> getChildren(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getChildren(owlAnnotationProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isLeaf(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.isLeaf(owlAnnotationProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLAnnotationProperty> getDescendants(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getDescendants(owlAnnotationProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLAnnotationProperty> getParents(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getParents(owlAnnotationProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLAnnotationProperty> getAncestors(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getAncestors(owlAnnotationProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<List<OWLAnnotationProperty>> getPathsToRoot(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getPathsToRoot(owlAnnotationProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isAncestor(OWLAnnotationProperty parent, OWLAnnotationProperty child) {
    return hierarchyAccessor.isAncestor(parent, child, projectId, branchId, ontoDocId);
  }

  @Override
  public void handleChanges(List<OntologyChange> list) {
    // Do nothing
  }
}
