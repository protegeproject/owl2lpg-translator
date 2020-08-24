package edu.stanford.owl2lpg.client.read.hierarchy;

import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AnnotationPropertyHierarchyAccessor extends HierarchyAccessor<OWLAnnotationProperty> {

  Collection<OWLAnnotationProperty> getRoots(AxiomContext context);
}
