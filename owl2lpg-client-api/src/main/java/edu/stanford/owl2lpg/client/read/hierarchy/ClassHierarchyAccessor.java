package edu.stanford.owl2lpg.client.read.hierarchy;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ClassHierarchyAccessor extends HierarchyAccessor<OWLClass> {

  OWLClass getOwlThing();
}
