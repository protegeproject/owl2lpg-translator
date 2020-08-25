package edu.stanford.owl2lpg.client.bind.hierarchy;

import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyProvider;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.hierarchy.DataPropertyHierarchyAccessor;
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
  private final AxiomContext axiomContext;

  @Nonnull
  private final DataPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jDataPropertyHierarchyProvider(@Nonnull AxiomContext axiomContext,
                                            @Nonnull DataPropertyHierarchyAccessor hierarchyAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLDataProperty> getRoots() {
    return hierarchyAccessor.getRoots(axiomContext);
  }

  @Override
  public Collection<OWLDataProperty> getChildren(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getChildren(owlDataProperty, axiomContext);
  }

  @Override
  public boolean isLeaf(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.isLeaf(owlDataProperty, axiomContext);
  }

  @Override
  public Collection<OWLDataProperty> getDescendants(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getDescendants(owlDataProperty, axiomContext);
  }

  @Override
  public Collection<OWLDataProperty> getParents(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getParents(owlDataProperty, axiomContext);
  }

  @Override
  public Collection<OWLDataProperty> getAncestors(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getAncestors(owlDataProperty, axiomContext);
  }

  @Override
  public Collection<List<OWLDataProperty>> getPathsToRoot(OWLDataProperty owlDataProperty) {
    return hierarchyAccessor.getPathsToRoot(owlDataProperty, axiomContext);
  }

  @Override
  public boolean isAncestor(OWLDataProperty parent, OWLDataProperty child) {
    return hierarchyAccessor.isAncestor(parent, child, axiomContext);
  }
}
