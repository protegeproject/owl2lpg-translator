package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ObjectPropertyHierarchyAccessor extends HierarchyAccessor<OWLObjectProperty> {

  Collection<OWLObjectProperty> getRoots(AxiomContext context);
}
