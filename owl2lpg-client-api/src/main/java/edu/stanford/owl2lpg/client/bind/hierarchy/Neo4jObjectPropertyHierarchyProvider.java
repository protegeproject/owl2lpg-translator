package edu.stanford.owl2lpg.client.bind.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyRoot;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.ObjectPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
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
  private final DocumentIdMap documentIdMap;

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
                                              @Nonnull DocumentIdMap documentIdMap,
                                              @Nonnull EntityAccessor entityAccessor,
                                              @Nonnull ObjectPropertyHierarchyAccessor hierarchyAccessor,
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
  public Collection<OWLObjectProperty> getRoots() {
    return ImmutableSet.of(root);
  }

  @Override
  public Collection<OWLObjectProperty> getChildren(OWLObjectProperty owlObjectProperty) {
    if (root.equals(dataFactory.getOWLTopObjectProperty()) && root.equals(owlObjectProperty)) {
      return documentIdMap.get(projectId)
          .stream()
          .flatMap(documentId -> hierarchyAccessor.getTopChildren(projectId, branchId, documentId).stream())
          .collect(ImmutableSet.toImmutableSet());
    } else {
      return documentIdMap.get(projectId)
          .stream()
          .flatMap(documentId -> hierarchyAccessor.getChildren(owlObjectProperty, projectId, branchId, documentId).stream())
          .collect(ImmutableSet.toImmutableSet());
    }
  }

  @Override
  public boolean isLeaf(OWLObjectProperty owlObjectProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .anyMatch(documentId -> hierarchyAccessor.isLeaf(owlObjectProperty, projectId, branchId, documentId));
  }

  @Override
  public Collection<OWLObjectProperty> getDescendants(OWLObjectProperty owlObjectProperty) {
    if (root.equals(dataFactory.getOWLTopObjectProperty()) && root.equals(owlObjectProperty)) {
      return getAllObjectProperties();
    } else {
      return documentIdMap.get(projectId)
          .stream()
          .flatMap(documentId -> hierarchyAccessor.getDescendants(owlObjectProperty, projectId, branchId, documentId).stream())
          .collect(ImmutableSet.toImmutableSet());
    }
  }

  @Nonnull
  private ImmutableSet<OWLObjectProperty> getAllObjectProperties() {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> entityAccessor.getEntitiesByType(EntityType.OBJECT_PROPERTY, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLObjectProperty> getParents(OWLObjectProperty owlObjectProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getParents(owlObjectProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLObjectProperty> getAncestors(OWLObjectProperty owlObjectProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getAncestors(owlObjectProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<List<OWLObjectProperty>> getPathsToRoot(OWLObjectProperty owlObjectProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> hierarchyAccessor.getPathsToRoot(owlObjectProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLObjectProperty parent, OWLObjectProperty child) {
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
