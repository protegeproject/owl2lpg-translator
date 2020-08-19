package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyProvider;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
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
  private final AxiomContext axiomContext;

  @Nonnull
  private final ClassHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jClassHierarchyProvider(@Nonnull AxiomContext axiomContext,
                                     @Nonnull ClassHierarchyAccessor hierarchyAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }


  @Override
  public Collection<OWLClass> getRoots() {
    return ImmutableSet.of(hierarchyAccessor.getOwlThing());
  }

  @Override
  public Collection<OWLClass> getChildren(OWLClass owlClass) {
    return hierarchyAccessor.getChildren(owlClass, axiomContext);
  }

  @Override
  public boolean isLeaf(OWLClass owlClass) {
    return hierarchyAccessor.isLeaf(owlClass, axiomContext);
  }

  @Override
  public Collection<OWLClass> getDescendants(OWLClass owlClass) {
    return hierarchyAccessor.getDescendants(owlClass, axiomContext);
  }

  @Override
  public Collection<OWLClass> getParents(OWLClass owlClass) {
    return hierarchyAccessor.getParents(owlClass, axiomContext);
  }

  @Override
  public Collection<OWLClass> getAncestors(OWLClass owlClass) {
    return hierarchyAccessor.getAncestors(owlClass, axiomContext);
  }

  @Override
  public Collection<List<OWLClass>> getPathsToRoot(OWLClass owlClass) {
    return hierarchyAccessor.getPathsToRoot(owlClass, axiomContext);
  }

  @Override
  public boolean isAncestor(OWLClass parent, OWLClass child) {
    return hierarchyAccessor.isAncestor(parent, child, axiomContext);
  }
}
