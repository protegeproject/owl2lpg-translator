package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyRoot;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.DataPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLDataFactory;
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
  private final OWLDataProperty root;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Nonnull
  private final DataPropertyHierarchyAccessor hierarchyAccessor;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public Neo4jDataPropertyHierarchyProvider(@Nonnull @DataPropertyHierarchyRoot OWLDataProperty root,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId,
                                            @Nonnull EntityAccessor entityAccessor,
                                            @Nonnull DataPropertyHierarchyAccessor hierarchyAccessor,
                                            @Nonnull OWLDataFactory dataFactory) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.entityAccessor = checkNotNull(entityAccessor);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public Collection<OWLDataProperty> getRoots() {
    return ImmutableSet.of(root);
  }

  @Override
  public Collection<OWLDataProperty> getChildren(OWLDataProperty owlDataProperty) {
    if (root.equals(dataFactory.getOWLTopDataProperty()) && root.equals(owlDataProperty)) {
      return hierarchyAccessor.getTopChildren(projectId, branchId, ontoDocId);
    } else {
      return hierarchyAccessor.getChildren(owlDataProperty, projectId, branchId, ontoDocId);
    }
  }

  @Override
  public boolean isLeaf(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.isLeaf(owlDataProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLDataProperty> getDescendants(OWLDataProperty owlDataProperty) {
    if (root.equals(dataFactory.getOWLTopDataProperty()) && root.equals(owlDataProperty)) {
      return getAllDataProperties();
    } else {
      return hierarchyAccessor.getDescendants(owlDataProperty, projectId, branchId, ontoDocId);
    }
  }

  @Nonnull
  private ImmutableSet<OWLDataProperty> getAllDataProperties() {
    return entityAccessor.getEntitiesByType(EntityType.DATA_PROPERTY, projectId, branchId, ontoDocId)
        .stream()
        .collect(ImmutableSet.toImmutableSet());
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

  @Override
  public void handleChanges(List<OntologyChange> list) {
    // Do nothing
  }
}
