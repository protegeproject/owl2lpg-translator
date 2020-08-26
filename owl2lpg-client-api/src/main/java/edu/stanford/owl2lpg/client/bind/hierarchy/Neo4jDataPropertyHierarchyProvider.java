package edu.stanford.owl2lpg.client.bind.hierarchy;

import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyProvider;
import edu.stanford.owl2lpg.client.read.hierarchy.DataPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jDataPropertyHierarchyProvider implements DataPropertyHierarchyProvider {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final DataPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jDataPropertyHierarchyProvider(@Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId,
                                            @Nonnull DataPropertyHierarchyAccessor hierarchyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLDataProperty> getRoots() {
    return hierarchyAccessor.getRoots(projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLDataProperty> getChildren(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getChildren(owlDataProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isLeaf(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.isLeaf(owlDataProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLDataProperty> getDescendants(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getDescendants(owlDataProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLDataProperty> getParents(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getParents(owlDataProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLDataProperty> getAncestors(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getAncestors(owlDataProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<List<OWLDataProperty>> getPathsToRoot(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getPathsToRoot(owlDataProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isAncestor(OWLDataProperty parent, OWLDataProperty child) {
    return hierarchyAccessor.isAncestor(parent, child, projectId, branchId, ontoDocId);
  }
}
