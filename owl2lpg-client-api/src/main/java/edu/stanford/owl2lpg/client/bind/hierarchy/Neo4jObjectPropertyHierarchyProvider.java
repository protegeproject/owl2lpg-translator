package edu.stanford.owl2lpg.client.bind.hierarchy;

import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyProvider;
import edu.stanford.owl2lpg.client.read.hierarchy.ObjectPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jObjectPropertyHierarchyProvider implements ObjectPropertyHierarchyProvider {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final ObjectPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jObjectPropertyHierarchyProvider(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId ontoDocId,
                                              @Nonnull ObjectPropertyHierarchyAccessor hierarchyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLObjectProperty> getRoots() {
    return hierarchyAccessor.getRoots(projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLObjectProperty> getChildren(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getChildren(owlObjectProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isLeaf(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.isLeaf(owlObjectProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLObjectProperty> getDescendants(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getDescendants(owlObjectProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLObjectProperty> getParents(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getParents(owlObjectProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLObjectProperty> getAncestors(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getAncestors(owlObjectProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<List<OWLObjectProperty>> getPathsToRoot(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getPathsToRoot(owlObjectProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isAncestor(OWLObjectProperty parent, OWLObjectProperty child) {
    return hierarchyAccessor.isAncestor(parent, child, projectId, branchId, ontoDocId);
  }
}
