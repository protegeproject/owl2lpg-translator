package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyRoot;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.DataPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.semanticweb.owlapi.model.EntityType.DATA_PROPERTY;

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
  private final DocumentIdMap documentIdMap;

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
                                            @Nonnull DocumentIdMap documentIdMap,
                                            @Nonnull EntityAccessor entityAccessor,
                                            @Nonnull DataPropertyHierarchyAccessor hierarchyAccessor,
                                            @Nonnull OWLDataFactory dataFactory) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.entityAccessor = checkNotNull(entityAccessor);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
    this.dataFactory = checkNotNull(dataFactory);
    hierarchyAccessor.setRoot(root);
  }

  @Override
  public Collection<OWLDataProperty> getRoots() {
    return ImmutableSet.of(root);
  }

  @Override
  public Collection<OWLDataProperty> getChildren(OWLDataProperty owlDataProperty) {
    if (root.equals(dataFactory.getOWLTopDataProperty()) && root.equals(owlDataProperty)) {
      return documentIdMap.get(projectId)
          .stream()
          .flatMap(documentId -> hierarchyAccessor.getTopChildren(projectId, branchId, documentId).stream())
          .collect(ImmutableSet.toImmutableSet());
    } else {
      return documentIdMap.get(projectId)
          .stream()
          .flatMap(documentId -> hierarchyAccessor.getChildren(owlDataProperty, projectId, branchId, documentId).stream())
          .collect(ImmutableSet.toImmutableSet());
    }
  }

  @Override
  public boolean isLeaf(OWLDataProperty owlDataProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .anyMatch(documentId -> hierarchyAccessor.isLeaf(owlDataProperty, projectId, branchId, documentId));
  }

  @Override
  public Collection<OWLDataProperty> getDescendants(OWLDataProperty owlDataProperty) {
    if (root.equals(dataFactory.getOWLTopDataProperty()) && root.equals(owlDataProperty)) {
      return getAllDataProperties();
    } else {
      return documentIdMap.get(projectId)
          .stream()
          .flatMap(documentId -> hierarchyAccessor.getDescendants(owlDataProperty, projectId, branchId, documentId).stream())
          .collect(ImmutableSet.toImmutableSet());
    }
  }

  @Nonnull
  private ImmutableSet<OWLDataProperty> getAllDataProperties() {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> entityAccessor.getEntitiesByType(DATA_PROPERTY, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLDataProperty> getParents(OWLDataProperty owlDataProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getParents(owlDataProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLDataProperty> getAncestors(OWLDataProperty owlDataProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getAncestors(owlDataProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<List<OWLDataProperty>> getPathsToRoot(OWLDataProperty owlDataProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getPathsToRoot(owlDataProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLDataProperty parent, OWLDataProperty child) {
    return documentIdMap.get(projectId)
        .stream()
        .anyMatch(documentId -> hierarchyAccessor.isAncestor(parent, child, projectId, branchId, documentId));
  }
//
//  @Override
//  public void handleChanges(List<OntologyChange> list) {
//    // Do nothing
//  }
}
