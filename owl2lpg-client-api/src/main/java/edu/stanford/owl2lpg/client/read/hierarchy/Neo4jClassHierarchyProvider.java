package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyProvider;
import edu.stanford.owl2lpg.model.AxiomContext;
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
  private final AxiomContext subClassOfAxiomContext;

  @Nonnull
  private final ClassHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jClassHierarchyProvider(@Nonnull AxiomContext subClassOfAxiomContext,
                                     @Nonnull ClassHierarchyAccessor hierarchyAccessor) {
    this.subClassOfAxiomContext = checkNotNull(subClassOfAxiomContext);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }


  @Override
  public Collection<OWLClass> getRoots() {
    return ImmutableSet.of(hierarchyAccessor.getOwlThing());
  }

  @Override
  public Collection<OWLClass> getChildren(OWLClass owlClass) {
    return hierarchyAccessor.getChildren(subClassOfAxiomContext, owlClass);
  }

  @Override
  public boolean isLeaf(OWLClass owlClass) {
    return hierarchyAccessor.isLeaf(subClassOfAxiomContext, owlClass);
  }

  @Override
  public Collection<OWLClass> getDescendants(OWLClass owlClass) {
    return hierarchyAccessor.getDescendants(subClassOfAxiomContext, owlClass);
  }

  @Override
  public Collection<OWLClass> getParents(OWLClass owlClass) {
    return hierarchyAccessor.getParents(subClassOfAxiomContext, owlClass);
  }

  @Override
  public Collection<OWLClass> getAncestors(OWLClass owlClass) {
    return hierarchyAccessor.getAncestors(subClassOfAxiomContext, owlClass);
  }

  @Override
  public Collection<List<OWLClass>> getPathsToRoot(OWLClass owlClass) {
    return hierarchyAccessor.getPathsToRoot(subClassOfAxiomContext, owlClass);
  }

  @Override
  public boolean isAncestor(OWLClass parent, OWLClass child) {
    return hierarchyAccessor.isAncestor(subClassOfAxiomContext, parent, child);
  }
}
