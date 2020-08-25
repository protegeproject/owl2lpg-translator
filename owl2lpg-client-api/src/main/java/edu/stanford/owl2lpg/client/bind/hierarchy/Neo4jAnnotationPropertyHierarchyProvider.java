package edu.stanford.owl2lpg.client.bind.hierarchy;

import edu.stanford.bmir.protege.web.server.hierarchy.AnnotationPropertyHierarchyProvider;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.hierarchy.AnnotationPropertyHierarchyAccessor;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationPropertyHierarchyProvider implements AnnotationPropertyHierarchyProvider {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AnnotationPropertyHierarchyAccessor hierarchyAccessor;

  @Inject
  public Neo4jAnnotationPropertyHierarchyProvider(@Nonnull AxiomContext axiomContext,
                                                  @Nonnull AnnotationPropertyHierarchyAccessor hierarchyAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.hierarchyAccessor = checkNotNull(hierarchyAccessor);
  }

  @Override
  public Collection<OWLAnnotationProperty> getRoots() {
    return hierarchyAccessor.getRoots(axiomContext);
  }

  @Override
  public Collection<OWLAnnotationProperty> getChildren(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getChildren(owlAnnotationProperty, axiomContext);
  }

  @Override
  public boolean isLeaf(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.isLeaf(owlAnnotationProperty, axiomContext);
  }

  @Override
  public Collection<OWLAnnotationProperty> getDescendants(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getDescendants(owlAnnotationProperty, axiomContext);
  }

  @Override
  public Collection<OWLAnnotationProperty> getParents(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getParents(owlAnnotationProperty, axiomContext);
  }

  @Override
  public Collection<OWLAnnotationProperty> getAncestors(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getAncestors(owlAnnotationProperty, axiomContext);
  }

  @Override
  public Collection<List<OWLAnnotationProperty>> getPathsToRoot(OWLAnnotationProperty owlAnnotationProperty) {
    return hierarchyAccessor.getPathsToRoot(owlAnnotationProperty, axiomContext);
  }

  @Override
  public boolean isAncestor(OWLAnnotationProperty parent, OWLAnnotationProperty child) {
    return hierarchyAccessor.isAncestor(parent, child, axiomContext);
  }
}
