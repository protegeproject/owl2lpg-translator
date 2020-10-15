package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyRoot;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.ObjectPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLDataFactory;
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
  private final OWLObjectProperty root;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Nonnull
  private final ObjectPropertyHierarchyAccessor hierarchyAccessor;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public Neo4jObjectPropertyHierarchyProvider(@Nonnull @ObjectPropertyHierarchyRoot OWLObjectProperty root,
                                              @Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId ontoDocId,
                                              @Nonnull EntityAccessor entityAccessor,
                                              @Nonnull ObjectPropertyHierarchyAccessor hierarchyAccessor,
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
  public Collection<OWLObjectProperty> getRoots() {
    return ImmutableSet.of(root);
  }

  @Override
  public Collection<OWLObjectProperty> getChildren(OWLObjectProperty owlObjectProperty) {
    if (root.equals(dataFactory.getOWLTopObjectProperty()) && root.equals(owlObjectProperty)) {
      return hierarchyAccessor.getTopChildren(projectId, branchId, ontoDocId);
    } else {
      return hierarchyAccessor.getChildren(owlObjectProperty, projectId, branchId, ontoDocId);
    }
  }

  @Override
  public boolean isLeaf(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.isLeaf(owlObjectProperty, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLObjectProperty> getDescendants(OWLObjectProperty owlObjectProperty) {
    if (root.equals(dataFactory.getOWLTopObjectProperty()) && root.equals(owlObjectProperty)) {
      return getAllObjectProperties();
    } else {
      return hierarchyAccessor.getDescendants(owlObjectProperty, projectId, branchId, ontoDocId);
    }
  }

  @Nonnull
  private ImmutableSet<OWLObjectProperty> getAllObjectProperties() {
    return entityAccessor.getEntitiesByType(EntityType.OBJECT_PROPERTY, projectId, branchId, ontoDocId)
        .stream()
        .collect(ImmutableSet.toImmutableSet());
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

  @Override
  public void handleChanges(List<OntologyChange> list) {
    // Do nothing
  }
}
