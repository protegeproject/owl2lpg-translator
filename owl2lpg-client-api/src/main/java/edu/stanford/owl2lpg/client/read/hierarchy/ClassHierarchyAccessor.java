package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLClass;

import java.util.Collection;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ClassHierarchyAccessor {

  OWLClass getOwlThing();

  Collection<OWLClass> getAncestors(AxiomContext context, OWLClass owlClass);

  Collection<OWLClass> getDescendants(AxiomContext context, OWLClass owlClass);

  Collection<OWLClass> getParents(AxiomContext context, OWLClass owlClass);

  Collection<OWLClass> getChildren(AxiomContext context, OWLClass owlClass);

  Collection<List<OWLClass>> getPathsToRoot(AxiomContext context, OWLClass owlClass);

  boolean isAncestor(AxiomContext context, OWLClass parent, OWLClass child);

  boolean isLeaf(AxiomContext context, OWLClass owlClass);
}
