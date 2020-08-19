package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HierarchyAccessor<E extends OWLEntity> {

  Collection<E> getAncestors(E entity, AxiomContext context);

  Collection<E> getDescendants(E entity, AxiomContext context);

  Collection<E> getParents(E entity, AxiomContext context);

  Collection<E> getChildren(E entity, AxiomContext context);

  Collection<List<E>> getPathsToRoot(E entity, AxiomContext context);

  boolean isAncestor(E parent, E child, AxiomContext context);

  boolean isLeaf(E entity, AxiomContext context);
}
