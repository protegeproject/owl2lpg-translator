package edu.stanford.owl2lpg.client.bind.hierarchy;

import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyProvider;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jClassHierarchyProvider implements ClassHierarchyProvider {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final ClassHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jClassHierarchyProvider(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId,
                                     @Nonnull ClassHierarchyAccessor hierarchyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLClass> getRoots() {
    return hierarchyAccessor.getRoots(projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLClass> getChildren(OWLClass owlClass) {
    return hierarchyAccessor.getChildren(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isLeaf(OWLClass owlClass) {
    return hierarchyAccessor.isLeaf(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLClass> getDescendants(OWLClass owlClass) {
    return hierarchyAccessor.getDescendants(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLClass> getParents(OWLClass owlClass) {
    return hierarchyAccessor.getParents(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<OWLClass> getAncestors(OWLClass owlClass) {
    return hierarchyAccessor.getAncestors(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public Collection<List<OWLClass>> getPathsToRoot(OWLClass owlClass) {
    return hierarchyAccessor.getPathsToRoot(owlClass, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isAncestor(OWLClass parent, OWLClass child) {
    return hierarchyAccessor.isAncestor(parent, child, projectId, branchId, ontoDocId);
  }
}
