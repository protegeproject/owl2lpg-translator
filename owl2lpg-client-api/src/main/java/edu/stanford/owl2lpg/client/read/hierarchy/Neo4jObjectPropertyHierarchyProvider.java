package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyProvider;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
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
  private final AxiomContext axiomContext;

  @Nonnull
  private final ObjectPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jObjectPropertyHierarchyProvider(@Nonnull AxiomContext axiomContext,
                                              @Nonnull ObjectPropertyHierarchyAccessor hierarchyAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLObjectProperty> getRoots() {
    return hierarchyAccessor.getRoots(axiomContext);
  }

  @Override
  public Collection<OWLObjectProperty> getChildren(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getChildren(owlObjectProperty, axiomContext);
  }

  @Override
  public boolean isLeaf(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.isLeaf(owlObjectProperty, axiomContext);
  }

  @Override
  public Collection<OWLObjectProperty> getDescendants(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getDescendants(owlObjectProperty, axiomContext);
  }

  @Override
  public Collection<OWLObjectProperty> getParents(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getParents(owlObjectProperty, axiomContext);
  }

  @Override
  public Collection<OWLObjectProperty> getAncestors(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getAncestors(owlObjectProperty, axiomContext);
  }

  @Override
  public Collection<List<OWLObjectProperty>> getPathsToRoot(OWLObjectProperty owlObjectProperty) {
    return hierarchyAccessor.getPathsToRoot(owlObjectProperty, axiomContext);
  }

  @Override
  public boolean isAncestor(OWLObjectProperty parent, OWLObjectProperty child) {
    return hierarchyAccessor.isAncestor(parent, child, axiomContext);
  }
}
