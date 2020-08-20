package edu.stanford.owl2lpg.client.read.hierarchy;

import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ObjectPropertyHierarchyAccessor extends HierarchyAccessor<OWLObjectProperty> {

  OWLObjectProperty getOwlTopObjectProperty();
}
