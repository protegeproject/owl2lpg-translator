package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;

import java.util.Collection;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HierarchyAccessor<E> {

  Collection<E> getAncestors(AxiomContext context, E owlClass);

  Collection<E> getDescendants(AxiomContext context, E owlClass);

  Collection<E> getParents(AxiomContext context, E owlClass);

  Collection<E> getChildren(AxiomContext context, E owlClass);

  Collection<List<E>> getPathsToRoot(AxiomContext context, E owlClass);

  boolean isAncestor(AxiomContext context, E parent, E child);

  boolean isLeaf(AxiomContext context, E owlClass);
}
